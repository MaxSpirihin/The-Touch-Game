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
import android.util.Log;
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

public class StandardModeActivity extends Activity implements OnTouchListener {

	// константы количества
	final int COUNT_OF_BUTTONS = 15;

	// константы расположения
	final int PERCENT_OF_HORIZONTAL_PIXELS = 85;
	final int PERCENT_OF_VERTICAL_PIXELS = 75;
	final int VERTICAL_INDENT = 5;
	final int HORIZONTAL_SIZE = 11;
	final int VERTICAL_SIZE = 14;

	// временные константы
	final int PLUS_TO_TIMER = 300;
	final int MINUS_TO_TIMER = 2000;
	final int TICK_PERIOD = 100;
	final int TIME_FOR_GAME = 10000;
	final int MIN_INTERVAL_FOR_RANDOM_CHANGE = 1000;
	final int MAX_INTERVAL_FOR_RANDOM_CHANGE = 3000;

	// константа отладки
	final String TAG = "myLogs";
	final int DIALOG_RULES = 0;

	final String PREF_NAME = "TheTouchPref";

	TextView tvTimer, tvScore;
	RelativeLayout gameLayout;
	TheButton[] buttons;
	int score;
	CountDownTimer timer;
	long timerPeriod;
	long timeForRandomChange;
	Button restart, toMainMenu;
	Context context;
	Intent intent;
	MediaPlayer soundOfGreenButtonClick, soundOfRedButtonClick;
	SharedPreferences sPref;
	boolean vibraOn, isFinish;
	Vibrator vibra;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.standard_mode);

		// обнаруживаем элементы, созданные версткой
		tvTimer = (TextView) findViewById(R.id.timerTextView);
		tvScore = (TextView) findViewById(R.id.scoreTextView);
		gameLayout = (RelativeLayout) findViewById(R.id.standardModeLayout);

		// задаем численные переменные
		score = 0;
		timerPeriod = TIME_FOR_GAME - PLUS_TO_TIMER;
		Random rand = new Random();
		timeForRandomChange = timerPeriod
				- rand.nextInt(MAX_INTERVAL_FOR_RANDOM_CHANGE
						- MIN_INTERVAL_FOR_RANDOM_CHANGE)
				- MIN_INTERVAL_FOR_RANDOM_CHANGE;

		// создаем функциональные переменные
		isFinish = false;
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		if (sPref.getBoolean("soundOn", true)) {
			soundOfGreenButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.button_click);
			soundOfRedButtonClick = MediaPlayer.create(getApplicationContext(),
					R.raw.wrong_button_click);
		} else {
			soundOfGreenButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.sound_off);
			soundOfRedButtonClick = MediaPlayer.create(getApplicationContext(),
					R.raw.sound_off);
		}
		vibraOn = sPref.getBoolean("vibrateOn", true);
		context = this;
		vibra = (Vibrator) this.context
				.getSystemService(Context.VIBRATOR_SERVICE);
		if (sPref.getBoolean("standardRules", true))
			showDialog(DIALOG_RULES);

		// создаем массив кнопок, который будет крутиться на экране
		// первые 2 кнопкии задаем вручную
		buttons = new TheButton[COUNT_OF_BUTTONS];
		for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
			buttons[i] = new TheButton(this, i);
			buttons[i].button.setOnTouchListener(this);
		}
		buttons[0].isVisible = true;
		buttons[1].isVisible = true;
		buttons[0].isGreen = true;
		buttons[0].SetToLayout(gameLayout, 20, 20);
		buttons[1].SetToLayout(gameLayout, 50, 50);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// TODO Auto-generated method stub
			isGameNow = true;

			int num = v.getId();
			if (buttons[num].isGreen) {
				score++;
				tvScore.setText(this.getResources().getString(R.string.score)
						+ " = " + score);
				soundOfGreenButtonClick.start();
			} else {
				soundOfRedButtonClick.start();
				if (vibraOn)
					vibra.vibrate(100);
			}

			if (timer != null)
				timer.cancel();

			if (buttons[num].isGreen)
				createTimer(PLUS_TO_TIMER);
			else
				createTimer(-MINUS_TO_TIMER);

			if ((buttons[num].isGreen) || (timerPeriod > MINUS_TO_TIMER)) {
				SetRandomCoordsToAllArray();
				SetAllButtonToLayout();
			}
		}
		return false;
	}

	private void SetRandomCoordsToAllArray() {
		int CountOfButtons, CountOfGreenButtons;
		Random rand = new Random();
		CountOfButtons = rand.nextInt(COUNT_OF_BUTTONS - 1) + 1;
		if (CountOfButtons != 1) {
			CountOfGreenButtons = rand.nextInt(CountOfButtons - 1) + 1;
		} else {
			CountOfGreenButtons = rand.nextInt(CountOfButtons) + 1;
		}

		// Log.d(TAG, CountOfButtons + ":" + CountOfGreenButtons);
		// if (2 * CountOfGreenButtons <= CountOfButtons)
		// Log.d(TAG, "+");

		for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
			buttons[i].isVisible = false;
			buttons[i].isGreen = false;
			if (i < CountOfGreenButtons)
				buttons[i].isGreen = true;
		}

		for (int i = 0; i < CountOfButtons; i++) {
			buttons[i].isVisible = true;
			SetRandomCoords(i);
		}

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

	private void createTimer(long changeTime) {
		timeForRandomChange += changeTime;

		timer = new CountDownTimer(timerPeriod + changeTime, TICK_PERIOD) {

			@Override
			public void onTick(long millisUntilFinished) {
				tvTimer.setText(getResources().getString(R.string.time_left)
						+ " " + millisUntilFinished / 1000 + ","
						+ (millisUntilFinished / 100) % 10);
				timerPeriod = millisUntilFinished;

				if (millisUntilFinished / TICK_PERIOD == timeForRandomChange
						/ TICK_PERIOD) {
					doRandomChange();
					Random rand = new Random();
					timeForRandomChange = timerPeriod
							- rand.nextInt(MAX_INTERVAL_FOR_RANDOM_CHANGE
									- MIN_INTERVAL_FOR_RANDOM_CHANGE)
							- MIN_INTERVAL_FOR_RANDOM_CHANGE;
					Log.d(TAG, "timeFor = " + timeForRandomChange);
				}
			}

			@Override
			public void onFinish() {
				isFinish = true;
				Finisher finisher = new Finisher((Activity) context, score,
						"standard");
				finisher.finishGame();

			}
		};
		timer.start();
	}

	private void doRandomChange() {
		Random rand = new Random();
		int changeMode = rand.nextInt(4);
		Log.d(TAG, "This is Random change in mode " + changeMode);
		switch (changeMode) {
		case 0:
			// режим 1 - инвертирование всех цветов, с проверкой остались ли
			// зеленые.
			boolean HaveGreen = false;
			for (int i = 0; i < COUNT_OF_BUTTONS; i++) {
				if (buttons[i].isVisible) {
					if (buttons[i].isGreen)
						buttons[i].isGreen = false;
					else {
						buttons[i].isGreen = true;
						HaveGreen = true;
					}
					if (!HaveGreen)
						buttons[0].isGreen = true;
				}
			}
			SetAllButtonToLayout();
			break;
		case 1:
			// режим 2-рандомная смена всех цветов (0й пускай будет зеленым)
			for (int i = 1; i < COUNT_OF_BUTTONS; i++) {
				int IsGreen = rand.nextInt(2);
				if (IsGreen == 0)
					buttons[i].isGreen = false;
				else
					buttons[i].isGreen = true;
			}
			SetAllButtonToLayout();
			break;
		default:
			// режим 3-полный сброс, как при нажатии на кнопку
			SetRandomCoordsToAllArray();
			SetAllButtonToLayout();
			break;
		}
	}

	private void SetAllButtonToLayout() {
		gameLayout.removeAllViews();
		gameLayout.addView(tvScore);
		gameLayout.addView(tvTimer);
		for (int i = 0; i < buttons.length; i++)
			buttons[i].SetToLayout(gameLayout);
	}

	private class TheButton {
		public Button button;
		public boolean isVisible;
		public boolean isGreen;
		public int x, y;

		// конструктор
		public TheButton(Context context, int id) {
			button = new Button(context);
			button.setId(id);
			isVisible = false;
			isGreen = false;
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
			if (isGreen)
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_green, 0, 0, 0);
			else
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_red, 0, 0, 0);

			// добавляем только если кнопка видима
			if (isVisible)
				rLay.addView(button);
		}

		// перегруз с заданием координат
		@SuppressWarnings("deprecation")
		public void SetToLayout(RelativeLayout rLay, int X, int Y) {
			x = X;
			y = Y;
			this.SetToLayout(rLay);
		}
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
		//isGameNow = false;
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
		if ((!WeLeave) && (isGameNow)) {
			if (spike) {
				onMenuClosed();
				spike = false;
			} else
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
			adb.setTitle(getResources().getString(R.string.menu_standard_mode));
			// сообщение
			// сообщение
			adb.setMessage(getResources().getString(
					R.string.rules_of_standard_mode));
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
				intent = new Intent(context, StandardModeActivity.class);
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
			ed.putBoolean("standardRules", false);
			ed.commit();
		}
	};


	private void onMenuClosed() {
		if (!isFinish) {
			doRandomChange();
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
