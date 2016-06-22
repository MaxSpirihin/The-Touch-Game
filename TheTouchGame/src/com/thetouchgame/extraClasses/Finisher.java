package com.thetouchgame.extraClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thetouchgame.CalculatorModeActivity;
import com.thetouchgame.ColorModeActivity;
import com.thetouchgame.HardcoreModeActivity;
import com.thetouchgame.MainMenuActivity;
import com.thetouchgame.MemoryModeActivity;
import com.thetouchgame.R;
import com.thetouchgame.SimpleModeActivity;
import com.thetouchgame.StandardModeActivity;

//нужен для окончания игры в любом режиме
public class Finisher implements OnClickListener {

	final String PREF_NAME = "TheTouchPref";
	SharedPreferences sPref;

	Activity context;
	int score;
	String mode;
	MediaPlayer gameOverSound;
	Button restart, toMainMenu, toChangeName, save, cancel, toShowRecords,
			toBack, toLeft, toRight, clearRecords;
	Intent intent;

	EditText enterName;
	RecordBuilder rBuilder;
	int position;
	RecordsShower rShower;

	public Finisher(Activity Cont, int Score, String Mode) {
		context = Cont;
		score = Score;
		mode = Mode;
	}

	public void finishGame() {
		sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		
		rBuilder = new RecordBuilder(context);
		position = rBuilder.SaveResult(mode, score);

		if (sPref.getBoolean("soundOn", true))
		{
			if (position==0)
			gameOverSound = MediaPlayer.create(context.getApplicationContext(),
					R.raw.game_over);
			else
			{
				if (position==1)
				gameOverSound = MediaPlayer.create(context.getApplicationContext(),
						R.raw.great_game_over);
				else
					gameOverSound = MediaPlayer.create(context.getApplicationContext(),
							R.raw.good_game_over);
			}
		}
		else
			gameOverSound = MediaPlayer.create(context.getApplicationContext(),
					R.raw.sound_off);
		gameOverSound.start();


		this.setFinishLayoutAndFindAndFillAllViews();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.to_Main_Menu:
			intent = new Intent(context, MainMenuActivity.class);
			context.startActivity(intent);
			context.finish();
			break;
		case R.id.restart:
			// при добавлении режима не забываем пихать сюда
			switch (mode) {
			case "simple":
				intent = new Intent(context, SimpleModeActivity.class);
				break;
			case "standard":
				intent = new Intent(context, StandardModeActivity.class);
				break;
			case "color":
				intent = new Intent(context, ColorModeActivity.class);
				break;
			case "calculator":
				intent = new Intent(context, CalculatorModeActivity.class);
				break;
			case "hardcore":
				intent = new Intent(context, HardcoreModeActivity.class);
				break;
			case "memory":
				intent = new Intent(context, MemoryModeActivity.class);
				break;
			}
			context.startActivity(intent);
			context.finish();
			break;
		case R.id.toChangeName:
			context.setContentView(R.layout.change_player_name);
			enterName = (EditText) context.findViewById(R.id.edEnterName);
			save = (Button) context.findViewById(R.id.save_name);
			save.setOnClickListener(this);
			cancel = (Button) context.findViewById(R.id.cancel);
			cancel.setOnClickListener(this);
			break;
		case R.id.cancel:
			this.setFinishLayoutAndFindAndFillAllViews();
			break;
		case R.id.save_name:
			if (TextUtils.isEmpty(enterName.getText().toString()))
				Toast.makeText(context, "Field is empty", Toast.LENGTH_SHORT)
						.show();
			else {
				if (position != 0) {
					rBuilder.replaceName(mode, position, enterName.getText()
							.toString());
				}
				rBuilder.ChangePlayerName(enterName.getText().toString());
				this.setFinishLayoutAndFindAndFillAllViews();
			}
			break;
		case R.id.toShowRecords:
			rShower = new RecordsShower(context);
			rShower.ShowRecords(mode);
			toBack = (Button) context.findViewById(R.id.to_back);
			toBack.setOnClickListener(this);
			toLeft = (Button) context.findViewById(R.id.to_left);
			toLeft.setVisibility(View.GONE);
			toRight = (Button) context.findViewById(R.id.to_right);
			toRight.setVisibility(View.GONE);
			clearRecords = (Button) context.findViewById(R.id.clearRecords);
			clearRecords.setVisibility(View.GONE);
			break;
		case R.id.to_back:
			this.setFinishLayoutAndFindAndFillAllViews();
			break;
		}
	}

	// никакими сохранениями метод не занимается
	private void setFinishLayoutAndFindAndFillAllViews() {
		context.setContentView(R.layout.game_over);
		TextView tvScore = (TextView) context.findViewById(R.id.tvScore);
		tvScore.setText(String.valueOf(score));
		restart = (Button) context.findViewById(R.id.restart);
		toChangeName = (Button) context.findViewById(R.id.toChangeName);
		toMainMenu = (Button) context.findViewById(R.id.to_Main_Menu);
		toShowRecords = (Button) context.findViewById(R.id.toShowRecords);
		restart.setOnClickListener(this);
		toChangeName.setOnClickListener(this);
		toMainMenu.setOnClickListener(this);
		toShowRecords.setOnClickListener(this);

		TextView tvBestScore = (TextView) context
				.findViewById(R.id.tvBestScore);
		tvBestScore.setText(context.getResources().getString(
				R.string.best_score)
				+ " " + String.valueOf(rBuilder.GetBestScore(mode)));

		TextView tvYourName = (TextView) context.findViewById(R.id.tvYourName);
		tvYourName.setText(context.getResources().getString(R.string.your_name)
				+ " " + rBuilder.LoadPlayerName());
	}

}
