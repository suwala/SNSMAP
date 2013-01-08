package com.example.snsmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GroupMemberOverlay extends Overlay{

	Context context;
	//List<Person> groupPerson;
	Person[] persons;

	public GroupMemberOverlay(Context context,Person... person) {
		this.context = context;
		this.persons = person;
	}

	@Override
	public synchronized void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		if(!shadow){

			Projection projection = mapView.getProjection();
			Point point = new Point();
			Paint paint = new Paint();
			StringBuilder messa=new StringBuilder();
			float baseX,baseY;
			int textWidth,textHeight;
			Bitmap icon;
			Drawable window = context.getResources().getDrawable(R.drawable.hukidasi_9);
			Rect rect = new Rect();
			int iconSize = MainMap.iconSize;

			for(Person p:persons){
				int resoures = NumToResources.toResources(p.getIconNumber());
				icon = BitmapFactory.decodeResource(context.getResources(), resoures);
				icon = Bitmap.createScaledBitmap(icon, iconSize, iconSize, false);
				projection.toPixels(p.getGp(), point);
				canvas.drawBitmap(icon, point.x-iconSize/2, point.y-iconSize, null);
			}
			for(Person p:persons){		
				//Nameの描画
				projection.toPixels(p.getGp(), point);
				textWidth = (int) paint.measureText(p.getName());
				FontMetrics fontMetrics = paint.getFontMetrics();
				paint.setAntiAlias(true);
				paint.setColor(Color.BLACK);
				paint.setTextSize(28);				
				baseX = point.x - textWidth/2;
				baseY = point.y - (fontMetrics.ascent+fontMetrics.descent)/2;

				canvas.drawText(p.getName(),baseX, baseY+iconSize*0.5f, paint);
			}
			for(Person p:persons){	
				projection.toPixels(p.getGp(), point);
				//window = BitmapFactory.decodeResource(context.getResources(), R.drawable.hukidasi_9);
				//messageの描画 etにしたい できんの？
				
				messa.delete(0, messa.length());
				messa.append(new String(p.getDate()+" "));

				//11文字ごとの改行処理
				if(p.getMessage().length()>11)
					messa.append(p.getMessage().substring(0, 11));
				else
					messa.append(p.getMessage());

				//吹き出しtextの描画
				FontMetrics fontMetrics = paint.getFontMetrics();


				textWidth = (int) paint.measureText(messa.toString());//textの幅
				textHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
				baseX = point.x - textWidth/2;
				baseY = point.y - (fontMetrics.ascent+fontMetrics.descent)/2-iconSize*2;


				rect.set((int)baseX-20,(int)baseY-textHeight , (int)(baseX+textWidth)+20, point.y-iconSize);
				window.setBounds(rect);
				window.draw(canvas);
				canvas.drawText(messa.toString(),baseX, baseY, paint);
			}


		}
	}


	public Person[] getMember(){
		return this.persons;
	}



}
