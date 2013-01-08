package com.example.snsmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

public class SettingListActivity extends Activity{

	GridView gridView;
	int iconNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingview);
		
		final EditText et = (EditText)findViewById(R.id.settingEdit);
		et.setHint(MyDate.getName());
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
		
		
		iconNum = MyDate.getIconNum();
		Button btn = (Button)findViewById(R.id.settingOK);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et.getText().toString();
				setMyDate(et.getText().toString());
				finish();
			}
		});
		
		btn = (Button)findViewById(R.id.settingNO);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		GridView gridView = (GridView)findViewById(R.id.settingGrid);
		
		final List<Icons> iconList=new ArrayList<Icons>();
		for(IconListData i:IconListData.values())
			iconList.add(new Icons(false, i.getId()));
		iconList.get(iconNum).isChecked = true;
		
		final ImageAdapter image = new ImageAdapter(this, iconList);
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
				
				image.setIconNum(iconNum);
				image.notifyDataSetChanged();
			}
		});
	}
	
	private void setMyDate(String name){
		if(!name.equals(""))
			MyDate.setName(name);
		MyDate.setIconNum(iconNum);
		MyDate.writeMyDate(this);
	}
}
