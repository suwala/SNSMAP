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
 * Activity�����L������Service���g���Č��ݒn�𓾂�
 * 
 * Manifest��Service�����L�q���� <service android:name="ServiceName" />
 * 
 * locationM.removeUpdates(this);�@LocationSevice�̒�~
 * �����Y����Servece���I���Ȃ���A���X�Ǝ󂯎����minsec�̐ݒ肪��������đ����Ă���H
 * ���R���|���@��{�I�ɋL�q���ׂ�
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	/*
	 *
	 *�@�T�[�r�X(���\�b�h)�J�n����I�����܂ň�x�����Ă΂�Ȃ�
	 *�풓���Ă邩�炠����܂���
	 */
	@Override
	public void onCreate() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate();
		
		//Toast.makeText(this, "create service", Toast.LENGTH_SHORT).show();
		
		
		//SharedPreferences ����ݒ莞�Ԃ�ǂݍ��݃Z�b�g�@null�̏ꍇ�͂P�T����
		
		
		
				
	}
	

	@Override
	public void onDestroy() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onDestroy();
		Log.d("Service","�I��");
		//Toast.makeText(this, "�T�[�r�X�I��", Toast.LENGTH_SHORT).show();
	}

	/*
	 * 
	 * ���\�b�h�̏d�����s�m�F
	 * ������Thread�͒P��̂݁H
	 * �P�O�b���ƂɃ��\�b�h�����s���Ă�
	 * �ŏ��ɌĂ΂�Ă���ݒ肵���b���ŏI������
	 * 
	 * ���ݏ풓�^�Ȃ̂Ŋ֌W�Ȃ�
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onStart(intent, startId);
				
		this.startLocationService();
		
	}
	
	public void setRequestLocation(long i){
		//LocationManager�̎擾 �ʒu���T�[�r�X�擾
		location = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		/*�ʒu��񂪕ω������Ƃ��̃��X�i�̓o�^�@requestLocationUpdates(�T�[�r�XGPS/3G/Wifi,�ʒu���̍X�V�Ԋums,�ʒu���̍Œ�X�V����m,�o�^���郊�X�i);
		�b�����Z�b�g�����������Ɠ{���̐����ōX�V���ꂽ�̂Ł@������1m�ɐݒ�
		*/
		location.requestLocationUpdates(LocationManager.GPS_PROVIDER, i, 1,this);
		location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, i, 1,this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		
		GeoPoint2 gp = new GeoPoint2((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");
		DBHepler dbh = new DBHepler(this,sdf1.format(date));
		SQLiteDatabase db = dbh.getReadableDatabase();
		

		//���݂̓��t�̃e�[�u����������΍쐬
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
				Log.d("service","�ʂ��");
			else
				Log.d("service",c.getString(2));
		}
		if(!gp.equalsGP(oldgp)){//�Ō�ɏ�������GP�Ɣ�r��������珑������
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH':'mm");
			//GP�Ǝ����̂ݏ������݁@���b�Z�[�W�E�A�C�R���͔C�ӂȂ̂�null�őΏ�
			dbh.databaseInsert(db,gp,sdf.format(Calendar.getInstance().getTime()),null,null);
		}

		dbh.close();
		
		//���̃N���X�ł͂Ȃ����P�[�V�����T�[�r�X�̒�~ 
		this.location.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
	
	public long readPreferences(){
		SharedPreferences prefs = getSharedPreferences("Maps", MODE_PRIVATE);
		Log.d("prefs",String.valueOf(prefs.getLong("time",0)));
		return prefs.getLong("time", LocationMinute.MINUTE15);
		
	}
	
	public void lockReleace(){
		//�X���[�v�̉����@PowerManager.�ŉ�����̏�Ԃ̐ݒ�  PARTIAL_WAKE_LOCK->CPU�̂�ON
		this.wakelock = ((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | 
				
				PowerManager.ON_AFTER_RELEASE, "disableLock");
		wakelock.acquire();
		
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		keylock = keyguardManager.newKeyguardLock("disableLock");
		this.keylock.disableKeyguard();
		
		
		this.wakelock.release();
		this.keylock.reenableKeyguard();
		
		Log.d("Service","���b�N���܂���");
	}
	
	public void saveDatabase(){
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");
		DBHepler dbh = new DBHepler(this,sdf1.format(date));
		SQLiteDatabase db = dbh.getReadableDatabase();
		
		//���݂̓��t�̃e�[�u����������΍쐬
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
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO �����������ꂽ���\�b�h�E�X�^�u
						
						try{

							setRequestLocation(readPreferences());
							lockReleace();
							

							//stopSelf();//���̃T�[�r�X�̏I��

						}catch (Exception e) {
							// TODO: handle exception
							Log.d("�G���[",e.toString());
						}
						
					}
				});
			}
		}, 0,this.readPreferences());
	}
}
