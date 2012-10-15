package com.example.snsmap;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public enum IconListData {

	IOCN1(R.drawable.icon01),
	ICON2(R.drawable.icon02),
	ICON3(R.drawable.icon03),
	ICON4(R.drawable.icon04),
	ICON5(R.drawable.icon05),
	ICON6(R.drawable.icon06),
	ICON7(R.drawable.icon07),
	ICON8(R.drawable.icon08),
	ICON9(R.drawable.icon09),;

	private int id;

	private IconListData(int i){
		this.id = i;
	}
	
	public int getId(){
		return this.id;
	}
	
}
