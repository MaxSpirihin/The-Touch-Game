package com.thetouchgame.extraClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RecordBuilder {

	final String PREF_NAME = "TheTouchPref";
	final int COUNT_OF_RECORDS = 5;

	SharedPreferences sPref;
	Context context;

	public RecordBuilder(Context cont) {
		context = cont;
		sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	public void LoadRecords(String mode, String[] recordsName,
			int[] recordsScore) {
		for (int i = 0; i < COUNT_OF_RECORDS; i++) {
			recordsName[i] = sPref.getString(mode + "name" + String.valueOf(i),
					"-");
			recordsScore[i] = sPref.getInt(mode + "score" + String.valueOf(i),
					0);
		}
	}

	public int SaveResult(String mode, int score) {
		String[] recordsName = new String[COUNT_OF_RECORDS];
		int[] recordsScore = new int[COUNT_OF_RECORDS];

		this.LoadRecords(mode, recordsName, recordsScore);
		int position = this
				.PasteToArray(recordsName, recordsScore, this.LoadPlayerName(), score);
		this.SaveAllArrayToPref(mode, recordsName, recordsScore);
		return position + 1;
	}

	public void replaceName(String mode, int position, String newName) {

		Editor ed = sPref.edit();
		ed.putString(mode + "name" + String.valueOf(position - 1), newName);
		ed.commit();
	}

	public int GetBestScore(String mode) {
		return sPref.getInt(mode + "score" + String.valueOf(0), 0);
	}
	
	public String LoadPlayerName()
	{
		return sPref.getString("playerName", "Player");
	}
	
	
	public void ChangePlayerName(String name)
	{
		Editor ed = sPref.edit();
		ed.putString("playerName", name);
		ed.commit();
	}

	public void ClearRecords(String mode) {
		Editor ed = sPref.edit();
		for (int i = 0; i < COUNT_OF_RECORDS; i++) {
			ed.putString(mode + "name" + String.valueOf(i), "-");
			ed.putInt(mode + "score" + String.valueOf(i), 0);
		}
		ed.commit();
	}

	private void SaveAllArrayToPref(String mode, String[] recordsName,
			int[] recordsScore) {
		Editor ed = sPref.edit();
		for (int i = 0; i < COUNT_OF_RECORDS; i++) {
			ed.putString(mode + "name" + String.valueOf(i), recordsName[i]);
			ed.putInt(mode + "score" + String.valueOf(i), recordsScore[i]);
		}
		ed.commit();
	}

	private int PasteToArray(String[] recordsName, int[] recordsScore,
			String name, int score) {
		if (score <= recordsScore[COUNT_OF_RECORDS - 1])
			return -1; // рекорда нет
		else {
			int key = 0;
			while ((score < recordsScore[key]) && (key < COUNT_OF_RECORDS))
				key++;

			while ((score == recordsScore[key]) && (key < COUNT_OF_RECORDS))
				key++;

			// позиция найдена, осуществляем смещение
			for (int i = COUNT_OF_RECORDS - 1; i > key; i--) {
				recordsScore[i] = recordsScore[i - 1];
				recordsName[i] = recordsName[i - 1];
			}

			// вставляем куда надо
			if (key < COUNT_OF_RECORDS) {
				recordsName[key] = name;
				recordsScore[key] = score;
			}
			return key;
		}
	}

}
