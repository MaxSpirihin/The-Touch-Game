package com.thetouchgame;

import java.util.Random;

import com.thetouchgame.extraClasses.Finisher;

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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HardcoreModeActivity extends Activity implements OnTouchListener {

	// константы расположения
	final int PERCENT_OF_HORIZONTAL_PIXELS = 85;
	final int PERCENT_OF_VERTICAL_PIXELS = 75;
	final int VERTICAL_INDENT = 5;

	// временные константы
	final int PLUS_TO_TIMER = 1000;
	final int TICK_PERIOD = 100;
	final int TIME_FOR_GAME = 20000;
	final int TIME_FOR_JUMP = 500;

	final int BUTTON_ID = 23423522;
	final String PREF_NAME = "TheTouchPref";
	final int DIALOG_RULES = 0;

	TextView tvTimer, tvScore;
	Button button;
	RelativeLayout gameLayout;
	RelativeLayout.LayoutParams lpBtn;
	int score;
	int x, y;
	Random rand;
	CountDownTimer timer;
	long timerPeriod;
	boolean isFinish, isGameNow;
	long timeBeforeJump;
	MediaPlayer soundOfButtonClick;
	SharedPreferences sPref;
	Context context;
	Intent intent;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hardcore_mode);

		// обнаруживаем элементы, созданные версткой
		tvTimer = (TextView) findViewById(R.id.timerTextView);
		tvScore = (TextView) findViewById(R.id.scoreTextView);
		gameLayout = (RelativeLayout) findViewById(R.id.hModeLayout);

		// задаем численные переменные
		score = 0;
		timerPeriod = TIME_FOR_GAME - PLUS_TO_TIMER;
		timeBeforeJump = TIME_FOR_JUMP;

		// задаем функциональные переменные
		rand = new Random();
		isGameNow = false;
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		if (sPref.getBoolean("soundOn", true))
			soundOfButtonClick = MediaPlayer.create(getApplicationContext(),
					R.raw.button_click);
		else
			soundOfButtonClick = MediaPlayer.create(getApplicationContext(),
					R.raw.sound_off);
		context = this;
		if (sPref.getBoolean("hardcoreRules", true))
			showDialog(DIALOG_RULES);

		// создаем кнопку и для начала ставим ее по центру
		button = new Button(this);
		button.setId(BUTTON_ID);
		lpBtn = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lpBtn.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lpBtn.addRule(RelativeLayout.CENTER_VERTICAL);
		button.setLayoutParams(lpBtn);
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.finger_red,
				0, 0, 0);
		button.setBackgroundDrawable(null);
		button.setOnTouchListener(this);
		gameLayout.setOnTouchListener(this);
		gameLayout.addView(button);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v.getId() == BUTTON_ID) {
				if (isGameNow)
				{
					score++;
					tvScore.setText(this.getResources().getString(R.string.score) + " = "
							+ score);
				}
				soundOfButtonClick.start();
				isGameNow = true;
				if (timer != null)
					timer.cancel();
				timeBeforeJump = TIME_FOR_JUMP;
				createTimer(PLUS_TO_TIMER);
				buttonJump();
			} else {
				if (timer != null)
					timer.cancel();
				isFinish = true;
				Finisher finisher = new Finisher((Activity) context, score,
						"hardcore");
				finisher.finishGame();
			}

		}
		return false;
	}
	
	
	private void createTimer(long changeTime) {
		timer = new CountDownTimer(timerPeriod + changeTime, TICK_PERIOD) {

			@Override
			public void onTick(long millisUntilFinished) {
				timeBeforeJump-=TICK_PERIOD;
				tvTimer.setText(getResources().getString(R.string.time_left)
						+ " " + millisUntilFinished / 1000 + ","
						+ (millisUntilFinished / 100) % 10);
				timerPeriod = millisUntilFinished;
				if (timeBeforeJump < TICK_PERIOD)
				{
					timeBeforeJump = TIME_FOR_JUMP;
					buttonJump();
				}
			}

			@Override
			public void onFinish() {
				isFinish = true;
				isFinish = true;
				Finisher finisher = new Finisher((Activity) context, score,
						"hardcore");
				finisher.finishGame();
			}
		};
		timer.start();
	}
	
	
	

	private void setBtnToScreen() {
		// создаем нужный layparams, присваиваем его кнопке и добавляем к
		// rLay
		gameLayout.removeView(button);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.leftMargin = (int) (x
				* getResources().getDisplayMetrics().widthPixels / 100);
		lp.topMargin = (int) (y
				* getResources().getDisplayMetrics().heightPixels / 100);
		button.setLayoutParams(lp);
		gameLayout.addView(button);
	}

	private void buttonJump() {
		x = rand.nextInt(PERCENT_OF_HORIZONTAL_PIXELS);
		y = rand.nextInt(PERCENT_OF_VERTICAL_PIXELS - VERTICAL_INDENT)
				+ VERTICAL_INDENT;
		setBtnToScreen();
	}
	
	
	// ***************************************************
		// Код, связанный с меню, да дублируется, но блин....

		final int DIALOG_RESTART = 1;
		final int DIALOG_TO_MAIN_MENU = 2;
		final int DIALOG_EXIT = 3;

		public boolean onCreateOptionsMenu(Menu menu) {
			// эти переменные пояснены ниже
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

		@Override
		public void onPanelClosed(int featureId, Menu menu) {
			if ((!WeLeave) && (isGameNow))
				onMenuClosed();
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
				adb.setTitle(getResources().getString(R.string.menu_hardcore_mode));
				// сообщение
				// сообщение
				adb.setMessage(getResources().getString(
						R.string.rules_of_hardcore_mode));
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
					intent = new Intent(context, HardcoreModeActivity.class);
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
				ed.putBoolean("hardcoreRules", false);
				ed.commit();
			}
		};


		// действия после закрытия меню
		private void onMenuClosed() {
			if (!isFinish) {
				if (timer != null)
					timer.cancel();
				timeBeforeJump = TIME_FOR_JUMP;
				createTimer(0);
				buttonJump();
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