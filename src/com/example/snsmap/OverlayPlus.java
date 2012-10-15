package com.example.snsmap;

import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.Toast;

import com.example.snsmap.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class OverlayPlus extends Overlay implements GestureDetector.OnGestureListener{
/*GestureDetectorを実装することによってダブルタップ等のイベントの拡張が出来る
 * onTapで拡張イベント実装したいけどやり方が分からなかった
 * 何処でも反応するけどlongPressで妥協
 */
	private Context context;
	private ArrayList<OverlayItems> myItem;
	private int iconNum;
	private GeoPoint now;
	private Date date;
	private Boolean nowFlag;
	
	private GestureDetector gestureDetector;
	private MotionEvent event;
	private List<OverlayItems> friend = new ArrayList<OverlayItems>();
	private String userName;
	
	//addの方法に問題あり過去のDBにまでadd出来てる　過去のポイントへ書き込んでない
	//db.updateで上書きできるらしいぞ
	//アイコンが正しく表示されてない
	
	//コレが実行されてる
	public OverlayPlus(Context context,int iconNum,ArrayList<OverlayItems> myItem,Boolean _flag,Date _date,ArrayList<OverlayItems> friend,String userName){
		this.context = context;
		this.iconNum = iconNum;
		
		//myItemを渡すと自分のが　friendを渡すとフレンドのアイコンが表示される
		//this.myItem = myItem; 
		this.myItem = friend;
		this.nowFlag = _flag;
		this.date = _date;
		//このクラスをリスナーとして設定
		this.gestureDetector = new GestureDetector(context,this);
		this.friend = friend;
		this.userName = userName;
		
	}
	
	//引数gpはタッチしたポイントのGeoPointになる
	@Override
	public boolean onTap(final GeoPoint gp, MapView map) {
		// TODO 自動生成されたメソッド・スタブ

		
		Projection projection = map.getProjection();
		final Integer hitIndex = hitTest(projection,gp);

		//interfaceとabstractでスリム化 　まだいけそう
		
		if(hitIndex != -1){
			
			Log.d("plusHit",String.valueOf(hitIndex));
			if(hitIndex ==-2){//-2はnewPoint(現在地を表示)を示す
				final EditText input;
				input = new EditText(this.context);
				new AlertDialog.Builder(context)
				.setIcon(R.drawable.icon01)
				.setTitle("メッセージを入力してね")
				.setView(input)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					//新規ポイントにメッセージが入力された場合
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ
						
						if(!input.getText().toString().equals("")){
							Log.d("inputtext","size="+String.valueOf(input.getTextSize()));
							Log.d("inputtext","text="+String.valueOf(input.getText().toString()));
							
							Log.d("inser",String.valueOf(myItem.size()));
							OverlayItems item = new OverlayItems();
							
							date = new Date(System.currentTimeMillis());
							
							SimpleDateFormat sdf1 = new SimpleDateFormat("HH':'mm");
							item.setItem(sdf1.format(date), input.getText().toString(), now, iconNum);
							myItem.add(item);
							Log.d("inser",String.valueOf(myItem.size()));
							DataBaseLogic dbl = new InsertDB();
							dbl.setData(hitIndex, input.getText().toString(), now, context, date,iconNum);
							
							serverSend(item);
							
							((MainActivity)context).pinClearS();
							((MainActivity)context).drawOverlay();
													
						}									
					}
				})
				.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ


					}
				})
				.show();//ここまでNewポイント
			}else if(this.myItem.get(hitIndex).getMessage()==null){//既存のポイントにヒットし　メッセージが空の場合
				
				Toast.makeText(context, this.myItem.get(hitIndex).getDate(), Toast.LENGTH_SHORT).show();
				Log.d("plus",date.toString());
				final EditText input;
				input = new EditText(this.context);
				new AlertDialog.Builder(context)
				.setIcon(R.drawable.icon01)
				.setTitle("メッセージを入力してね")
				.setView(input)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					//既存ポイントへの更新
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ
						
						if(!input.getText().toString().equals("")){//入力があった場合
							
							Log.d("inputtext","size="+String.valueOf(input.getTextSize()));
							Log.d("inputtext","size="+String.valueOf(input.getText()));
							
							
							
							Toast.makeText(context, myItem.get(hitIndex).getDate(), Toast.LENGTH_SHORT).show();
							DataBaseLogic dbl = new UpdateDB();
							Log.d("DB更新",String.valueOf(iconNum));
							dbl.setData(hitIndex, input.getText().toString(),context, date, myItem,iconNum);
							((MainActivity)context).pinClearS();
							((MainActivity)context).drawOverlay();
													
						}									
					}
				})
				.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ


					}
				})
				.show();
			}else{//既存Pにヒットしメッセージが入力済みの場合
				
				Toast.makeText(context, this.myItem.get(hitIndex).getDate()+"  "+this.myItem.get(hitIndex).getFriendName()+"\n"+this.myItem.get(hitIndex).getMessage(), Toast.LENGTH_SHORT).show();
				
			}
			
		}
		
		if(gestureDetector.onTouchEvent(event)){
			Toast.makeText(context, "Test2", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		return super.onTap(gp, map);
	}

	public int hitTest(Projection pj,GeoPoint gp){

		Point hit = new Point();
		pj.toPixels(gp, hit);
		Drawable icon = context.getResources().getDrawable(NumToResources.toResources(this.iconNum));
		
		Log.d("oveelay",String.valueOf(this.myItem.size()));
		
		if(!this.nowFlag){
			for(int i=0;i<this.myItem.size();i++){
				Point point = new Point();
				pj.toPixels(this.myItem.get(i).getGeoPoint(), point);
				//pj.toPixels(this.now, point);

				

				int halfWidth = icon.getIntrinsicWidth()*2;
				int left = point.x - halfWidth;
				int right = point.x + halfWidth;
				int top = point.y - icon.getIntrinsicHeight()*2;
				int bottom = point.y;



				if(left <= hit.x && hit.x <= right){
					if(top <= hit.y && hit.y <= bottom){
						return i;
					}
				}
			}
		}else{
			Point point = new Point();
			pj.toPixels(this.now, point);
			int halfWidth = icon.getIntrinsicWidth()*2;
			int left = point.x - halfWidth;
			int right = point.x + halfWidth;
			int top = point.y - icon.getIntrinsicHeight()*2;
			int bottom = point.y;



			if(left <= hit.x && hit.x <= right){
				if(top <= hit.y && hit.y <= bottom){
					return -2;
				}
			}
		}
		return -1;

	}

	@Override
	public synchronized void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO 自動生成されたメソッド・スタブ
		super.draw(canvas, mapView, shadow);
		if(!shadow){
			
			Drawable icon = context.getResources().getDrawable(NumToResources.toResources(this.iconNum));
			Projection pj = mapView.getProjection();
			Point point = new Point();
			
			
					
			
			//現在地点を描画 flagで管理
			if(this.nowFlag){
				pj.toPixels(this.now, point);
				Rect bound = new Rect();
				
				int halfWidth = icon.getIntrinsicWidth()/2;
				
				bound.left = point.x - halfWidth;
				bound.right = point.x + halfWidth;
				bound.top = point.y - icon.getIntrinsicHeight();
				bound.bottom = point.y;
				
				icon.setBounds(bound);
				icon.draw(canvas);		
			}else{
			
			//GP全てに描画する場合

				for(OverlayItems i:this.myItem){
				//for(OverlayItems i:this.friend){
					pj.toPixels(i.getGeoPoint(), point);//GPをPointに渡してる？ここで描画位置の特定か
					Rect bound = new Rect();

				/*
					if(null != i.getMessage())
						Log.d("plusMesse",i.getMessage());
					*/
					
					if(i.getIconNum() == 0){//DBのIconNumがnull=0の時　多分使わない
						icon = this.context.getResources().getDrawable(R.drawable.icon01);
						
						int halfWidth = icon.getIntrinsicWidth()/2;

						bound.left = point.x - halfWidth;
						bound.right = point.x + halfWidth;
						bound.top = point.y - icon.getIntrinsicHeight();
						bound.bottom = point.y;
						
						icon.setBounds(bound);
						icon.draw(canvas);
						
						
					}else{//DBにiconNumが入ってるとき
												
						int resources = NumToResources.toResources(i.getIconNum());
						icon = this.context.getResources().getDrawable(resources);
												
						int halfWidth = icon.getIntrinsicWidth()/2;

						//描画領域の設定みたい
						bound.left = point.x - halfWidth;
						bound.right = point.x + halfWidth;
						bound.top = point.y - icon.getIntrinsicHeight();
						bound.bottom = point.y;
						
						icon.setBounds(bound);
						icon.draw(canvas);
						
						//SnsMapの追加分 アイコンの上に吹き出しを描画する 描画順があるので
						//Text生成　生成したTextの幅を元に吹き出しを描画の順で行う
						Drawable window = context.getResources().getDrawable(R.drawable.hukidasi_9);
												
						
						//　textを描画する
						Paint paint = new Paint();
						paint.setAntiAlias(true);
						paint.setColor(Color.BLACK);
						paint.setTextSize(22);
						
						
						Rect rect = new Rect();
						String freMessa = i.getDate()+" "+i.getMessage();
						paint.getTextBounds(freMessa, 0, freMessa.length(), rect);
						
						FontMetrics fm = paint.getFontMetrics();
						int mtw = (int) paint.measureText(freMessa);//textの幅
						int fmHeight = (int) (Math.abs(fm.top)+fm.bottom);//高さ
						Log.d("draw",String.valueOf(mtw)+":"+String.valueOf(fmHeight));
						
						bound.left = point.x - mtw/2;
						bound.right = point.x + mtw/2+20;
						bound.top = point.y - icon.getIntrinsicHeight()-80;
						bound.bottom = point.y - icon.getIntrinsicHeight();
						
						window.setBounds(bound);
						window.draw(canvas);
						canvas.drawText(freMessa, point.x-mtw/2+10, point.y-icon.getIntrinsicHeight()-40, paint);
						
						String freName = i.getFriendName();
						paint.getTextBounds(freMessa, 0, freMessa.length(), rect);
						fm = paint.getFontMetrics();
						mtw = (int) paint.measureText(freMessa);//textの幅
						fmHeight = (int) (Math.abs(fm.top)+fm.bottom);//高さ
						canvas.drawText(freName, point.x-mtw/3, point.y+fmHeight/2, paint);
					}
					/*
					int halfWidth = icon.getIntrinsicWidth()/2;

					bound.left = point.x - halfWidth;
					bound.right = point.x + halfWidth;
					bound.top = point.y - icon.getIntrinsicHeight();
					bound.bottom = point.y;
					
					icon.setBounds(bound);
					icon.draw(canvas);	
					*/
				}
			}
		}
	}

	public void addGp(GeoPoint gp){
		this.now = gp;
	}



	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}



	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}



	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		// TODO 自動生成されたメソッド・スタブ
		this.event = e;
		this.gestureDetector.onTouchEvent(e);
		return super.onTouchEvent(e, mapView);
	}


	//Overlayが存在し長押しされた場合
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
				
		((MainActivity)context).onCreateDialog2();
	}



	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}



	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}



	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	
	public void serverSend(OverlayItems item){
		SharedPreferences prefs = context.getSharedPreferences("snsmap", Context.MODE_PRIVATE);
		String user = prefs.getString("user", null);
		
		String setUrl = context.getResources().getString(R.string.url) + "maps.php";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams httpParams = httpclient.getParams();
		httpParams.setParameter("http.useragent", "snsmap");//User-agentの設定　通常はブラウザとか端末とか
		HttpPost httppost = new HttpPost(setUrl);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("user", user));
		nameValuePairs.add(new BasicNameValuePair("dates", item.getDate()));//Date
		nameValuePairs.add(new BasicNameValuePair("gp", item.getGeoPoint().toString()));//Gp
		nameValuePairs.add(new BasicNameValuePair("icon", item.getIconNum().toString()));//ここまでええええええ
		nameValuePairs.add(new BasicNameValuePair("message", item.getMessage()));
		nameValuePairs.add(new BasicNameValuePair("userName", this.userName));
		
		//imorida:D121007:33555876,130305253:2130837506:あたま
		Log.d("SendData",user+":"+item.getDate()+":"+item.getGeoPoint().toString()+":"+item.getIconNum().toString()+":"+item.getMessage());
		
		try{
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			HttpResponse respone = httpclient.execute(httppost);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			respone.getEntity().writeTo(byteArrayOutputStream);
			
			if(respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				
				Log.d("maps.php",":"+byteArrayOutputStream.toString());
				Toast.makeText(context, "位置情報を登録しました", Toast.LENGTH_SHORT).show();
				
				
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, "サーバーと接続できません", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void getFriendData(){
		if(this.friend.size()!=0){
			
		}
	}
}
