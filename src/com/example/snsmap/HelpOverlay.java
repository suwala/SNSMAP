package com.example.snsmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class HelpOverlay extends Overlay{
	
	Context context;
	MapView mapView;
	
	Drawable[] helps;
	int inWidth,inHeight,setWidth,nowWidth;
	Rect[] rects;
	
	public HelpOverlay(Context context,MapView mapView,View view) {
		this.context = context;
		this.mapView = mapView;
		
		helps = new Drawable[7];
		helps[0] = (Drawable)context.getResources().getDrawable(R.drawable.helpin);
		helps[1] = (Drawable)context.getResources().getDrawable(R.drawable.helpnew);
		helps[2] = (Drawable)context.getResources().getDrawable(R.drawable.helpset);
		//[3]のみ暫定
		helps[3] = (Drawable)context.getResources().getDrawable(R.drawable.helpreload);
		helps[4] = (Drawable)context.getResources().getDrawable(R.drawable.helplist);
		helps[5] = (Drawable)context.getResources().getDrawable(R.drawable.helpnow);
		helps[6] = (Drawable)context.getResources().getDrawable(R.drawable.helpcenter);
		
		
		inWidth = mapView.getWidth()/6;
		inHeight = mapView.getHeight()/3;
		
		int baseY = inHeight*2;
		int baseY2 = inHeight*3;
		rects = new Rect[helps.length];
		for(int i=0;i<rects.length;i++)
			rects[i] = new Rect();
		
		Button btn = (Button) ((MainMap)context).findViewById(R.id.btnNew);
		int w = btn.getWidth();
		
		rects[0].set(0, baseY, w, baseY2);
		rects[1].set(w, baseY, w*2, baseY2);		
		rects[2].set(w*2, baseY, inWidth*5, baseY2);
		
		
		int[] anchorPos = new int[2];
		view.getLocationOnScreen(anchorPos);
		rects[3].set(0, 0, anchorPos[0], inHeight);
		rects[4].set(anchorPos[0], 0, anchorPos[0]+view.getWidth(), inHeight);
		rects[5].set(anchorPos[0]+view.getWidth(), 0, anchorPos[0]+view.getWidth()*2, inHeight);
		rects[6].set((int)(0),(int)(mapView.getHeight()*0.4),(int)(mapView.getWidth()),(int)(mapView.getHeight()*0.6));
		
		Log.d("helps",""+mapView.getWidth());
		
		for(int i=0;i<rects.length;i++){
			helps[i].setBounds(rects[i]);
		}
		
	}

	@Override
	public void draw(Canvas canvas,MapView mapView,boolean shadow){
		super.draw(canvas, mapView, shadow);
		if(!shadow){
			
			for(Drawable d:helps)
				d.draw(canvas);			
		}
	}
}
