package com.thetouchgame.extraClasses;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.thetouchgame.R;

public class RecordsShower {
	
	final int COUNT_OF_RECORDS = 5;
	final String T="myLogs";

	Activity context;
	
	public RecordsShower(Activity cont)
	{
		context = cont;
	}
	
	public void ShowRecords(String mode)
	{
		context.setContentView(R.layout.records);
		
		//напишем название режима
		TextView tvModeName = (TextView) context.findViewById(R.id.tvRecordsMode);
		switch (mode) {
		case "standard":
			tvModeName.setText(context.getResources().getString(R.string.records_in_standard));
			break;
		case "simple":
			tvModeName.setText(context.getResources().getString(R.string.records_in_simple));
			break;
		case "color":
			tvModeName.setText(context.getResources().getString(R.string.records_in_color));
			break;
		case "calculator":
			tvModeName.setText(context.getResources().getString(R.string.records_in_calculator));
			break;
		case "hardcore":
			tvModeName.setText(context.getResources().getString(R.string.records_in_hardcore));
			break;
		case "memory":
			tvModeName.setText(context.getResources().getString(R.string.records_in_memory));
			break;
		}
		
		//занесем в массив все нужные textview. Да это тупо, но что поделать
			TextView[] tvScore,tvName;
			tvScore = new TextView[COUNT_OF_RECORDS];
			tvName = new TextView[COUNT_OF_RECORDS];
			
			tvScore[0] = (TextView) context.findViewById(R.id.score1);
			tvScore[1] = (TextView) context.findViewById(R.id.score2);
			tvScore[2] = (TextView) context.findViewById(R.id.score3);
			tvScore[3] = (TextView) context.findViewById(R.id.score4);
			tvScore[4] = (TextView) context.findViewById(R.id.score5);
			
			tvName[0] = (TextView) context.findViewById(R.id.name1);
			tvName[1] = (TextView) context.findViewById(R.id.name2);
			tvName[2] = (TextView) context.findViewById(R.id.name3);
			tvName[3] = (TextView) context.findViewById(R.id.name4);
			tvName[4] = (TextView) context.findViewById(R.id.name5);
				
			
			//теперь надо загрузить рекорды
			RecordBuilder rBuilder = new RecordBuilder(context);
			String[] recordsName = new String[COUNT_OF_RECORDS];
			int[] recordsScore = new int[COUNT_OF_RECORDS];
			rBuilder.LoadRecords(mode, recordsName, recordsScore);
			
			
			//ну и засунем рекорды в табличку
			for (int i=0;i<COUNT_OF_RECORDS;i++)
			{

				tvScore[i].setText(String.valueOf(recordsScore[i]));
				tvName[i].setText(recordsName[i]);
			}
			
	}
}
