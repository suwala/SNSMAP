package com.example.snsmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.snsmap.R;
import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InsertDB extends DataAbs{

	
	@Override
	public void toData(ContentValues val,String input,Date date,SQLiteDatabase db,ArrayList<OverlayItems> myItem,int hitIndex,int icon) {
		// TODO 自動生成されたメソッド・スタブ
		;
	}

	@Override
	public void toData(ContentValues val, GeoPoint now, String input,
			Date date, SQLiteDatabase db, int hitIndex, int iconNum) {
		// TODO 自動生成されたメソッド・スタブ
		
		//DBへ新規ポイントの書き込み
		val.put("Longitude",now.getLongitudeE6());
		val.put("Latitude",now.getLatitudeE6());
		val.put("MapDate", new SimpleDateFormat("HH':'mm").format(Calendar.getInstance().getTime()));
		val.put("Message", input);
		val.put("Icon", iconNum);
		
		db.insert(sdf1.format(date), null,val);
		Log.d("inser","かきこんだ");
	}


	
}
