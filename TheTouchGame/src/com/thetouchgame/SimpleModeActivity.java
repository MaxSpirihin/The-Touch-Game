package com.thetouchgame;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thetouchgame.extraClasses.Finisher;

public class SimpleModeActivity extends Activity implements OnTouchListener {

	// константы расположения
	final int PERCENT_OF_HORIZONTAL_PIXELS = 85;
	final int PERCENT_OF_VERTICAL_PIXELS = 75;
	final int VERTICAL_INDENT = 5;

	// временные константы
	final int PLUS_TO_TIMER = 400;
	final int TICK_PERIOD = 100;
	final int TIME_FOR_GAME = 5000;

	final String PREF_NAME = "TheTouchPref";
	final int DIALOG_RULES = 0;

	TextView tvTimer, tvScore;
	Button btn;
	Random rand;
	RelativeLayout.LayoutParams lpBtn;
	int score;
	RelativeLayout gameLayout;
	CountDownTimer timer;
	MediaPlayer soundOfButtonClick;
	long timerPeriod;
	int skinOfButton;
	Context context;
	Intent intent;
	Vibrator vibra;
	SharedPreferences sPref;
	boolean vibraOn, isFinish;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_mode);

		// обнаруживаем элементы, созданные версткой
		tvTimer = (TextView) findViewById(R.id.timerTextView);
		tvScore = (TextView) findViewById(R.id.scoreTextView);
		gameLayout = (RelativeLayout) findViewById(R.id.simpleModeLayout);

		// задаем численные переменные
		score = 0;
		timerPeriod = TIME_FOR_GAME - PLUS_TO_TIMER;

		// создаем функциональные переменные
		isFinish = false;
		rand = new Random();
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		if (sPref.getBoolean("soundOn", true))
			soundOfButtonClick = MediaPlayer.create(getApplicationContext(),
					R.raw.button_click);
		else
			soundOfButtonClick = MediaPlayer.create(getApplicationContext(),
					R.raw.sound_off);
		vibraOn = sPref.getBoolean("vibrateOn", true);
		skinOfButton = R.drawable.finger_red;
		context = this;
		vibra = (Vibrator) this.context
				.getSystemService(Context.VIBRATOR_SERVICE);
		if (sPref.getBoolean("simpleRules", true))
			showDialog(DIALOG_RULES);

		// создаем кнопку и для начала ставим ее по центру
		btn = new Button(this);
		lpBtn = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lpBtn.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lpBtn.addRule(RelativeLayout.CENTER_VERTICAL);
		btn.setLayoutParams(lpBtn);
		btn.setCompoundDrawablesWithIntrinsicBounds(skinOfButton, 0, 0, 0);
		btn.setBackgroundDrawable(null);
		btn.setOnTouchListener(this);
		gameLayout.addView(btn);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			soundOfButtonClick.start();
			// игра идет
			isGameNow = true;

			if (vibraOn)
				vibra.vibrate(100);

			score++;
			tvScore.setText(this.getResources().getString(R.string.score)
					+ " = " + score);

			lpBtn.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
			lpBtn.addRule(RelativeLayout.CENTER_VERTICAL, 0);
			lpBtn.leftMargin = (int) (rand
					.nextInt(PERCENT_OF_HORIZONTAL_PIXELS)
					* getResources().getDisplayMetrics().widthPixels / 100);
			lpBtn.topMargin = (int) ((rand.nextInt(PERCENT_OF_VERTICAL_PIXELS
					- VERTICAL_INDENT) + VERTICAL_INDENT)
					* getResources().getDisplayMetrics().heightPixels / 100);
			Animation anim = AnimationUtils.loadAnimation(this,
					R.anim.anim_button);
			btn.startAnimation(anim);
			btn.setLayoutParams(lpBtn);

			if (timer != null)
				timer.cancel();
			createTimer(PLUS_TO_TIMER);
		}
		return false;
	}

	private void createTimer(long changeTime) {
		timer = new CountDownTimer(timerPeriod + changeTime, TICK_PERIOD) {

			@Override
			public void onTick(long millisUntilFinished) {
				tvTimer.setText(getResources().getString(R.string.time_left)
						+ " " + millisUntilFinished / 1000 + ","
						+ (millisUntilFinished / 100) % 10);
				timerPeriod = millisUntilFinished;
			}

			@Override
			public void onFinish() {
				isFinish = true;
				Finisher finisher = new Finisher((Activity) context, score,
						"simple");
				finisher.finishGame();
			}
		};
		timer.start();
	}

	// ***************************************************
	// Код, связанный с меню, да дублируется, но блин....

	final int DIALOG_RESTART = 1;
	final int DIALOG_TO_MAIN_MENU = 2;
	final int DIALOG_EXIT = 3;

	public boolean onCreateOptionsMenu(Menu menu) {
		// эти переменные пояснены ниже
		spike = true;
		WeLeave = false;
		menu.add(0, 1, 0, getResources().getString(R.string.restart_in_menu));
		menu.add(0, 2, 0,
				getResources().getString(R.string.to_main_menu_in_menu));
		menu.add(0, 3, 0, getResources().getString(R.string.exit_in_menu));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isFinish) {
			menu.clear();
			menu.add(0, 3, 0, getResources().getString(R.string.exit_in_menu));
		}
		if (timer != null)
			timer.cancel();
		return super.onPrepareOptionsMenu(menu);
	}

	// проблема в том, что при нажатии т.е. для выхода все равно запускается
	// onPanelClosed и создает таймер, следующая переменная решает проблему.
	boolean WeLeave;
	// также игра не должна стартовать, если заходим в меню до старта игры
	boolean isGameNow;

	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {
		WeLeave = true;
		switch (item.getItemId()) {
		case 1:
			showDialog(DIALOG_RESTART);
			break;
		case 2:
			showDialog(DIALOG_TO_MAIN_MENU);
			break;
		case 3:
			showDialog(DIALOG_EXIT);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// этот сраный метод вызывается 2 раза вместо одного, добавил костыль, чтобы
	// из них выполнять это 1 раз. Мне дико стыдно, но что поделать...
	boolean spike;

	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		if ((!WeLeave) && (isGameNow))
			if (spike) {
				onMenuClosed();
				spike = false;
			} else {
				spike = true;
			}
	}

	AlertDialog.Builder adb;

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_RESTART:
			adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(getResources().getString(R.string.restart_in_finish));
			// сообщение
			adb.setMessage(getResources().getString(R.string.are_you_sure)
					+ "\n"
					+ getResources().getString(R.string.progress_will_be_lost));
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(getResources().getString(R.string.yes),
					dialogListenerRestart);
			// кнопка отрицательного ответа
			adb.setNegativeButton(getResources().getString(R.string.no),
					dialogListenerRestart);
			// создаем диалог
			return adb.create();
		case DIALOG_TO_MAIN_MENU:
			adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(getResources().getString(
					R.string.to_main_menu_in_finish));
			// сообщение
			adb.setMessage(getResources().getString(R.string.are_you_sure)
					+ "\n"
					+ getResources().getString(R.string.progress_will_be_lost));
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(getResources().getString(R.string.yes),
					dialogListenerToMainMenu);
			// кнопка отрицательного ответа
			adb.setNegativeButton(getResources().getString(R.string.no),
					dialogListenerToMainMenu);
			// создаем диалог
			return adb.create();
		case DIALOG_EXIT:
			adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(getResources().getString(R.string.exit_in_dialog));
			// сообщение
			// сообщение
			adb.setMessage(getResources().getString(R.string.are_you_sure)
					+ "\n"
					+ getResources().getString(R.string.progress_will_be_lost));
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(getResources().getString(R.string.yes),
					dialogListenerExit);
			// кнопка отрицательного ответа
			adb.setNegativeButton(getResources().getString(R.string.no),
					dialogListenerExit);
			// создаем диалог
			return adb.create();
		case DIALOG_RULES:
			adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(getResources().getString(R.string.menu_simple_mode));
			// сообщение
			// сообщение
			adb.setMessage(getResources().getString(
					R.string.rules_of_simple_mode));
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(getResources().getString(R.string.ok),
					dialogListenerRules);
			// кнопка отрицательного ответа
			// создаем диалог
			adb.setCancelable(false);
			return adb.create();
		}
		return super.onCreateDialog(id);
	}

	DialogInterface.OnClickListener dialogListenerRestart = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				if (timer != null)
					timer.cancel();
				intent = new Intent(context, SimpleModeActivity.class);
				startActivity(intent);
				finish();
			} else {
				WeLeave = false;
				if (isGameNow)
					onMenuClosed();
			}
		}
	};

	DialogInterface.OnClickListener dialogListenerToMainMenu = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				if (timer != null)
					timer.cancel();
				intent = new Intent(context, MainMenuActivity.class);
				startActivity(intent);
				finish();
			} else {
				WeLeave = false;
				if (isGameNow)
					onMenuClosed();
			}
		}
	};

	DialogInterface.OnClickListener dialogListenerExit = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				if (timer != null)
					timer.cancel();
				finish();
			} else {
				WeLeave = false;
				if (isGameNow)
					onMenuClosed();
			}
		}
	};

	DialogInterface.OnClickListener dialogListenerRules = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Editor ed = sPref.edit();
			ed.putBoolean("simpleRules", false);
			ed.commit();
		}
	};

	// действия после закрытия меню
	private void onMenuClosed() {
		if (!isFinish) {
			lpBtn.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
			lpBtn.addRule(RelativeLayout.CENTER_VERTICAL, 0);
			lpBtn.leftMargin = (int) (rand
					.nextInt(PERCENT_OF_HORIZONTAL_PIXELS)
					* getResources().getDisplayMetrics().widthPixels / 100);
			lpBtn.topMargin = (int) ((rand.nextInt(PERCENT_OF_VERTICAL_PIXELS
					- VERTICAL_INDENT) + VERTICAL_INDENT)
					* getResources().getDisplayMetrics().heightPixels / 100);
			Animation anim = AnimationUtils.loadAnimation(context,
					R.anim.anim_button);
			btn.startAnimation(anim);
			btn.setLayoutParams(lpBtn);

			if (timer != null)
				timer.cancel();
			createTimer(0);
		}
	}

	@Override
	public void onBackPressed() {
		this.openOptionsMenu();
	}

	// если пользователь решил выйти на home
	@Override
	protected void onUserLeaveHint() {
		if (timer != null)
			timer.cancel();
		System.exit(0);
		super.onUserLeaveHint();
	}

	// ****************************************
}
