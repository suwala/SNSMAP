package com.example.snsmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<Object>{
	
	static final int text1 = android.R.id.text1;
	static final int icon = android.R.id.icon;
	LayoutInflater mInflater;
	Group[] groups;

	public FileListAdapter(Context context, Group[] groups) {
		super(context,text1,groups);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groups = groups;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.list_item_with_icon, null);
		}
		TextView fName = (TextView)convertView.findViewById(text1);
		ImageView fIcon = (ImageView)convertView.findViewById(icon);
		
		fName.setText(groups[position].name);
		if("".equals(groups[position].pass))
			fIcon.setImageResource(R.drawable.anlock);
		else
			fIcon.setImageResource(R.drawable.lock);
		
		return convertView;
	}
}
