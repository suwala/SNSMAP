package com.example.snsmap;


import com.google.android.maps.GeoPoint;
/*
 * GeoPoint同士を比較しbooleanで返すメソッドを実装したクラス
 * 書いた後
 * .toStoring().equals();で良かったんじゃないかと思う＼(^o^)／
 */

public class GeoPoint2 extends GeoPoint {

	public GeoPoint2(int latitudeE6, int longitudeE6) {
		super(latitudeE6, longitudeE6);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public boolean equalsGP(GeoPoint gp2){
		if(gp2 == null)
			return false;
		if(this.getLatitudeE6() == gp2.getLatitudeE6() && this.getLongitudeE6() == gp2.getLongitudeE6())
			return true;
		else
			return false;
	}
	
	

}
