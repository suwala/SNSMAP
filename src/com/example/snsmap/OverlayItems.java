package com.example.snsmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class OverlayItems  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint gp;
	private String message;
	private Integer iconNum;
	private String date;
	private String friendId;
	private String friendName;

	public void setFriendName(String _friendName){
		this.friendName = _friendName;
	}
	
	public String getFriendName(){
		return this.friendName;
	}
	
	public void setStringToGeoPoint(String str){
		String[] gpStr = str.split(",");
		this.gp = new GeoPoint(Integer.valueOf(gpStr[0]), Integer.valueOf(gpStr[1]));
	}
	
	public void setFriendId(String _friendId){
		this.friendId = _friendId;
	}
	
	public String getFriendId(){
		return this.friendId;
	}
	
	public void setDate(String _date){
		this.date = _date;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public void setGeoPoint(GeoPoint _gp){
		this.gp = _gp;
	}
	
	public GeoPoint getGeoPoint(){
		return this.gp;
	}
	
	public void setIconNum(int _iconNum){
		this.iconNum = _iconNum;
	}
	
	public Integer getIconNum(){
		return this.iconNum;
	}
	
	public void setMessage(String str){
		this.message = str;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public void setItem(String _date,String _message,GeoPoint _gp,int _iconNum){
		
		this.date = _date;
		this.message = _message;
		this.gp = _gp;
		this.iconNum = _iconNum; 
	}
}
