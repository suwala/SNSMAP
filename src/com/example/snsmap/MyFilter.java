package com.example.snsmap;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

public class MyFilter implements InputFilter{

	
	private Integer maxLength;
	
	//InputFilterを使い入力された文字をチェックし制限する
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		// TODO 自動生成されたメソッド・スタブ
		
		if(source.toString().matches("^[a-zA-Z0-9]+$")&&dest.length() < this.maxLength){
			if(source.toString().matches("^[0-9]")&&dest.length() >= this.maxLength)
				return "";
			else 
				return source.toString().toLowerCase();//小文字で一文字ずつ返すようになるので　変換できなくなる
				
		}else{
			return "";
		}
	}
	
	public MyFilter(Integer i){
		this.maxLength = i;
	}
	

}
