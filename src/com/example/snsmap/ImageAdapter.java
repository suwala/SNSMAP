package com.example.snsmap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAdapter extends ArrayAdapter<Drawable> {
	
	private Context mContext;
	private final LayoutInflater mInflater;
	private IconListData[] iconList = IconListData.values();
	
	public ImageAdapter(Context _mContext){
		super(_mContext,0);
		this.mContext = _mContext;
		this.mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	//addした回数繰り返される？positionでカウント
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自動生成されたメソッド・スタブ
		
		View row = convertView;
		ImageView iv = new ImageView(this.mContext);
		LinearLayout layout = new LinearLayout(this.mContext);
		
		if(row == null){
			layout = new LinearLayout(this.mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
			
			row = mInflater.inflate(R.layout.iconlist, null);
			
			iv = (ImageView)row.findViewById(R.id.imageView1);
			
			iv.setImageResource(this.iconList[position].getId());
		}else{
			iv=(ImageView)row.getTag();
		}
		
		return row;
	}

}