package com.example.snsmap;

public interface Callback {
	
	public static final int SUCCESS = 0;//成功時のレスポンスコード
	public static final int ERROR = 1;//失敗時
	
	public void callback(final int responceCode,final int requestCode,final Object... resultMap);

}
