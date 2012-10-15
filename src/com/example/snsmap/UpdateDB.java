package com.example.snsmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.snsmap.R;
import com.google.android.maps.GeoPoint;

public class UpdateDB extends DataAbs {

	@Override
	public void toData(ContentValues val, String input,
			Date date, SQLiteDatabase db, ArrayList<OverlayItems> myItem,int hitIndex,int icon) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		//�f�[�^�x�[�X�̍X�V
		val.put("Message", input);
		
		val.put("Icon", icon);
		db.update(sdf1.format(date), val,"_id = "+(hitIndex+1),null);//3�Ԗڈ�����String�@�����int��n���Ă�悤��
		
		//�A�C�e���̍X�V
		myItem.get(hitIndex).setMessage(input);
		myItem.get(hitIndex).setIconNum(icon);
		
		Log.d("text",myItem.get(hitIndex).getMessage());
		
	}

	@Override
	public void toData(ContentValues val, GeoPoint now, String input,
			Date date, SQLiteDatabase db, int hitIndex, int icon) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		;
		
	}

	

}
