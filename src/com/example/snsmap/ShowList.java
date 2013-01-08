package com.example.snsmap;

import android.content.Context;
import android.widget.ListView;

public class ShowList extends ListView{
	
	public ShowList(Context context,Group... groups) {
		super(context);
		FileListAdapter adapter = new FileListAdapter(context, groups);
		this.setAdapter(adapter);
	}
	
	public ShowList(Context context,Person... persons){
		super(context);
		MemberListAdapter adapter = new MemberListAdapter(context, persons);
		setAdapter(adapter);
	}
}
