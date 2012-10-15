package com.example.snsmap;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

public class MyFilter implements InputFilter{

	
	private Integer maxLength;
	
	//InputFilter���g�����͂��ꂽ�������`�F�b�N����������
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		if(source.toString().matches("^[a-zA-Z0-9]+$")&&dest.length() < this.maxLength){
			if(source.toString().matches("^[0-9]")&&dest.length() >= this.maxLength)
				return "";
			else 
				return source.toString().toLowerCase();//�������ňꕶ�����Ԃ��悤�ɂȂ�̂Ł@�ϊ��ł��Ȃ��Ȃ�
				
		}else{
			return "";
		}
	}
	
	public MyFilter(Integer i){
		this.maxLength = i;
	}
	

}
