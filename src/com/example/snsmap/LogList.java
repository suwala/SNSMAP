package com.example.snsmap;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LogList extends ListActivity{
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent();
		
		//i.putExtra("time", time[position].getTime());
		i.putExtra("friend", (String)l.getItemAtPosition(position));
		i.putExtra("friendList", position);
		Log.d("friend",(String)l.getItemAtPosition(position));
		this.setResult(RESULT_OK, i);
		//finish()�ŃA�N�e�B�r�e�B���I��������
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate(savedInstanceState);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		Intent intent = getIntent();
		
		String[] str = intent.getStringArrayExtra("friend");
		for(String item:str)
			adapter.add(item);
		
		
		
		// �A�_�v�^�[��ݒ肵�܂�
		this.setListAdapter(adapter);
	}

}
