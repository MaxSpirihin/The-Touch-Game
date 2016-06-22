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
import android.graphics.Color;
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

import com.thetouchgame.extraClasses.Finisher;

public class ColorModeActivity extends Activity implements OnTouchListener {

	final String PREF_NAME = "TheTouchPref";
	final int DIALOG_RULES = 0;

	// константы количества
	final int COUNT_OF_BUTTONS = 15;
	final int COUNT_OF_COLOR = 5;
	final int SCORE_BONUS = 3;
	final int RED = 0;
	final int GREEN = 1;
	final int BLUE = 2;
	final int YELLOW = 3;
	final int WHITE = 4;

	// константы расположения
	final int PERCENT_OF_HORIZONTAL_PIXELS = 85;
	final int PERCENT_OF_VERTICAL_PIXELS = 75;
	final int VERTICAL_INDENT = 10;
	final int HORIZONTAL_SIZE = 14;
	final int VERTICAL_SIZE = 15;

	// временные константы
	final int TICK_PERIOD = 100;
	final int TIME_FOR_GAME = 30000;

	TextView tvTimer, tvScore, tvColor;
	RelativeLayout gameLayout;
	int score;
	TheButton[] buttons;
	int currentColor;
	int needButtonLeft;
	long timerPeriod;
	CountDownTimer timer;
	MediaPlayer soundOfRightButtonClick, soundOfWrongButtonClick,
			soundOfGreatButtonClick;
	SharedPreferences sPref;
	boolean vibraOn, isFinish;
	Vibrator vibra;
	Context context;
	Intent intent;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_mode);

		// обнаруживаем элементы, созданные версткой
		tvTimer = (TextView) findViewById(R.id.timerTextView);
		tvScore = (TextView) findViewById(R.id.scoreTextView);
		tvColor = (TextView) findViewById(R.id.colorTextView);
		gameLayout = (RelativeLayout) findViewById(R.id.colorModeLayout);

		// задаем численные переменные
		score = 0;
		timerPeriod = TIME_FOR_GAME;

		// создаем функциональные переменные
		isFinish = false;
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		if (sPref.getBoolean("soundOn", true)) {
			soundOfRightButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.button_click);
			soundOfWrongButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.wrong_button_click);
			soundOfGreatButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.great_button_click);
		} else {
			soundOfRightButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.sound_off);
			soundOfWrongButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.sound_off);
			soundOfGreatButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.sound_off);
		}
		vibraOn = sPref.getBoolean("vibrateOn", true);
		context = this;
		vibra = (Vibrator) this.context
				.getSystemService(Context.VIBRATOR_SERVICE);
		if (sPref.getBoolean("colorRules", true))
			showDialog(DIALOG_RULES);

		// создаем массив кнопок, который будет крутиться на экране
		buttons = new TheButton[COUNT_OF_BUTTONS];
		for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
			buttons[i] = new TheButton(this, i, GREEN);
			buttons[i].button.setOnTouchListener(this);
		}

		this.SetAllButtonsToLayoutInNewConfig();

	}

	@Override
	public boolean onTouch(View btn, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// TODO Auto-generated method stub
			isGameNow = true;
			if (timer == null)
				createTimer();
			int num = btn.getId();
			// Log.d("myLogs", num + ",color = " + buttons[num].color
			// + ".currentcolor = " + currentColor + ".needb = "
			// + needButtonLeft);
			if (buttons[num].color == currentColor) {
				if (needButtonLeft != 0) {
					score++;
					tvScore.setText(this.getResources().getString(
							R.string.score)
							+ " = " + score);
					buttons[num].isVisible = false;
					this.SetAllButtonToLayout();
					needButtonLeft--;
					soundOfRightButtonClick.start();
				} else // если была нажата последняя нужная кнопка
				{
					score += SCORE_BONUS;
					tvScore.setText(this.getResources().getString(
							R.string.score)
							+ " = " + score);
					soundOfGreatButtonClick.start();
					this.SetAllButtonsToLayoutInNewConfig();
				}
			} else {
				soundOfWrongButtonClick.start();
				this.SetAllButtonsToLayoutInNewConfig();

				if (vibraOn)
					vibra.vibrate(100);
			}
		}
		return false;
	}

	private void createTimer() {
		timer = new CountDownTimer(timerPeriod, TICK_PERIOD) {

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
						"color");
				finisher.finishGame();
			}
		};
		timer.start();
	}

	private void SetAllButtonsToLayoutInNewConfig() {
		Random rand = new Random();

		// задаем цвета первым COUNT_OF_COLORS кнопкам
		for (int i = 0; i < COUNT_OF_COLOR; i++) {
			buttons[i].color = i;// i - это id цвета из констант.
		}

		// случайно задаем текущий цвет, а также сосчитаем кол-во нужных кнопок
		currentColor = rand.nextInt(COUNT_OF_COLOR);
		needButtonLeft = 0;

		// пишем текст
		switch (currentColor) {
		case RED:
			tvColor.setTextColor(Color.RED);
			tvScore.setTextColor(Color.RED);
			tvTimer.setTextColor(Color.RED);
			tvColor.setText(getResources().getString(R.string.press_red));
			break;
		case GREEN:
			tvColor.setTextColor(Color.GREEN);
			tvScore.setTextColor(Color.GREEN);
			tvTimer.setTextColor(Color.GREEN);
			tvColor.setText(getResources().getString(R.string.press_green));
			break;
		case BLUE:
			tvColor.setTextColor(Color.BLUE);
			tvScore.setTextColor(Color.BLUE);
			tvTimer.setTextColor(Color.BLUE);
			tvColor.setText(getResources().getString(R.string.press_blue));
			break;
		case YELLOW:
			tvColor.setTextColor(Color.YELLOW);
			tvScore.setTextColor(Color.YELLOW);
			tvTimer.setTextColor(Color.YELLOW);
			tvColor.setText(getResources().getString(R.string.press_yellow));
			break;
		case WHITE:
			tvColor.setTextColor(Color.WHITE);
			tvScore.setTextColor(Color.WHITE);
			tvTimer.setTextColor(Color.WHITE);
			tvColor.setText(getResources().getString(R.string.press_white));
			break;
		}

		// остальным кнопкам задаем цвет рандомом
		for (int i = COUNT_OF_COLOR; i < COUNT_OF_BUTTONS; i++) {
			buttons[i].color = rand.nextInt(COUNT_OF_COLOR);
			if (buttons[i].color == currentColor)
				needButtonLeft++;
		}

		for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
			buttons[i].isVisible = true;
			SetRandomCoords(i);
		}

		this.SetAllButtonToLayout();
	}

	// задает num-й кнопке случайные координаты так чтобы она не перекрывала
	// оствльные видимые кнопки.
	private void SetRandomCoords(int num) {
		Random rand = new Random();
		buttons[num].x = rand.nextInt(PERCENT_OF_HORIZONTAL_PIXELS);
		buttons[num].y = rand.nextInt(PERCENT_OF_VERTICAL_PIXELS
				- VERTICAL_INDENT)
				+ VERTICAL_INDENT;

		while (ButtonsAreBlocked(num)) {
			buttons[num].x = rand.nextInt(PERCENT_OF_HORIZONTAL_PIXELS);
			buttons[num].y = rand.nextInt(PERCENT_OF_VERTICAL_PIXELS
					- VERTICAL_INDENT)
					+ VERTICAL_INDENT;
		}

	}

	private boolean ButtonsAreBlocked(int num) {
		boolean result = false;
		for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
			if ((buttons[i].isVisible) && (i != num)
					&& (buttons[num].x > buttons[i].x - HORIZONTAL_SIZE)
					&& (buttons[num].x < buttons[i].x + HORIZONTAL_SIZE)
					&& (buttons[num].y > buttons[i].y - VERTICAL_SIZE)
					&& (buttons[num].y < buttons[i].y + VERTICAL_SIZE))
				result = true;
		}
		return result;
	}

	private void SetAllButtonToLayout() {
		gameLayout.removeAllViews();
		gameLayout.addView(tvScore);
		gameLayout.addView(tvTimer);
		gameLayout.addView(tvColor);
		for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
			// Log.d("myLogs",i+",x = "+buttons[i].x+",y = "+buttons[i].y+",color = "+buttons[i].color);
			buttons[i].SetToLayout(gameLayout);
		}
	}

	private class TheButton {
		public Button button;
		public int color;
		public int x, y;
		boolean isVisible;

		// конструктор
		public TheButton(Context context, int id, int Color) {
			button = new Button(context);
			button.setId(id);
			color = Color;
			isVisible = true;
			x = 0;
			y = 0;
		}

		@SuppressWarnings("deprecation")
		public void SetToLayout(RelativeLayout rLay) {
			// создаем нужный layparams, присваиваем его кнопке и добавляем к
			// rLay
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.leftMargin = (int) (x
					* getResources().getDisplayMetrics().widthPixels / 100);
			lp.topMargin = (int) (y
					* getResources().getDisplayMetrics().heightPixels / 100);
			button.setLayoutParams(lp);
			button.setBackgroundDrawable(null);
			switch (color) {
			case RED:
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_red, 0, 0, 0);
				break;
			case GREEN:
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_green, 0, 0, 0);
				break;
			case BLUE:
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_blue, 0, 0, 0);
				break;
			case YELLOW:
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_yellow, 0, 0, 0);
				break;
			case WHITE:
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_white, 0, 0, 0);
				break;
			}
			// добавляем только если кнопка видима
			if (isVisible)
				rLay.addView(button);
		}

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
			adb.setTitle(getResources().getString(R.string.menu_color_mode));
			// сообщение
			// сообщение
			adb.setMessage(getResources().getString(
					R.string.rules_of_color_mode));
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
				intent = new Intent(context, ColorModeActivity.class);
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
			ed.putBoolean("colorRules", false);
			ed.commit();
		}
	};


	// действия после закрытия меню
	private void onMenuClosed() {
		if (!isFinish) {
			this.SetAllButtonsToLayoutInNewConfig();
			if (timer != null)
				timer.cancel();
			createTimer();
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
