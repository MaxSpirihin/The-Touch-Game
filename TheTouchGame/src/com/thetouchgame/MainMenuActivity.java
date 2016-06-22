package com.thetouchgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.thetouchgame.extraClasses.RecordBuilder;
import com.thetouchgame.extraClasses.RecordsShower;

public class MainMenuActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	final String PREF_NAME = "TheTouchPref";
	final String TAG = "myLogs";
	final int COUNT_OF_MODE = 6;
	final int DIALOG_CLEAR_RECORDS = 1;

	Button toStandard, toChooseMode, toRules, toAbout, toRecords, toBack,
			toSimple, toColor, toCalculator, toMemory, toHardcore, toLeft,
			toRight, clearRecords;
	boolean isMainMenu;
	TextView tvEmail, tvVk;
	Intent intent;
	ToggleButton soundOnOff, vibrateOnOff;
	SharedPreferences sPref;
	String[] modes;
	int mode;
	RecordsShower rShower;
	Context context;

	private void SetMainMenuAndGetAllViews() {
		setContentView(R.layout.main_menu);

		// обнаруживаем кнопки на экране и даем им обработчик
		toStandard = (Button) findViewById(R.id.to_standard_mode);
		toStandard.setOnClickListener(this);
		toChooseMode = (Button) findViewById(R.id.to_choose_mode);
		toChooseMode.setOnClickListener(this);
		toRules = (Button) findViewById(R.id.to_show_rules);
		toRules.setOnClickListener(this);
		toRecords = (Button) findViewById(R.id.to_records);
		toRecords.setOnClickListener(this);
		toAbout = (Button) findViewById(R.id.to_about);
		toAbout.setOnClickListener(this);
		soundOnOff = (ToggleButton) findViewById(R.id.sound_on_off);
		soundOnOff.setOnCheckedChangeListener(this);
		vibrateOnOff = (ToggleButton) findViewById(R.id.vibrate_on_off);
		vibrateOnOff.setOnCheckedChangeListener(this);

		if (LoadBoolFromPref("soundOn")) {
			soundOnOff.setChecked(true);
		} else {
			soundOnOff.setChecked(false);
		}

		if (LoadBoolFromPref("vibrateOn")) {
			vibrateOnOff.setChecked(true);
		} else {
			vibrateOnOff.setChecked(false);
		}
		isMainMenu = true;
	}

	private void SetRecordsMenuAndGetAllViews() {
		rShower = new RecordsShower(this);
		rShower.ShowRecords(modes[mode]);
		toBack = (Button) findViewById(R.id.to_back);
		toBack.setOnClickListener(this);
		toLeft = (Button) findViewById(R.id.to_left);
		toLeft.setOnClickListener(this);
		toRight = (Button) findViewById(R.id.to_right);
		toRight.setOnClickListener(this);
		clearRecords = (Button) findViewById(R.id.clearRecords);
		clearRecords.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SetMainMenuAndGetAllViews();

		// задаем инфу о режимах и стартовый режим
		context = this;
		mode = 0;
		modes = new String[COUNT_OF_MODE];
		modes[0] = "standard";
		modes[1] = "simple";
		modes[2] = "color";
		modes[3] = "calculator";
		modes[4] = "memory";
		modes[5] = "hardcore";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.to_standard_mode:
			intent = new Intent(this, StandardModeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.to_choose_mode:
			setContentView(R.layout.choose_mode);
			isMainMenu = false;

			// oбнаруживаем кнопки
			toSimple = (Button) findViewById(R.id.to_simple_mode);
			toSimple.setOnClickListener(this);
			toColor = (Button) findViewById(R.id.to_color_mode);
			toColor.setOnClickListener(this);
			toCalculator = (Button) findViewById(R.id.to_calculator_mode);
			toCalculator.setOnClickListener(this);
			toMemory = (Button) findViewById(R.id.to_memory_mode);
			toMemory.setOnClickListener(this);
			toHardcore = (Button) findViewById(R.id.to_hardcore_mode);
			toHardcore.setOnClickListener(this);
			toBack = (Button) findViewById(R.id.to_back);
			toBack.setOnClickListener(this);
			break;
		case R.id.to_show_rules:
			setContentView(R.layout.rules);
			isMainMenu = false;

			toBack = (Button) findViewById(R.id.to_back);
			toBack.setOnClickListener(this);
			break;
		case R.id.to_records:
			mode = 0;
			isMainMenu = false;
			this.SetRecordsMenuAndGetAllViews();
			break;
		case R.id.to_about:
			setContentView(R.layout.about);
			isMainMenu = false;

			toBack = (Button) findViewById(R.id.to_back);
			toBack.setOnClickListener(this);
			tvEmail = (TextView) findViewById(R.id.textEMail);
			tvVk = (TextView) findViewById(R.id.textVkPage);
			tvEmail.setOnClickListener(this);
			tvVk.setOnClickListener(this);
			break;
		case R.id.to_back:
			SetMainMenuAndGetAllViews();
			break;
		case R.id.to_simple_mode:
			intent = new Intent(this, SimpleModeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.to_color_mode:
			intent = new Intent(this, ColorModeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.to_calculator_mode:
			intent = new Intent(this, CalculatorModeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.to_memory_mode:
			intent = new Intent(this, MemoryModeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.to_hardcore_mode:
			intent = new Intent(this, HardcoreModeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.to_left:
			if (mode == 0)
				mode = COUNT_OF_MODE - 1;
			else
				mode--;
			this.SetRecordsMenuAndGetAllViews();
			break;
		case R.id.to_right:
			if (mode == COUNT_OF_MODE - 1)
				mode = 0;
			else
				mode++;
			this.SetRecordsMenuAndGetAllViews();
			break;
		case R.id.clearRecords:
			// вызываем диалог
			showDialog(DIALOG_CLEAR_RECORDS);
			break;
		case R.id.textEMail:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maaaks777@mail.ru"));
			startActivity(intent);
			break;
		case R.id.textVkPage:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vk.com/maaaks777"));
			startActivity(intent);
			break;	
		}
	}

	@Override
	public void onBackPressed() {
		if (isMainMenu)
			finish();
		else {
			SetMainMenuAndGetAllViews();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (btn.getId()) {
		case R.id.sound_on_off:
			if (isChecked)
				SaveBoolToPref("soundOn", true);// now on
			else
				SaveBoolToPref("soundOn", false);// now off
			break;
		case R.id.vibrate_on_off:
			if (isChecked)
				SaveBoolToPref("vibrateOn", true);// now on
			else
				SaveBoolToPref("vibrateOn", false);// now off
			break;
		}
	}

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_CLEAR_RECORDS) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(getResources()
					.getString(R.string.clear_records_dialog));
			// сообщение
			adb.setMessage(getResources().getString(R.string.are_you_sure));
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(getResources().getString(R.string.yes),
					dialogListener);
			// кнопка отрицательного ответа
			adb.setNegativeButton(getResources().getString(R.string.no),
					dialogListener);
			// создаем диалог
			return adb.create();
		}
		return super.onCreateDialog(id);
	}

	DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				RecordBuilder rBuilder = new RecordBuilder(context);
				rBuilder.ClearRecords(modes[mode]);
				MainMenuActivity mma = (MainMenuActivity) context;
				mma.SetRecordsMenuAndGetAllViews();
				break;
			// негаитвная кнопка
			case Dialog.BUTTON_NEGATIVE:
				break;
			}
		}
	};

	public void SaveBoolToPref(String name, boolean value) {
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putBoolean(name, value);
		ed.commit();

	}

	public boolean LoadBoolFromPref(String name) {
		sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		return sPref.getBoolean(name, true);
	}

}
