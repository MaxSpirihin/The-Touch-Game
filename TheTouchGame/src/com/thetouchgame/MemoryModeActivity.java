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

public class MemoryModeActivity extends Activity implements OnTouchListener {

	final String PREF_NAME = "TheTouchPref";
	final int DIALOG_RULES = 0;

	// константы количества
	final int START_COUNT_OF_BUTTONS = 4;
	final int SCORE_FOR_PLUS_BUTTON = 2;
	final int SCORE_BONUS = 3;
	final int MAX_COUNT_OF_BUTTONS = 21;

	// константы расположения
	final int COUNT_OF_POSITION = 23;
	final int COUNT_OF_NUMBERS_IN_ROW = 5;
	final int PERCENT_OF_HORIZONTAL_PIXELS = 85;
	final int PERCENT_OF_VERTICAL_PIXELS = 75;
	final int VERTICAL_INDENT = 10;
	final int HORIZONTAL_SIZE = 17;
	final int VERTICAL_SIZE = 15;

	// временные константы
	final int TIME_FOR_MEMORY = 500;
	final int TIME_FOR_READY = 1000;
	final int TICK_PERIOD = 100;

	TextView tvDoIt, tvScore;
	RelativeLayout gameLayout;
	int score;
	TheButton[] buttons;
	int currentColor;
	int needButtonLeft;
	CountDownTimer timer;
	MediaPlayer soundOfRightButtonClick, soundOfGreatButtonClick;
	SharedPreferences sPref;
	boolean vibraOn, isFinish;
	Vibrator vibra;
	Context context;
	Intent intent;
	int countOfButtons;
	boolean isGameNow;
	int shit;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memory_mode);

		shit = 0;

		// обнаруживаем элементы, созданные версткой
		tvDoIt = (TextView) findViewById(R.id.timerTextView);
		tvScore = (TextView) findViewById(R.id.scoreTextView);
		gameLayout = (RelativeLayout) findViewById(R.id.memModeLayout);

		// задаем численные переменные
		score = 0;
		isGameNow = false;
		countOfButtons = this.FindCountOfButtons();

		// создаем функциональные переменные
		isFinish = false;
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		if (sPref.getBoolean("soundOn", true)) {
			soundOfRightButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.button_click);
			soundOfGreatButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.great_button_click);
		} else {
			soundOfRightButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.sound_off);
			soundOfGreatButtonClick = MediaPlayer.create(
					getApplicationContext(), R.raw.sound_off);
		}
		vibraOn = sPref.getBoolean("vibrateOn", true);
		context = this;
		vibra = (Vibrator) this.context
				.getSystemService(Context.VIBRATOR_SERVICE);
		if (sPref.getBoolean("memoryRules", true))
			showDialog(DIALOG_RULES);

		// создаем массив кнопок, который будет крутиться на экране
		buttons = new TheButton[MAX_COUNT_OF_BUTTONS];
		for (int i = 0; i < MAX_COUNT_OF_BUTTONS; i++) {
			buttons[i] = new TheButton(this, i);
			buttons[i].button.setOnTouchListener(this);
		}

		this.SetAllButtonsToLayoutInNewConfig();

	}

	@Override
	public boolean onTouch(View btn, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int num = btn.getId();

			if (buttons[num].isGreen) {

				if (buttons[num].isVisible == buttons[num].WHITE_VISIBLE) {

					if (needButtonLeft != 0) {
						buttons[num].isVisible = buttons[num].NOT_VISIBLE;
						this.SetAllButtonToLayout();
						needButtonLeft--;
						soundOfRightButtonClick.start();
					} else {
						score++;
						tvScore.setText(this.getResources().getString(
								R.string.score)
								+ " = " + score);
						this.clearbuttons();
						this.createTimerReady();
						soundOfGreatButtonClick.start();
						if (vibraOn)
							vibra.vibrate(100);
					}

				} else {

					if (!isGameNow) {

						Log.d("myLogs", String.valueOf(needButtonLeft));
						if (needButtonLeft != 0) {
							isGameNow = true;
							buttons[num].isVisible = buttons[num].NOT_VISIBLE;
							this.doAllButtonsWhite();
							needButtonLeft--;
							tvDoIt.setText(getResources().getString(
									R.string.standard_mode_begin));
							soundOfRightButtonClick.start();
						} else {
							isGameNow = true;
							score++;
							tvScore.setText(this.getResources().getString(
									R.string.score)
									+ " = " + score);

							this.clearbuttons();
							this.createTimerReady();
							soundOfGreatButtonClick.start();
							if (vibraOn)
								vibra.vibrate(100);
						}

					}

				}

			} else {
				isFinish = true;
				Finisher finisher = new Finisher((Activity) context, score,
						"memory");
				finisher.finishGame();
			}

		}
		return false;

	}

	// в зависимости от очков считает, сколько кнопок делать
	private int FindCountOfButtons() {
		if (this.START_COUNT_OF_BUTTONS + score / this.SCORE_FOR_PLUS_BUTTON <= this.MAX_COUNT_OF_BUTTONS)
			return this.START_COUNT_OF_BUTTONS + score
					/ this.SCORE_FOR_PLUS_BUTTON;
		else
			return this.MAX_COUNT_OF_BUTTONS;

	}

	private void clearbuttons() {
		for (int i = 0; i < countOfButtons; i++) {
			buttons[i].isVisible = buttons[i].NOT_VISIBLE;
		}

		this.SetAllButtonToLayout();
	}

	private void createTimerMemory() {
		timer = new CountDownTimer(TIME_FOR_MEMORY, TICK_PERIOD) {

			@Override
			public void onTick(long millisUntilFinished) {
				tvDoIt.setText(getResources().getString(R.string.memorize));
			}

			@Override
			public void onFinish() {
				doAllButtonsWhite();
				tvDoIt.setText(getResources().getString(
						R.string.standard_mode_begin));
			}
		};
		timer.start();
	}

	private void createTimerReady() {
		timer = new CountDownTimer(TIME_FOR_READY, TICK_PERIOD) {

			@Override
			public void onTick(long millisUntilFinished) {
				tvDoIt.setText(getResources().getString(R.string.be_ready));
			}

			@Override
			public void onFinish() {
				SetAllButtonsToLayoutInNewConfig();
				tvDoIt.setText(getResources().getString(R.string.memorize));
			}
		};
		timer.start();
	}

	private void doAllButtonsWhite() {
		for (int i = 0; i < countOfButtons; i++) {
			if (buttons[i].isVisible == buttons[i].COLOR_VISIBLE)
				buttons[i].isVisible = buttons[i].WHITE_VISIBLE;
		}

		this.SetAllButtonToLayout();
	}

	private void SetAllButtonsToLayoutInNewConfig() {
		countOfButtons = this.FindCountOfButtons();

		Random rand = new Random();
		buttons[0].isGreen = true;
		needButtonLeft = 0;

		// остальным кнопкам задаем цвет рандомом
		for (int i = 1; i < countOfButtons; i++) {
			int color = rand.nextInt(2);
			if (color == 0)
				buttons[i].isGreen = true;
			else
				buttons[i].isGreen = false;
			if (buttons[i].isGreen)
				needButtonLeft++;
		}

		for (int i = 0; i < countOfButtons; i++) {
			buttons[i].isVisible = buttons[i].COLOR_VISIBLE;
			SetRandomCoords(i);
		}

		this.SetAllButtonToLayout();
		if (isGameNow)
			createTimerMemory();
	}

	// задает num-й кнопке случайные координаты так чтобы она не перекрывала
	// оствльные видимые кнопки.
	private void SetRandomCoords(int num) {
		Random rand = new Random();
		buttons[num].position = rand.nextInt(COUNT_OF_POSITION);

		// позиция должна быть уникальной, следующий код добивается этого
		while ((ButtonsAreBlocked(num))
				&& (buttons[num].position < this.COUNT_OF_POSITION)) {
			buttons[num].position++;
		}

		// если впереди не оказалось свободных позиций, идем назад, там есть
		// точно
		if (buttons[num].position < this.COUNT_OF_POSITION) {
			while (ButtonsAreBlocked(num)) {
				buttons[num].position++;
			}
		}

		// позицию вычислили, вычислим координаты
		buttons[num].y = (buttons[num].position / 5) * this.VERTICAL_SIZE
				+ this.VERTICAL_INDENT;
		buttons[num].x = (buttons[num].position % 5) * this.HORIZONTAL_SIZE
				+ ((buttons[num].position / 5) % 2) * (HORIZONTAL_SIZE / 2);

	}

	private boolean ButtonsAreBlocked(int num) {
		boolean result = false;
		for (int i = 0; i < countOfButtons; i++) {
			if ((buttons[i].isVisible != buttons[i].NOT_VISIBLE) && (i != num)
					&& (buttons[i].position == buttons[num].position))
				result = true;
		}
		return result;
	}

	private void SetAllButtonToLayout() {
		gameLayout.removeAllViews();
		gameLayout.addView(tvScore);
		gameLayout.addView(tvDoIt);
		for (int i = 0; i < countOfButtons; i++) {
			buttons[i].SetToLayout(gameLayout);
		}
	}

	private class TheButton {
		final int NOT_VISIBLE = 0;
		final int COLOR_VISIBLE = 1;
		final int WHITE_VISIBLE = 2;

		public Button button;
		public boolean isGreen;
		public int position;
		public int x, y;
		int isVisible;

		// конструктор
		public TheButton(Context context, int id) {
			button = new Button(context);
			button.setId(id);
			isGreen = true;
			isVisible = NOT_VISIBLE;
			x = 0;
			y = 0;
			position = 0;
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

			if (isVisible == WHITE_VISIBLE)
				button.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.finger_white, 0, 0, 0);
			else {
				if (isGreen)
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.finger_green, 0, 0, 0);
				else
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.finger_red, 0, 0, 0);
			}
			// добавляем только если кнопка видима
			if (isVisible != NOT_VISIBLE)
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
			adb.setTitle(getResources().getString(R.string.menu_memory_mode));
			// сообщение
			// сообщение
			adb.setMessage(getResources().getString(
					R.string.rules_of_memory_mode));
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
				intent = new Intent(context, MemoryModeActivity.class);
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
			ed.putBoolean("memoryRules", false);
			ed.commit();
		}
	};

	// действия после закрытия меню
	private void onMenuClosed() {
		if (!isFinish) {
			this.clearbuttons();
			createTimerReady();
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