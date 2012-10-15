package com.example.snsmap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

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
import org.xmlpull.v1.XmlPullParser;

import com.example.snsmap.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.R.array;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.support.v4.app.NavUtils;

//DB関連のバグ取り終了？　鯖への転送の仕様固まらず


public class MainActivity extends MapActivity implements LocationListener{//googleMapを使う際はMapActivityを継承する

	/*
	 * Longitudeが経度
	 * Latitudeが緯度
	 * 
	 * Overlayを使いMAPに画像を書き込む
	 * 仕様変更があったらしく古い書き方に注意
	 * 
	 * 
	 * 現在初期位置横浜：更新時間3分
	 * 
	 * distance(GeoP,GeoP)で二点の距離が求められるらしい
	 * 
	 * LocationManeger.removeUpdates(this);で位置情報リクの停止
	 * 
	 * Location(引数)　の引数は何に使うのか
	 * 
	 * protected Dialog onCreateDialog(int id)を使って
	 * ダイアログにレイアウトを流し込むことに成功
	 * グリッドビューを流し込んでアイコンリストが作れるかも
	 * 
	 * #getView内でのIconListData[] list = IconListData.values();
	 * list[posion]なんかはよく使う形っぽいので覚えておこう
	 * 
	 * Seviceもインスタンス化可能なので
	 * 明示的にメソッドを呼び出せたりもする　MainAcからLocition関係消せる？
	 * CallBackを実装するとService←→Actiでの通信が出来る
	 */


	/*未実装
	 *
	 * caseの除外?
	 */

//-----Overlayが2つある状態になってるので統一させる OverlayPlusに統一中
//アイコンの追加方法をどうするか

	private MapView map;
	//private ArrayList<Location> oldLocation = new ArrayList<Location>();
	//private PinItemizedOverlay itemovarlay;
	//private ArrayList<GeoPoint> gp = new ArrayList<GeoPoint>();
	public static int zoom = -1;
	private LocationManager lastLocation;
	private ArrayList<OverlayItems> items=new ArrayList<OverlayItems>();
	private GeoPoint nowGp;//現在地ボタンを押した時に格納されるGP
	private Button btn;
	private static Calendar calendar = Calendar.getInstance();//この時点でその日の日付がセットされる
	private SimpleDateFormat sdf = new SimpleDateFormat("'D'yyMMdd");
	private Date oldDate;
	private Boolean nowFlag=false; //現在地を求める時のフラグ
	//private int icon = R.drawable.icon01;//使用するアイコン
	private int iconNum = 1;
	private String user,userName;
	
	private final int USER_PREFS = 0,USER_NAME=1;
	private ArrayList<OverlayItems> friend = new ArrayList<OverlayItems>();
	
	
	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
		zoom = map.getZoomLevel();
		this.writePreferences();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("DRAW",calendar.getTime().toString());
		
		//Prefsに保存したUserの名前を読み込み表示
		this.readPreferences(this.USER_PREFS);
		((TextView)findViewById(R.id.userName)).setText("ID:"+this.user);
		
		map = (MapView)findViewById(R.id.mapview);
		MapController c = map.getController();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		
		//作動中のサービスをチェックし　動いてなければ起動する
		this.isService();

		
		Log.d("oncre",(new Date(System.currentTimeMillis()).toString()));

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");
		
		DBHepler dbh = new DBHepler(this,sdf1.format(date));
		SQLiteDatabase db = dbh.getReadableDatabase();
		boolean isEof;
		
		Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);
		String str;
		sdf1=new SimpleDateFormat("yyMMdd");
		isEof = cursor.moveToFirst();
		
		
		while(isEof){//テーブル名が帰ってくる
			try{
				str = cursor.getString(1).replace("D", "");
				//str = str.replace("D", "");
				this.oldDate = sdf1.parse(str);
				Log.d("onCre",str);
			}catch (Exception e) {
				// TODO: handle exception
				Log.d("onCre","Date型に変換できませんでした");
			}
			isEof = cursor.moveToNext();
		}
		
		
		
		dbh.close();
		

		if(zoom == -1)
			c.setZoom(15);//ズーム値の設定
		else
			c.setZoom(zoom);
		
		//c.setCenter(new GeoPoint(35455281,139629711));//現在地の設定　引数はGeoPoint　この場合は東京になる

		/*非推奨のやり方　.setBuilt～を使おう
        ZoomControls zc = (ZoomControls)map.getZoomControls();
        zc.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        zc.setGravity(Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL);//画面下に表示されるズームウィジェットの設置関係
        map.addView(zc);
		 */

		
		
		this.btn=new Button(this);
		this.btn.setText("現在地の取得");
		this.btn.setTextSize(10);
		

		/*
		final Button btn2 = new Button(this);
		//日付を切り替えるボタン
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				
				
			}
		});
		*/
		LinearLayout linear_layout = new LinearLayout(this);
		View view = getLayoutInflater().inflate(R.layout.buttons, linear_layout);
		
		LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		//btn2.setLayoutParams(param);
		//btn2.setGravity(Gravity.RIGHT+Gravity.RIGHT);
		this.map.addView(view,param);
		
		/*
		this.btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.btn.setGravity(Gravity.TOP+Gravity.CENTER_HORIZONTAL);
		this.map.addView(btn);
		this.map.setBuiltInZoomControls(true);
		*/
		
		
		Calendar calendar2 = Calendar.getInstance();
		
		//日付のみを比較したいので　HMSを初期化する
		calendar2.set(Calendar.HOUR_OF_DAY,0);
		calendar2.set(Calendar.MINUTE,0);
		calendar2.set(Calendar.SECOND,0);
		calendar2.set(Calendar.MILLISECOND,0);
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		
		//日付をプラスするボタンで現在の日付を越えないようにする(同日なら押せなくする)
		if(calendar2.equals(calendar)){
			Button btn2 = (Button)findViewById(R.id.button2);
			btn2.setEnabled(false);
		}
			
			
		
		/*無名インナークラスの場合
		 * 
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ

				LocationManager locationManager = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
				Location myLocate = locationManager.getLastKnownLocation("gps");
				if(myLocate!=null){
					GeoPoint gp = new GeoPoint((int)(myLocate.getLatitude()*1e6), (int)(myLocate.getLongitude()*1e6));
					c.animateTo(gp);
				}
			}
		});*/

		
		/*
		 * MapControllerを内部に持つボタンの作成
		 * ボタン内部のｃはアドレスが一緒なので
		 * Activityのｃが変化しても追い続ける
		 * 
		 * new OnClickListener() {}の形では無名インナークラスとなりローカル変数を渡せない
		 * デフォオルトコンストラクタしかないので
		 * コンストラクタを通しての引渡しも出来ない
		 * 
		 * 新たに有名(?)クラスを作りOnClickListenerをインターフェースで継承し
		 * 引数を持ったデフォルトコンストラクタを作る　class名onClickButton
		 * classを作ったら
		 * 
		 * btn.setOnClickListener (new onClickButton(c));で実装
		 * 
		 */
		
		class onClickButton extends MainActivity implements OnClickListener{

			//private ArrayList<GeoPoint> gp;
			private Context context;
			private MainActivity activity;
			
			public onClickButton(Context context){
				
				//this.gp = mainActiovoty.gp;
				this.context = context;
				this.activity = (MainActivity)context;
				
			}
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ

				/*locationManager.getLastKnownLocationを使って現在地を取得
				locationに入れてgeopointを取り出す
				取得できない場合はエラー落ちするので対策を練ること
				 */

				btn.setEnabled(false);
				lastLocation = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
				
				lastLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, activity);
				lastLocation.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, activity);
				
				Log.d("DRAW",calendar.getTime().toString());
				
				/*
				
				if(location!=null){
					GeoPoint gp = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
					MapController mapcon = map.getController();
									
					//インスタンスが違う？のでthisだとエラーになる　MainActiviのインスタンスを引き継いでメソッドを呼び出す
					drawOverlay();
					
					mapcon.animateTo(gp);
					Log.d("Button",String.valueOf(gp));
				}else{
					Toast.makeText(MainActivity.this, "現在地を取得できません", Toast.LENGTH_LONG).show();
				}
				
				lastLocation.removeUpdates(activity);
				*/
			}
		}

		
		
		this.btn.setOnClickListener (new onClickButton(this));

		param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		this.btn.setLayoutParams(param);
		this.btn.setGravity(Gravity.TOP+Gravity.CENTER_HORIZONTAL);
		this.map.addView(this.btn);
		this.map.setBuiltInZoomControls(true);
		
		
		

		
		
		
		//LocationManagerの設定　GPS更新時間をPreferencesから読み込む　無い場合は15分に
		//this.setRequestLocation(readPreferences());
		
		Log.d("prefsの中身",String.valueOf(this.readPreferences()));
		
		/*初期位置を東京へ
        GeoPoint tokyo = new GeoPoint(35681396, 139766049);
        c.animateTo(tokyo);
		 */
		
/*
		try{
			DBHepler dbh = new DBHepler(this);
			SQLiteDatabase db = dbh.getReadableDatabase();
			boolean isEof;

			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");

			//テーブル名一覧を返すクエリ	なかった場合は新たにテーブルを作成
			Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);
			isEof = cursor.moveToFirst();
			
			String str = "";
			
			while(isEof){//テーブル名が帰ってくる
				str += cursor.getString(1)+":";
				isEof = cursor.moveToNext();
			}
			
			Log.d("onCre",str);
			
			cursor.close();
			dbh.close();
			db.close();
			
			//DB->GPへ読み込むメソッド
			this.ReadDataBase(sdf1.format(date));
			
		}catch (Exception e) {
			// TODO: handle exception
		}

		
        /*東京と大阪のGeoPointを設定しOverlayに描画

        GeoPoint tokyo = new GeoPoint(35681396, 139766049);
        GeoPoint osaka = new GeoPoint(34701895, 135494975);
        pinOverlay.addPoint(tokyo);
        pinOverlay.addPoint(osaka);
		 */
		
		//zoomが-1の時　サービスの起動間隔を3分にする
		if(this.readPreferences()==0){
			this.writePreferences(LocationMinute.MINUTE3);
			this.isService();
		}
		
		this.friendGetName();
		this.setUserName();
		
			
	}

	
	
	
	@Override
	protected void onStart() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart();

		SimpleDateFormat sdf1 = new SimpleDateFormat("'D'yyMMdd");

		
		this.ReadDataBase(sdf1.format(calendar.getTime()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}



	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ

		if(item.getItemId() == R.id.menu_settings){//フレンド登録

			final EditText input = new EditText(this);
			InputFilter[] filters = {new MyFilter(8)};
			input.setFilters(filters);//MyFilterで8文字まで英数字のみ入力できるようにする
			
			
			new AlertDialog.Builder(this).setTitle("登録したいIDを入力してください")
				.setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ
						if(input.getText().toString().length() <6){
							Toast.makeText(MainActivity.this, "IDは6文字以上です", Toast.LENGTH_SHORT).show();
							
						}else{
							setFriend(input.getText().toString());
						}
						
						
					}
				}).show();
			
		}else if(item.getItemId() == R.id.menu_settime){//更新時間の設定
			Intent i = new Intent(this,TimeList.class);
			this.startActivityForResult(i, 0);
			
			
		}else if(item.getItemId() == R.id.menu_friend){//フレンド一覧の表示

			Log.d("hriend",String.valueOf(this.friend.size()));
			Intent intent = new Intent(this,LogList.class);
			
			String[] str = new String[this.friend.size()];
			int i=0;
			for(OverlayItems items:this.friend){
				str[i] = items.getFriendName();
				i++;
			}
						
			intent.putExtra("friend",str);//ArrayListを渡す
			this.startActivityForResult(intent, 1);
			
			
		}else if(item.getItemId() == R.id.menu_gpout){//ログの出力
			
			ArrayList<String> list = new ArrayList<String>();
			
			for(OverlayItems gp:this.items){
				
				list.add(gp.getDate()+" "+gp.getGeoPoint().toString());
				
			}
			
			SdLog sdlog = new SdLog(list);
			sdlog.outFile();
			
			Toast.makeText(this,sdlog.getFileName()+"に保存しました", Toast.LENGTH_LONG).show();
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);


		/*呼び出し先のアクティビティでsetResult(RESULT_OK, i);を実行すると
		 * resultCodeに結果が入るRESULT_OK成功　RESULT_CANCELEDキャンセルなど
		 * resultCodeは成否の分岐に使える?
		 * 
		 * .startActivityForResult(intent, x);xがrequset_code
		 * xの値で分岐可能
		 * 
		 * 
		 * dataには呼び出し先のアクティで保持してたインテントがあれば入る
		 * Intent.put(key,value);で保存
		 *  data.getStringExtra(name)などで取り出す
		 *  
		 * */
		if(resultCode == RESULT_OK){
			if(requestCode == 0){//更新時間の設定

				//this.setRequestLocation(data.getLongExtra("time", 0));
				Log.d("time",String.valueOf(data.getLongExtra("time", 0)));
				if(this.readPreferences() != data.getLongExtra("time", 0)){
					this.writePreferences(data.getLongExtra("time", this.readPreferences()));

					//新たにサービスをスタートさせる
					this.isService();
				}
				
			}else if(requestCode == 1){//選択したテーブルの値を受け取り　フレンドの位置へ飛ぶ
				
				int friendNum = data.getIntExtra("friendList", 0);
				MapController c = map.getController();
				c.animateTo(this.friend.get(friendNum).getGeoPoint());
				
				
				this.buttonOnOff();
				
			}
		}
	}

	//とりあえず必須
	@Override
	protected boolean isRouteDisplayed() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	
	//Locationが実装されているか？GPS機能が無いとLocationが動かない(?)
	public void updateDisplay(Location location){
		if(location==null){
			Log.e("HelloLocation","location is null");
			return;
		}else
			Log.e("HelloLocation","location is not null");
	}


	//Overlayクリアメソッド　map.invalidate();でOverlayの再描画を忘れずに
	public void pinClearS(){

		Log.d("clear","けします");
		this.map.getOverlays().clear();
		this.map.invalidate();
	}


	public long readPreferences(){
		SharedPreferences prefs = getSharedPreferences("Maps", MODE_PRIVATE);
		return prefs.getLong("time", LocationMinute.MINUTE3);
	}
	
	public void readPreferences(int i){
		if(i == this.USER_PREFS){
			SharedPreferences prefs = getSharedPreferences("snsmap", MODE_PRIVATE);
			this.user = prefs.getString("user", null);
			this.iconNum = prefs.getInt("iconNum", 1);
		}
		
		if(i == this.USER_NAME){
			SharedPreferences prefs = getSharedPreferences("snsmap", MODE_PRIVATE);
			
			//Login画面でのprefsを取得しユーザー名をゲット
			this.userName = prefs.getString(prefs.getString("user", null), "");
			((TextView)findViewById(R.id.textView2)).setText(" NAME:"+this.userName);
			
		}
	}
	
	public void writePreferences(long l){
		SharedPreferences prefs = getSharedPreferences("Maps", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("time", l);
		editor.commit();
	}
	
	public void writePreferences(){
		SharedPreferences prefs = getSharedPreferences("snsmap", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("iconNum", this.iconNum);
		editor.commit();
	}
	
	public void writePreferences(int i){
		if(i == this.USER_NAME){
			SharedPreferences prefs = getSharedPreferences("snsmap", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			
			editor.putString(prefs.getString("user", null), this.userName);
			editor.commit();
		}
	}
	
	//指定したGP上のoverlayに画像を書き込むメソッド
	public void drawOverlay(){
		
		
		//Drawbleを引数にしてPinItemizedOverlayのコンストラクタに渡す
		//this.itemovarlay = new PinItemizedOverlay(getResources().getDrawable(R.drawable.icon01),this);

		/*map.getOverlays().add()メソッドでMapViewのオーバーレイにpinOverlayを描画　
        pinOverlayはListでGeoPointを保持している　その保持しているポイント全てを描画する
        pinOverlayはローカルなのだからリストで保持する意味はあるのか? -> インスタンス変数に変更
		   
		
		
		this.itemovarlay.addPoint(this.nowGp);
		map.getOverlays().add(this.itemovarlay);
		map.getOverlays().clear();*/
		
		//addPoint(gp)メソッドでgpの位置に描画
		
		
		//Overlayを拡張したplusで現在地のGPを渡し描画する
		Log.d("main",String.valueOf(this.iconNum));
		OverlayPlus plus = new OverlayPlus(this, this.iconNum,this.items,this.nowFlag,calendar.getTime(),this.friend,this.userName);
		plus.addGp(nowGp);
		map.getOverlays().add(plus);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		Log.d("Main",String.valueOf(event.getX())+":"+String.valueOf(event.getY()));
		return super.onTouchEvent(event);
	}

	
	//setView()を使い独自のレイアウトを使ったダイアログを作成する
	//何故かOverlayPlusからしか呼ばれてない
	protected void onCreateDialog2(){

		//GridViewの設定
		GridView gridView = new GridView(this);
		gridView.setNumColumns(GridView.AUTO_FIT);
		gridView.setVerticalSpacing(50);
		gridView.setColumnWidth(70);
		
			
		final AlertDialog dia = new AlertDialog.Builder(this).create();
		
		//adapterの設定
		ImageAdapter image = new ImageAdapter(this);
		Drawable drawble;
		for(IconListData i:IconListData.values()){
			drawble = getResources().getDrawable(i.getId());
			image.add(drawble);
		}
		gridView.setAdapter(image);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				
				
				//IconListData[] list = IconListData.values();//Enumを配列へセットよく使うらしい
				iconNum = arg2+1;//クリックしたアイコンのＩＤをセット
				dia.dismiss();//対象のダイアログを閉じるメソッド
				Log.d("MAin",String.valueOf(arg2)+":"+String.valueOf(iconNum));
				
			}
		});
		
		dia.setView(gridView);
		dia.show();
		
		//↓だとsetViewからのメソッドチェーンが出来るが、↑だと出来ないなんで
		//new AlertDialog.Builder(this).setView(gridView).create().show();
		
		
	}
	
	public void ReadDataBase(String date){
		
		DBHepler dbh = new DBHepler(this,date);
		SQLiteDatabase db = dbh.getReadableDatabase();
		//dbh.test(db);
		
		Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' and name='"+date+"'", null);
		boolean isEof = c.moveToFirst();
		if(!isEof){
			dbh.dbTableCreate(db);
			Log.d("readDB","テーブル作成");
		}
		
		Cursor cursor = db.query(date,new String[]{"Longitude","Latitude","MapDate","Message","Icon"},null,null,null,null,null);
		isEof = cursor.moveToFirst();
		
		
		GeoPoint setGp;

		this.items.clear();
		
		Integer i=0;
		
		while(isEof){
			
			setGp = new GeoPoint(cursor.getInt(1),cursor.getInt(0));
			
			OverlayItems setItems = new OverlayItems();
			setItems.setGeoPoint(setGp);
			setItems.setDate(cursor.getString(2));
			setItems.setMessage(cursor.getString(3));
			setItems.setIconNum(cursor.getInt(4));
			
			if(cursor.getString(3) != null)
				Log.d("datavase",cursor.getString(3)+":"+i.toString());
			
			this.items.add(setItems);
			
			isEof = cursor.moveToNext();
			i++;
		}
		
		cursor.moveToLast();
		
		
		dbh.close();

		
		Log.d("read",String.valueOf(this.items.size()));
		Log.d("database","全てclose");		
	}
	
	
	public boolean isService(){
		ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
		for(RunningServiceInfo r:serviceList){//.getClassName()でサービス名を取得し.startedで起動状態の確認
			//Log.d("service",LocationService.class.getCanonicalName());
			if(LocationService.class.getCanonicalName().equals(r.service.getClassName())){
				Log.d("serviceチェック","起動済みです");
				return true;
				}
		}
		
		//定期的に実行するAlarmManagerの設定
		Intent intent = new Intent(MainActivity.this,LocationService.class);
		this.startService(intent);
		Log.d("serviceチェック","LService起動しました");

		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ
		
		this.btn.setEnabled(true);
		
		if(location!=null){
			nowGp = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
			calendar = Calendar.getInstance();
			this.buttonOnOff();
			this.map.getOverlays().clear();
			MapController c = map.getController();
			this.nowFlag = true;
			this.drawOverlay();
			c.animateTo(nowGp);
		}else{
			Toast.makeText(this, "現在地を取得できません", Toast.LENGTH_LONG).show();
		}
		
		this.lastLocation.removeUpdates(this);
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
		

	public void dataIs(View v){
		
		if(v.getId() == R.id.button1){//日付をマイナスする
			calendar.add(Calendar.DATE, -1);
		}else if(v.getId() == R.id.button2){
			calendar.add(Calendar.DATE, 1);
		}
		else{
			;
		}
		SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd");
		Toast.makeText(this, simple.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
		
		if(this.buttonOnOff()){
			this.pinClearS();
			this.ReadDataBase(this.sdf.format(calendar.getTime()));

			this.drawOverlay();
		}
	}
	
	public Boolean buttonOnOff(){

		this.nowFlag = false;
		Calendar calendar2 = Calendar.getInstance();
		
		//日付のみを比較したいので　HMSを初期化する
		calendar2.set(Calendar.HOUR_OF_DAY,0);
		calendar2.set(Calendar.MINUTE,0);
		calendar2.set(Calendar.SECOND,0);
		calendar2.set(Calendar.MILLISECOND,0);
		

		//参照している日付がリアルの日付を超えないようにする
		if(calendar2.equals(calendar)||calendar2.before(calendar)){
			Button btn = (Button)findViewById(R.id.button2);
			btn.setEnabled(false);
		}else{
			Button btn = (Button)findViewById(R.id.button2);
			btn.setEnabled(true);
		}		
		
		if(oldDate.equals(calendar.getTime()) || this.oldDate.after(calendar.getTime())){
			Button btn = (Button)findViewById(R.id.button1);
			btn.setEnabled(false);
		}else{
			Button btn = (Button)findViewById(R.id.button1);
			btn.setEnabled(true);
		}
		//LineからIconへ
		//this.mapToLine(this.sdf.format(calendar.getTime()));
		
		return true;
	}
	
	public void mapToLine(String date){
		try{
			if(date!=null){

				Log.d("Line",date);

				this.ReadDataBase(date);

				this.pinClearS();



				//fに移動距離を計算して代入
				Location location = new Location("now");
				Location oldLocation = new Location("old");
				GeoPoint gp;

				Float f=(float) 0;

				for(int i=0;i<this.items.size()-1;i++){


					gp = this.items.get(i+1).getGeoPoint();
					location.setLatitude(gp.getLatitudeE6()/1E6);
					location.setLatitude(gp.getLongitudeE6()/1E6);

					gp = this.items.get(i).getGeoPoint();
					oldLocation.setLatitude(gp.getLatitudeE6()/1E6);
					oldLocation.setLatitude(gp.getLongitudeE6()/1E6);


					f+=location.distanceTo(oldLocation);

				}

				Toast totas = Toast.makeText(this, String.valueOf(f)+"m", 1000);
				totas.show();

				//GP同士を線で結ぶ
				LineOverlay lineOverlay = new LineOverlay(this.items);
				map.getOverlays().add(lineOverlay);
				map.invalidate();

				
				Log.d("ItemsSize",String.valueOf(this.items.size()));
			}

		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void friendGetName(){
		
		String setUrl = this.getResources().getString(R.string.url) + "friendname.php";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams httpParams = httpclient.getParams();
		httpParams.setParameter("http.useragent", "snsmap");//User-agentの設定　通常はブラウザとか端末とか
		HttpPost httppost = new HttpPost(setUrl);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("user", this.user));
		XmlPullParser xmlPP = Xml.newPullParser();
		String friendName=null;
		
		OverlayItems item=new OverlayItems();
		
		try{

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			HttpResponse respone = httpclient.execute(httppost);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			respone.getEntity().writeTo(byteArrayOutputStream);


			if(respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				Log.d("friend",byteArrayOutputStream.toString());

				StringReader strReader = new StringReader(byteArrayOutputStream.toString());
				xmlPP.setInput(strReader);

				Log.d("friend",String.valueOf(this.friend.size()));
				Integer eventType = xmlPP.getEventType();
				while(eventType != XmlPullParser.END_DOCUMENT){
					
					
//					if(eventType == XmlPullParser.START_TAG && "name".equals(xmlPP.getName())){
//						friendName = xmlPP.nextText();//,区切りで戻ってくる
//						Log.d("php",friendName);
//					}
					
					if(eventType == XmlPullParser.START_TAG && "fid".equals(xmlPP.getName()))
						item.setFriendId(xmlPP.nextText());					
					if(eventType == XmlPullParser.START_TAG && "fname".equals(xmlPP.getName()))
						item.setFriendName(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "ficon".equals(xmlPP.getName()))
						item.setIconNum(Integer.valueOf(xmlPP.nextText()));
					if(eventType == XmlPullParser.START_TAG && "fgp".equals(xmlPP.getName()))
						item.setStringToGeoPoint(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "fdate".equals(xmlPP.getName()))
						item.setDate(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "fmsg".equals(xmlPP.getName()))
						item.setMessage(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "end".equals(xmlPP.getName())){
						this.friend.add(item);
						item = new OverlayItems();
						Log.d("friend1",String.valueOf(this.friend.size()));
					}
					
					
					
					
					//次のタグへ
					eventType = xmlPP.next();
				}
				this.drawOverlay();
			}else
				Log.d("newUser","errer");


		}catch (Exception e) {
			// TODO: handle exception
			Log.d("php2",e.toString());
			Toast.makeText(this, "サーバーと接続できません", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setUserName(){
		this.readPreferences(this.USER_NAME);
		if(this.userName.equals("")){
			
			final EditText input = new EditText(this);
			input.setHint("8文字まで入力できます");//setHint　薄い文字で表示されるヤツ
			InputFilter[] filters = new InputFilter[1];
			filters[0] = new InputFilter.LengthFilter(8);
			input.setFilters(filters);//入力できる文字数を8文字までにする
			
			
			new AlertDialog.Builder(this).setTitle("ニックネームを入力してください")
			.setView(input)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					if(input.getText().toString().equals("")){//EditTextに何も入力しないと空文字が入る
						setUserName();
					}else{
						userName = input.getText().toString();
						writePreferences(USER_NAME);
					}
				}
			})
			.show();
			
		}
	}
	
	public void setFriend(String friend){
		
		String setUrl = this.getResources().getString(R.string.url) + "friendreq.php";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams httpParams = httpclient.getParams();
		httpParams.setParameter("http.useragent", "snsmap");//User-agentの設定　通常はブラウザとか端末とか
		HttpPost httppost = new HttpPost(setUrl);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("user", this.user));
		nameValuePairs.add(new BasicNameValuePair("request", friend));
		XmlPullParser xmlPP = Xml.newPullParser();
		
		try{

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			HttpResponse respone = httpclient.execute(httppost);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			respone.getEntity().writeTo(byteArrayOutputStream);


			if(respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				Log.d("friend",byteArrayOutputStream.toString());

				StringReader strReader = new StringReader(byteArrayOutputStream.toString());
				xmlPP.setInput(strReader);

				/*
				Log.d("friend",String.valueOf(this.friend.size()));
				Integer eventType = xmlPP.getEventType();
				while(eventType != XmlPullParser.END_DOCUMENT){
					
					
//					if(eventType == XmlPullParser.START_TAG && "name".equals(xmlPP.getName())){
//						friendName = xmlPP.nextText();//,区切りで戻ってくる
//						Log.d("php",friendName);
//					}
					if(eventType == XmlPullParser.START_TAG && "fid".equals(xmlPP.getName()))
						item.setFriendId(xmlPP.nextText());					
					if(eventType == XmlPullParser.START_TAG && "fname".equals(xmlPP.getName()))
						item.setFriendName(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "ficon".equals(xmlPP.getName()))
						item.setIconNum(Integer.valueOf(xmlPP.nextText()));
					if(eventType == XmlPullParser.START_TAG && "fgp".equals(xmlPP.getName()))
						item.setStringToGeoPoint(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "fdate".equals(xmlPP.getName()))
						item.setDate(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "fmsg".equals(xmlPP.getName()))
						item.setMessage(xmlPP.nextText());
					if(eventType == XmlPullParser.START_TAG && "end".equals(xmlPP.getName())){
						this.friend.add(item);
						item = new OverlayItems();
						Log.d("friend1",String.valueOf(this.friend.size()));
					}
					
					
					
					
					//次のタグへ
					eventType = xmlPP.next();
				}
				this.drawOverlay();
				*/
			}else
				Log.d("newUser","errer");
		}catch (Exception e) {
			// TODO: handle exception
			Log.d("php2",e.toString());
			Toast.makeText(this, "サーバーと接続できません", Toast.LENGTH_SHORT).show();
		}
		
		
		
	}
}
