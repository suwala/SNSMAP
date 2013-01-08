package com.example.snsmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

public class MemberListAdapter extends ArrayAdapter<Person>{
	
	static final int text1 = android.R.id.edit;
	static final int icon = android.R.id.icon;
	LayoutInflater mInflater;
	Person[] perons;
	
	public MemberListAdapter(Context context,Person[] persons){
		super(context,text1,persons);
		mInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.perons = persons;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
			convertView = mInflater.inflate(R.layout.member_list_item_with_icon, null);
		
		EditText message = (EditText)convertView.findViewById(text1);
		ImageView iconImage = (ImageView)convertView.findViewById(icon);
		message.setFocusable(false);
		message.setText(perons[position].getName()+":"+perons[position].getDate()+"\n"+perons[position].getMessage());
		int resource = NumToResources.toResources(perons[position].getIconNumber());
		iconImage.setImageResource(resource);
		
		return convertView;
		
	}

}
