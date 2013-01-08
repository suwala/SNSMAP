package com.example.snsmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyOverlay extends Overlay implements OnGestureListener,OnDoubleTapListener{

	Context context;
	MapView mapView;
	GeoPoint[] myGP;
	GestureDetector gestureDetector;
	LayoutInflater mInflater;


	public MyOverlay(Context context,MapView mapView) {
		this.context = context;
		this.mapView = mapView;
		myGP = new GeoPoint[3];
		gestureDetector = new GestureDetector(this);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		// GestureDetector にタッチイベントを詳細化してもらう
		gestureDetector.onTouchEvent(e);
		return super.onTouchEvent(e, mapView);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		if(!shadow && myGP[0]!=null){

			Projection projection = mapView.getProjection();
			Point point = new Point();
			Paint paint = new Paint();

			int iconSize = MainMap.iconSize;

			int resource = NumToResources.toResources(MyDate.getIconNum());
			Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resource);
			icon = Bitmap.createScaledBitmap(icon, iconSize, iconSize, false);
			projection.toPixels(myGP[0], point);
			canvas.drawBitmap(icon, point.x-iconSize/2, point.y-iconSize, null);

		}

	}

	@Override
	public boolean onTap(GeoPoint gp, MapView mapView) {
		//使うと全イベントが吸収される？使わないこと推奨
		return false;
	}

	public void onLongPress(MotionEvent event) {
		Log.d("myOverlay","LongPressssssssssssss");
		
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		//一度のみ呼ばれる
		Projection projection = mapView.getProjection();		
		myGP[0] = projection.fromPixels((int)event.getX(), (int)event.getY());
		
		setSendDate();
		mapView.invalidate();
		
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		//過剰反応？5つぐらい呼ばれる
		
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private synchronized void setSendDate(){
		final EditText et = new EditText(context);
		et.setHint(R.string.et_send_hint);
		et.setInputType(InputType.TYPE_CLASS_TEXT);
		InputFilter[] inputFilters = new InputFilter[1];
		inputFilters[0] = new InputFilter.LengthFilter(144);
		et.setFilters(inputFilters);
		
		View view = mInflater.inflate(R.layout.sendview, null);
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup1);		
		
		
		new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.dialog_messa_title))
			.setView(view).setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String messa = et.getText().toString();
					int id = radioGroup.getCheckedRadioButtonId();
					String idStr;
					if(id == R.id.radio0)
						idStr = "1";
					else if(id == R.id.radio1)
						idStr = "2";
					else
						idStr = "3";
					
					SendMyDate send = new SendMyDate(context,MainMap.SEND_DATE);
					send.execute(MainMap.MODE_UPDATE,myGP[0].toString(),messa,idStr);
				}
			}).setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myGP[0] = null;
					mapView.invalidate();
				}
			}).setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(event.getAction() == KeyEvent.ACTION_UP){
						if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
							myGP[0] = null;
							mapView.invalidate();
						}
					}
					return false;
				}
			}).show();
	}
}
