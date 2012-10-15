package com.example.snsmap;

import com.example.snsmap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;




public class GridIconList extends Activity {

	private Context context;
	private final LayoutInflater mInflater;

	/*
	 * ���X�g�̕��т��c���ɑΉ��������N���X�̗\��
	 * �摜���i�q��ɕ\�������ā@ ��������C�x���g�����݂����Ȍ`
	 * GridView�Ƃ����̂�����̂ł�����ɕύX
	 * 
	 * 
	 */
	
	public GridIconList(Context _context){
		this.context = _context;
		this.mInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onCreateDialog().show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate(savedInstanceState);
		Log.d("dia","cre");
		this.onCreateDialog().show();
		
		
	}
	
	protected Dialog onCreateDialog(){
		
		View row = this.mInflater.inflate(R.layout.gridview, null);
		
		
		GridView gv = new GridView(this.context);
		gv = (GridView)row.findViewById(R.id.gridView1);//GridView�擾
		
		
		ImageView iv = new ImageView(this.context);
		LinearLayout layout = new LinearLayout(this.context);
		
		layout = new LinearLayout(this.context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		row = mInflater.inflate(R.layout.iconlist, null);
		
		iv = (ImageView)row.findViewById(R.id.imageView1);
		
		iv.setImageResource(R.drawable.icon02);
		
		
		ImageAdapter image = new ImageAdapter(context);
		for(IconListData id:IconListData.values()){
			Drawable draw = this.context.getResources().getDrawable(id.getId());
			image.add(draw);
		}
		
		
		Log.d("dia","test");
		gv.setAdapter(image);
		return new AlertDialog.Builder(this.context)
		.setView(gv).create();
		
	}

}