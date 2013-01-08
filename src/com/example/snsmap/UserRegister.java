package com.example.snsmap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class UserRegister extends Activity{

	GridView gridView;
	List<Icons> iconList;
	int iconNum;
	UUID uuid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userregister);
		
		if(MyDate.readMyDate(this)){
			startActi();
		}
		
		gridView = (GridView)findViewById(R.id.gridView_register);
		
		iconList = new ArrayList<Icons>();
		for(IconListData i :IconListData.values()){
			iconList.add(new Icons(false,i.getId()));
		}
		iconList.get(0).isChecked = true;
		
		final ImageAdapter image = new ImageAdapter(this,iconList);
		
		
		Drawable draw;
		for(Icons i:iconList){
			draw = getResources().getDrawable(i.resoce);
			image.add(draw);
		}		
		
		gridView.setAdapter(image);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				iconList.get(arg2).isChecked=true;
				iconList.get(iconNum).isChecked=false;
				iconNum = arg2;
				
				//再描画 パフォ悪し
				image.setIconNum(iconNum);
				image.notifyDataSetChanged();
			}
		});
				
		final EditText et = (EditText)findViewById(R.id.editText1);
		et.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
					InputMethodManager imm = (InputMethodManager)getSystemService(UserRegister.INPUT_METHOD_SERVICE);
	                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}				
				return false;
			}
		});
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ここでUserセット
				if(et.getText().toString().equals(""))
					Toast.makeText(UserRegister.this, R.string.toast_noname, Toast.LENGTH_SHORT).show();
				else{
					MyDate.setName(et.getText().toString());
					MyDate.setIconNum(iconNum);
					MyDate.setUuid(uuid);
					MyDate.writeMyDate(UserRegister.this);
					
					startActi();
				}
			}
		});
		
		iconNum = 0;
		uuid = UUID.randomUUID();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	private void startActi(){
		Intent intent = new Intent(this,MainMap.class);
		startActivity(intent);
		finish();
	}
}
