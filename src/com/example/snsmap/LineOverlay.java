package com.example.snsmap;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LineOverlay extends Overlay{
	
	private ArrayList<OverlayItems> gp;
	
	public LineOverlay(ArrayList<OverlayItems> gp){
		
		//新たにListのインスタンスを作る this.gp=gpとやった場合アドレスのコピーとなって動作がおかしくなる
		this.gp = new ArrayList<OverlayItems>(gp);
		Log.d("Line",String.valueOf(this.gp.size()));
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO 自動生成されたメソッド・スタブ
		super.draw(canvas, mapView, shadow);
		if(this.gp.size() > 1){
			if(!shadow){
				Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG);
				paint.setStyle(Paint.Style.STROKE);
				paint.setAntiAlias(true);
				paint.setStrokeWidth(3);
				paint.setColor(Color.BLUE);

				Path  path = new Path();
				Projection projection = mapView.getProjection();
				Point pxStart;
				Point pxEnd;
				
				
				for(int i = 0;i < this.gp.size()-1;i++){
					pxStart = projection.toPixels(this.gp.get(i).getGeoPoint(), null);
					pxEnd = projection.toPixels(this.gp.get(i+1).getGeoPoint(), null);
					path.moveTo(pxStart.x,pxStart.y);
					path.lineTo(pxEnd.x,pxEnd.y);
					canvas.drawPath(path, paint);
				}
				
				
			}
		}
		Log.d("LineGpSize",String.valueOf(this.gp.size()));
	}
	
	
	

}
