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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CalculatorModeActivity extends Activity implements OnClickListener {

	final String PREF_NAME = "TheTouchPref";
	final int DIALOG_DIFFICULTY = 0;
	final int DIALOG_RULES = 5;

	// константы количества
	final int EASY_RANGE = 20;
	final int NORMAL_RANGE = 40;
	final int HARD_RANGE = 80;
	final int EASY_COUNT_OF_BUTTONS = 5;
	final int NORMAL_COUNT_OF_BUTTONS = 7;
	final int HARD_COUNT_OF_BUTTONS = 10;
	final int EASY_COST_OF_MISTAKE = 2000;
	final int NORMAL_COST_OF_MISTAKE = 800;
	final int HARD_COST_OF_MISTAKE = 300;

	// константы расположения
	final int PERCENT_OF_HORIZONTAL_PIXELS = 80;
	final int PERCENT_OF_VERTICAL_PIXELS = 75;
	final int VERTICAL_INDENT = 10;
	final int HORIZONTAL_SIZE = 19;
	final int VERTICAL_SIZE = 18;

	// временные константы
	final int TICK_PERIOD = 100;
	final int TIME_FOR_GAME = 30000;
	final int EASY_TIME_FOR_DECISION = 2000;
	final int NORMAL_TIME_FOR_DECISION = 3000;
	final int HARD_TIME_FOR_DECISION = 4000;

	TextView tvTimer, tvScore, tvTask;
	RelativeLayout gameLayout;
	int score;
	TheButton[] buttons;
	int solution;
	int mistakes;
	long timerPeriod, timePassed;
	CountDownTimer timer;
	int difficulty;
	int range, countOfButtons, timeForDecision, costOfMistake;
	SharedPreferences sPref;
	boolean vibraOn, isFinish;
	Vibrator vibra;
	Context context;
	Intent intent;
	MediaPlayer soundOfRightButtonClick, soundOfWrongButtonClick;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculator_mode);
		
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		// нужно выбрать сложность
		showDialog(DIALOG_DIFFICULTY);

		if (sPref.getBoolean("calculatorRules", true))
			showDialog(DIALOG_RULES);

	

		// обнаруживаем элементы, созданные версткой
		tvTimer = (TextView) findViewById(R.id.timerTextView);
		tvScore = (TextView) findViewById(R.id.scoreTextView);
		tvTask = (TextView) findViewById(R.id.taskTextView);
		gameLayout = (RelativeLayout) findViewById(R.id.colorModeLayout);
		gameLayout.removeAllViews();

		context = this;

		// остатки перенесены в обработчик диалога
	}

	@Override
	public void onClick(View btn) {
		// TODO Auto-generated method stub
		isGameNow = true;
		int num = btn.getId();
		if (buttons[num].number == solution) {
			score++;
			soundOfRightButtonClick.start();
			tvScore.setText(this.getResources().getString(R.string.score)
					+ " = " + score);
			if (timer != null)
				timer.cancel();
			if (timeForDecision > timePassed)
				createTimer(timeForDecision - timePassed);
			else
				createTimer(0);
		} else {
			mistakes++;
			if (timer != null)
				timer.cancel();
			createTimer(-mistakes * costOfMistake);

			soundOfWrongButtonClick.start();
			if (vibraOn)
				vibra.vibrate(100);
		}
		timePassed = 0;
		this.SetAllButtonsToLayoutInNewConfig();

	}

	private void createTimer(long changeTime) {
		timer = new CountDownTimer(timerPeriod + changeTime, TICK_PERIOD) {

			@Override
			public void onTick(long millisUntilFinished) {
				timePassed += TICK_PERIOD;
				tvTimer.setText(getResources().getString(R.string.time_left)
						+ " " + millisUntilFinished / 1000 + ","
						+ (millisUntilFinished / 100) % 10);
				timerPeriod = millisUntilFinished;
			}

			@Override
			public void onFinish() {
				isFinish = true;
				Finisher finisher = new Finisher((Activity) context, score,
						"calculator");
				finisher.finishGame();
			}
		};
		timer.start();
	}

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DIFFICULTY:
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(getResources()
					.getString(R.string.menu_calculator_mode));
			// сообщение
			adb.setMessage(getResources().getString(R.string.choose_difficulty));
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(
					getResources().getString(R.string.hard_difficulty),
					dialogListener);
			// кнопка отрицательного ответа
			adb.setNegativeButton(
					getResources().getString(R.string.easy_difficulty),
					dialogListener);
			// кнопка нейтрального ответа
			adb.setNeutralButton(
					getResources().getString(R.string.normal_difficulty),
					dialogListener);
			// создаем диалог
			adb.setCancelable(false);
			return adb.create();
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
			adb.setTitle(getResources().getString(R.string.menu_calculator_mode));
			// сообщение
			// сообщение
			adb.setMessage(getResources().getString(
					R.string.rules_of_calculator_mode));
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

	DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				range = HARD_RANGE;
				countOfButtons = HARD_COUNT_OF_BUTTONS;
				timeForDecision = HARD_TIME_FOR_DECISION;
				timePassed = timeForDecision;
				costOfMistake = HARD_COST_OF_MISTAKE;
				break;
			// негаитвная кнопка
			case Dialog.BUTTON_NEGATIVE:
				range = EASY_RANGE;
				countOfButtons = EASY_COUNT_OF_BUTTONS;
				timeForDecision = EASY_TIME_FOR_DECISION;
				timePassed = timeForDecision;
				costOfMistake = EASY_COST_OF_MISTAKE;
				break;
			// нейтральная кнопка
			case Dialog.BUTTON_NEUTRAL:
				range = NORMAL_RANGE;
				countOfButtons = NORMAL_COUNT_OF_BUTTONS;
				timeForDecision = NORMAL_TIME_FOR_DECISION;
				timePassed = timeForDecision;
				costOfMistake = NORMAL_COST_OF_MISTAKE;
				break;
			}
			// задаем численные переменные
			score = 0;
			mistakes = 0;
			timerPeriod = TIME_FOR_GAME;

			// создаем массив кнопок, который будет крутиться на экране
			buttons = new TheButton[countOfButtons];
			for (int i = 0; i < countOfButtons; i++) {
				buttons[i] = new TheButton(context, i);
				buttons[i].button.setOnClickListener((OnClickListener) context);
			}

			// создаем функциональные переменные
			isFinish = false;
			if (sPref.getBoolean("soundOn", true)) {
				soundOfRightButtonClick = MediaPlayer.create(
						getApplicationContext(), R.raw.button_click);
				soundOfWrongButtonClick = MediaPlayer.create(
						getApplicationContext(), R.raw.wrong_button_click);
			} else {
				soundOfRightButtonClick = MediaPlayer.create(
						getApplicationContext(), R.raw.sound_off);
				soundOfWrongButtonClick = MediaPlayer.create(
						getApplicationContext(), R.raw.sound_off);
			}
			vibraOn = sPref.getBoolean("vibrateOn", true);
			vibra = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);

			SetAllButtonsToLayoutInNewConfig();
		}
	};

	private void SetAllButtonsToLayoutInNewConfig() {
		final int COUNT_OF_MODE = 4;
		final int PLUS = 0;
		final int MINUS = 1;
		final int MULTIPLY = 2;
		final int DIVIDE = 3;
		int num1 = 0, num2 = 0;
		String symbol = "";

		Random rand = new Random();

		// генерим примерчик
		int mode = rand.nextInt(COUNT_OF_MODE);
		switch (mode) {
		case PLUS:
			num1 = rand.nextInt(range - 1);
			num2 = rand.nextInt(range - num1);
			solution = num1 + num2;
			symbol = "+";
			break;
		case MINUS:
			num1 = rand.nextInt(range) + 1;
			num2 = rand.nextInt(num1);
			solution = num1 - num2;
			symbol = "-";
			break;
		case MULTIPLY:
			num1 = rand.nextInt(range / 3) + 1;
			num2 = rand.nextInt(range / num1);
			solution = num1 * num2;
			symbol = "*";
			break;
		case DIVIDE:
			solution = rand.nextInt(range / 3) + 2;
			num2 = rand.nextInt(range / solution) + 1;
			num1 = solution * num2;
			symbol = "/";
			break;
		}
		tvTask.setText(String.valueOf(num1) + " " + symbol + " "
				+ String.valueOf(num2) + " =");

		// первой кнопке верное решение
		buttons[0].number = solution;// i - это id цвета из констант.

		// случайно задаем цифры остальных
		for (int i = 1; i < countOfButtons; i++) {
			buttons[i].number = rand.nextInt(range);
		}

		for (int i = 0; i < countOfButtons; i++) {
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
		for (int i = 0; i < countOfButtons; i++) {
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
		gameLayout.addView(tvTask);
		for (int i = 0; i < countOfButtons; i++) {
			// Log.d("myLogs",i+",x = "+buttons[i].x+",y = "+buttons[i].y+",color = "+buttons[i].color);
			buttons[i].SetToLayout(gameLayout);
		}
	}

	private class TheButton {
		public Button button;
		public int number;
		public int x, y;
		boolean isVisible;

		// конструктор
		public TheButton(Context context, int id) {
			button = new Button(context);
			button.setId(id);
			number = 0;
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
			button.setTextColor(getResources().getColor(R.color.text_color));

			// button.setCompoundDrawablesWithIntrinsicBounds(
			// R.drawable.finger_white, 0, 0, 0);
			button.setTextSize(30);
			button.setText(String.valueOf(number));

			// добавляем только если кнопка видима
			if (isVisible)
				rLay.addView(button);
		}

	}

	// ***************************************************
	// Код, связанный с меню, да дублируется, но блин....
	// здесь немного не так, не копируйте

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

	DialogInterface.OnClickListener dialogListenerRestart = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				if (timer != null)
					timer.cancel();
				intent = new Intent(context, CalculatorModeActivity.class);
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
			ed.putBoolean("calculatorRules", false);
			ed.commit();
		}
	};

	// действия после закрытия меню
	private void onMenuClosed() {
		if (!isFinish) {
			this.SetAllButtonsToLayoutInNewConfig();
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