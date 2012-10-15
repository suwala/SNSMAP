package com.example.snsmap;

public class LocationMinute{
	/* 
	 * longの値を15・30・45・60しか持たないクラス
	 * コンストラクタがprivateなためインスタンス化できない?はず
	 *  
	 */
	
	
	
	/*参照方法　LocationTime.MINUTE15.getTime();で対応した分が帰ってくる
	 * LocationTime.toTime();で配列が帰ってくる
	 */
	
	
	public static final Long MINUTE3 = (long)3*60*1000;
	public static final Long MINUTE5= (long)5*60*1000;
	public static final Long MINUTE15 = (long)15*60*1000;
	public static final Long MINUTE30 = (long)30*60*1000;
		
	//配列で扱うときに使う
	private static final Long[] MINUTE = {MINUTE3,MINUTE5,MINUTE15,MINUTE30};
	
	private long l;
	
	//private　コンストラクタ
	private LocationMinute(){
	}
	
	
	//配列取得用 LocationMinute[] lt = LocationMinute.toTime();で配列をセットする
	public static Long[] toTime(){
		return MINUTE;
		
	}
	
	//ミリ秒を分に直して返すメソッド
	public long getMinute(){
		return this.l/60/1000;
	}
	

	//debug用　0を返す
	public static long debug(){
		return 0;
	}

}
