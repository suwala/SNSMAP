package com.example.snsmap;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class GridIcons {
	
	private Context context;
	List<Icons> iconList;
	
	public GridIcons(Context context) {
		this.context = context;
		this.iconList = new ArrayList<Icons>();
	}
	
	public View showGridView(){
				
		GridView gridView = new GridView(context);
		gridView.setNumColumns(6);
		
		for(IconListData i:IconListData.values())
			iconList.add(new Icons(false,i.getId()));
		iconList.get(MyDate.getIconNum()).isChecked = true;
		
		final ImageAdapter image = new ImageAdapter(context, iconList);
		
		for(Icons i:iconList){
			Drawable draw = context.getResources().getDrawable(i.resoce);
			image.add(draw);
		}
		
		gridView.setAdapter(image);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				iconList.get(arg2).isChecked=true;
				iconList.get(MyDate.getIconNum()).isChecked=false;
				MyDate.setIconNum(arg2);
				MyDate.writeMyDate(context);
				image.setIconNum(arg2);
				image.notifyDataSetChanged();				
			}
		});
		
		return gridView;
		//new AlertDialog.Builder(context).setView(gridView).show();
	}

}
