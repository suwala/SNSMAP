package com.example.snsmap;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimeList extends ListActivity{

	/*
	 * ListActivityを継承
	 * Activityを継承した場合と記述が変わる
	 * ListView list を使わずに
	 * ListActivityを直接オーバーライドする
	 *
	 *
	 */
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		super.onListItemClick(l, v, position, id);
						
		Long[] time = LocationMinute.toTime();
		
		Intent i = new Intent();
		
		//数値を返す　返し方はコレデ良いのかなー
		i.putExtra("time", time[position]);
		this.setResult(RESULT_OK, i);
		//finish()でアクティビティを終了させる
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		/* adapter.add アイテムを追加します
		 * 
		 * LocationTimeの配列は作られてるため
		 * LocationTime[] timeの中身を追記すれば勝手にリストを増やしてくれる、すばら！
		 */
		Long[] time = LocationMinute.toTime();
		for(Long i:time){
			adapter.add(String.valueOf(i/60/1000)+"分");
		}
		
		// アダプターを設定します
		this.setListAdapter(adapter);

		
		
	}
	

}
