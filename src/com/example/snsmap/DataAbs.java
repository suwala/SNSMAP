package com.example.snsmap;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DataAbs implements DataBaseLogic{


	DBHepler dbh;
	SQLiteDatabase db;
	ContentValues val;
	
	//DBUpDate
	@Override
	public void setData(int _hitIndex,String input,Context context,Date date,ArrayList<OverlayItems> myItem,int icon){
		
		this.settingDB(context, date);
		//‚±‚±‚Å•ªŠò
		this.toData(this.val,input,date,this.db,myItem,_hitIndex,icon);
		this.dbh.close();
	}
	
	//DBInsert
	@Override
	public void setData(int _hitIndex,String input,GeoPoint gp,Context context,Date date,int icon){
		
		this.settingDB(context, date);
		this.toData(this.val,gp,input,date,this.db,_hitIndex,icon);
		this.dbh.close();
	}
	
	private void settingDB(Context context,Date date){
		this.dbh = new DBHepler(context,sdf1.format(date));
		this.db = dbh.getReadableDatabase();
		this.val = new ContentValues();
		
	}
	
	public abstract void toData(ContentValues val,String input,Date date,SQLiteDatabase db,ArrayList<OverlayItems> myItem,int hitIndex,int icon);
	public abstract void toData(ContentValues val,GeoPoint now,String input,Date date,SQLiteDatabase db,int hitIndex,int icon);
}
