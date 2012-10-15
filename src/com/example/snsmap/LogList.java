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
		// TODO 自動生成されたメソッド・スタブ
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent();
		
		//i.putExtra("time", time[position].getTime());
		i.putExtra("friend", (String)l.getItemAtPosition(position));
		i.putExtra("friendList", position);
		Log.d("friend",(String)l.getItemAtPosition(position));
		this.setResult(RESULT_OK, i);
		//finish()でアクティビティを終了させる
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		Intent intent = getIntent();
		
		String[] str = intent.getStringArrayExtra("friend");
		for(String item:str)
			adapter.add(item);
		
		
		
		// アダプターを設定します
		this.setListAdapter(adapter);
	}

}
