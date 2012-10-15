package com.example.snsmap;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHepler extends SQLiteOpenHelper {
	
	private static final Integer VERSION = 8;
	private static final CursorFactory FACTORY = null;
	private static final String NAME = "MapTest.db";
	private String date;
	
	public DBHepler(Context context,String date) {
		super(context, NAME, FACTORY, VERSION);
		// TODO 自動生成されたコンストラクター・スタブ
		
		this.date = date;
	}
	
	public DBHepler(Context context) {
		super(context, NAME, FACTORY, VERSION);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//データベースが存在しない場合のみ呼ばれる？
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
		Log.d("dbhoncre","databaseのヴァージョンアップ");
		db.execSQL("create table "+ this.date +"("+
				"_id integer primary key autoincrement,"+
				" Longitude integer not null,"+
				" Latitude integer not null,"+
				" MapDate String,"+
				" Message String,"+
				" Icon integer"+
				");"
		);
		
	}

	
	//Varを上げると実行される
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO 自動生成されたメソッド・スタブ
		
		if(arg1 < 8 && arg2 == 8){
			Log.d("dbh","databaseのヴァージョンアップ");
			/*db.execSQL("create table if not exists "+ date +"("+
					"_id integer primary key autoincrement,"+
					" Longitude integer not null,"+
					" Latitude integer not null,"+
					");"
			);
			*/
			
			db.execSQL("alter table "+this.date+" add column MapDate string");
			db.execSQL("alter table "+this.date+" add column Message string");
			db.execSQL("alter table "+this.date+" add column Icon integer");
		}
	}
	
	//Listから一括書き込み　現在未使用
	public void databaseInsert(SQLiteDatabase db,ArrayList<GeoPoint> gp){
		
		Date date = new Date(System.currentTimeMillis());

		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");
		
		
		
		ContentValues val = new ContentValues();
		for(GeoPoint g:gp){
			val.put("Longitude", g.getLongitudeE6());
			val.put("Latitude",g.getLatitudeE6());
			//db.insertOrThrow(sdf1.format(date), null, val);
			db.insert(this.date, null, val);
		}
	}
	
	//gpをそのつど書き込み　Serviceなんかで使用
	public void databaseInsert(SQLiteDatabase db,GeoPoint gp,String date,String message,Integer icon){
		
		//db.execSQL("alter table "+this.date+"add column Dates string");
		//db.execSQL("alter table "+this.date+"add column Message string");
		
		ContentValues val = new ContentValues();
		
		val.put("Longitude",gp.getLongitudeE6());
		val.put("Latitude",gp.getLatitudeE6());
		val.put("MapDate", date);
		val.put("Message", message);
		val.put("Icon", icon);
		Log.d("database",date);
	
		db.insert(this.date, null, val);
		
		Log.d("DataBase",this.date+"へ"+gp.toString()+"と"+date+"が書き込まれました");
		
	}
	
	public void dbClear(SQLiteDatabase db){
		
		
		db.execSQL("DELETE FROM "+this.date);
	}
	
	public void dbTableCreate(SQLiteDatabase db){
		db.execSQL("create table if not exists "+ this.date +"("+
				"_id integer primary key autoincrement,"+
				" Longitude integer not null,"+
				" Latitude integer not null,"+
				" MapDate String,"+
				" Message String,"+
				" Icon integer"+
				");"
		);
	}
	
	//プログラマにあるまじきコード
	public void test(SQLiteDatabase db){
		this.date = "D120919";
		db.execSQL("alter table "+this.date+" add column MapDate string");
		db.execSQL("alter table "+this.date+" add column Message string");
		db.execSQL("alter table "+this.date+" add column Icon integer");
	
		this.date = "D120918";
		db.execSQL("alter table "+this.date+" add column MapDate string");
		db.execSQL("alter table "+this.date+" add column Message string");
		db.execSQL("alter table "+this.date+" add column Icon integer");
	
		this.date = "D120917";
		db.execSQL("alter table "+this.date+" add column MapDate string");
		db.execSQL("alter table "+this.date+" add column Message string");
		db.execSQL("alter table "+this.date+" add column Icon integer");
		
		this.date = "D120916";
		db.execSQL("alter table "+this.date+" add column MapDate string");
		db.execSQL("alter table "+this.date+" add column Message string");
		db.execSQL("alter table "+this.date+" add column Icon integer");
		
		this.date = "D120915";
		db.execSQL("alter table "+this.date+" add column MapDate string");
		db.execSQL("alter table "+this.date+" add column Message string");
		db.execSQL("alter table "+this.date+" add column Icon integer");
		this.date = "D120914";
		db.execSQL("alter table "+this.date+" add column MapDate string");
		db.execSQL("alter table "+this.date+" add column Message string");
		db.execSQL("alter table "+this.date+" add column Icon integer");
		
	}

}
