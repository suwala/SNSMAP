package com.example.snsmap;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAdapter extends ArrayAdapter<Drawable> {
	
	private Context mContext;
	private final LayoutInflater mInflater;
	//private IconListData[] iconList = IconListData.values();
	private List<Icons> list;
	private int iconNum,oldIcon;
	
	public ImageAdapter(Context _mContext,List<Icons> icons){
		super(_mContext,0);
		this.mContext = _mContext;
		this.mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = icons;
	}
	
	public void setIconNum(int iconNum){
		this.oldIcon = this.iconNum;
		this.iconNum = iconNum;
	}

	//add�����񐔌J��Ԃ����Hposition�ŃJ�E���g
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		Bitmap bmp;
		View row = convertView;
		ImageView iv;
		LinearLayout layout = new LinearLayout(this.mContext);
		
		
		//if(row == null){
			layout = new LinearLayout(this.mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
			
			row = mInflater.inflate(R.layout.gridicon, null);
			
			iv = (ImageView)row.findViewById(R.id.imageView1);
			
			bmp = BitmapFactory.decodeResource(mContext.getResources(), list.get(position).resoce);
			bmp = Bitmap.createScaledBitmap(bmp, 90, 90, false);
			
			iv.setImageBitmap(bmp);
			if(list.get(position).isChecked){
				Canvas canvas = new Canvas(bmp);
				Paint paint = new Paint();
				paint.setStrokeWidth(3);
				paint.setStyle(Style.STROKE);
				paint.setColor(Color.RED);
				Rect rect = new Rect(0, 0, 90, 90);

				canvas.drawRect(rect, paint);
			}/*	特定の部分のみ再描画しようとしたら
			グリッド内部の順番が入れ替わった不思議
			パフォ悪いけど毎回描画してます
		}else{
			//if(position == iconNum){
				iv = (ImageView)row.findViewById(R.id.imageView1);
				bmp = BitmapFactory.decodeResource(mContext.getResources(), list.get(position).resoce);
				bmp = Bitmap.createScaledBitmap(bmp, 90, 90, false);
				
				Canvas canvas = new Canvas(bmp);
				Paint paint = new Paint();
				paint.setStrokeWidth(3);
				paint.setStyle(Style.STROKE);
				paint.setColor(Color.RED);
				Rect rect = new Rect(0, 0, 90, 90);
				
				canvas.drawRect(rect, paint);

				iv.setImageBitmap(bmp);
			}
			if(position == oldIcon){
				iv = (ImageView)row.findViewById(R.id.imageView1);
				bmp = BitmapFactory.decodeResource(mContext.getResources(), list.get(position).resoce);
				bmp = Bitmap.createScaledBitmap(bmp, 90, 90, false);
								
				iv.setImageBitmap(bmp);
			}
			iv=(ImageView)row.getTag();
		}*/
		
		return row;
	}

}