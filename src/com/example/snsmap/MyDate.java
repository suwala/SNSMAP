package com.example.snsmap;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public class MyDate{
	private static String name;
	private static int iconNum;
	private static UUID uuid;
	static public String getName() {
		return name;
	}
	static public void setName(String name) {
		MyDate.name = name;
	}
	static public int getIconNum() {
		return iconNum;
	}
	static public void setIconNum(int iconNum) {
		MyDate.iconNum = iconNum;
	}
	static public UUID getUuid() {
		return uuid;
	}
	static public void setUuid(UUID uuid) {
		MyDate.uuid = uuid;
	}
	
	public static void writeMyDate(Context context){
		SharedPreferences prefs = context.getSharedPreferences("user",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("name", MyDate.getName());
		editor.putString("uuid", MyDate.getUuid().toString());
		editor.putInt("icon", MyDate.getIconNum());
		editor.commit();
	}
	
	public static boolean readMyDate(Context context){
		SharedPreferences prefs = context.getSharedPreferences("user",Context.MODE_PRIVATE);
		
		String uuid = prefs.getString("uuid", null);
		if(uuid != null){
			
			UUID id = UUID.fromString(uuid);
			MyDate.setUuid(id);

			String name = prefs.getString("name", null);
			MyDate.setName(name);

			int iconNum = prefs.getInt("icon", -1);
			MyDate.setIconNum(iconNum);
			return true;
		}
		return false;
	}
}