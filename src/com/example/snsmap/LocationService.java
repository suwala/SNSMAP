package com.example.snsmap;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.Keyboard;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;


/*
 * Activityよりもキルされ難いServiceを使って現在地を得る
 * 
 * ManifestにService名を記述する <service android:name="ServiceName" />
 * 
 * locationM.removeUpdates(this);　LocationSeviceの停止
 * 書き忘れるとServeceが終わらない上、延々と受け取る上にminsecの設定が無視されて送られてくる？
 * 何コレ怖い　基本的に記述すべき
 * 
 * 
 * 
 * 
 */
public class LocationService extends Service implements LocationListener{
	
	private KeyguardLock keylock;
	private WakeLock wakelock;
	private LocationManager location;
	private Timer timer;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	/*
	 *
	 *　サービス(メソッド)開始から終了時まで一度しか呼ばれない
	 *常駐してるからあたりまえか
	 */
	@Override
	public void onCreate() {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate();
		
		//Toast.makeText(this, "create service", Toast.LENGTH_SHORT).show();
		
		
		//SharedPreferences から設定時間を読み込みセット　nullの場合は１５分に
		
		
		
				
	}
	

	@Override
	public void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
		Log.d("Service","終了");
		//Toast.makeText(this, "サービス終了", Toast.LENGTH_SHORT).show();
	}

	/*
	 * 
	 * メソッドの重複実行確認
	 * ただしThreadは単一のみ？
	 * １０秒ごとにメソッドを実行しても
	 * 最初に呼ばれてから設定した秒数で終了する
	 * 
	 * 現在常駐型なので関係なし
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart(intent, startId);
				
		this.startLocationService();
		
	}
	
	public void setRequestLocation(long i){
		//LocationManagerの取得 位置情報サービス取得
		location = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		/*位置情報が変化したときのリスナの登録　requestLocationUpdates(サービスGPS/3G/Wifi,位置情報の更新間隔ms,位置情報の最低更新距離m,登録するリスナ);
		秒数をセットしただけだと怒涛の勢いで更新されたので　距離も1mに設定
		*/
		location.requestLocationUpdates(LocationManager.GPS_PROVIDER, i, 1,this);
		location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, i, 1,this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ
		
		
		GeoPoint2 gp = new GeoPoint2((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");
		DBHepler dbh = new DBHepler(this,sdf1.format(date));
		SQLiteDatabase db = dbh.getReadableDatabase();
		

		//現在の日付のテーブルが無ければ作成
		Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' and name='"+sdf1.format(date)+"'", null);
		boolean isEof = c.moveToFirst();
		if(!isEof)
			dbh.dbTableCreate(db);
		
		
		c = db.query(sdf1.format(date),new String[]{"Longitude","Latitude","MapDate"},null,null,null,null,null);
		isEof = c.moveToLast();
		GeoPoint oldgp=null;
		if(isEof){
			oldgp= new GeoPoint(c.getInt(1),c.getInt(0));
			Log.d("DataBase",gp.toString()+"old:"+oldgp.toString());
			if(c.getString(2)==null)
				Log.d("service","ぬるぽ");
			else
				Log.d("service",c.getString(2));
		}
		if(!gp.equalsGP(oldgp)){//最後に書き込んだGPと比較し違ったら書き込む
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH':'mm");
			//GPと時刻のみ書き込み　メッセージ・アイコンは任意なのでnullで対処
			dbh.databaseInsert(db,gp,sdf.format(Calendar.getInstance().getTime()),null,null);
		}

		dbh.close();
		
		//このクラスではないロケーションサービスの停止 
		this.location.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
	public long readPreferences(){
		SharedPreferences prefs = getSharedPreferences("Maps", MODE_PRIVATE);
		Log.d("prefs",String.valueOf(prefs.getLong("time",0)));
		return prefs.getLong("time", LocationMinute.MINUTE15);
		
	}
	
	public void lockReleace(){
		//スリープの解除　PowerManager.で解除後の状態の設定  PARTIAL_WAKE_LOCK->CPUのみON
		this.wakelock = ((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | 
				
				PowerManager.ON_AFTER_RELEASE, "disableLock");
		wakelock.acquire();
		
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		keylock = keyguardManager.newKeyguardLock("disableLock");
		this.keylock.disableKeyguard();
		
		
		this.wakelock.release();
		this.keylock.reenableKeyguard();
		
		Log.d("Service","ロックしました");
	}
	
	public void saveDatabase(){
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");
		DBHepler dbh = new DBHepler(this,sdf1.format(date));
		SQLiteDatabase db = dbh.getReadableDatabase();
		
		//現在の日付のテーブルが無ければ作成
		Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' and name='"+sdf1.format(date)+"'", null);
		boolean isEof = c.moveToFirst();
		if(!isEof)
			dbh.dbTableCreate(db);
	}
	
	public void startLocationService(){
		this.timer = new Timer();
		final Handler handler = new Handler();
		
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO 自動生成されたメソッド・スタブ
						
						try{

							setRequestLocation(readPreferences());
							lockReleace();
							

							//stopSelf();//このサービスの終了

						}catch (Exception e) {
							// TODO: handle exception
							Log.d("エラー",e.toString());
						}
						
					}
				});
			}
		}, 0,this.readPreferences());
	}
}
