package com.example.snsmap;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class MainMap extends MapActivity implements Callback,LocationCallback{
	//GP取れたのでサバへ反映すべし
	static final String MODE_SHOW = "show";
	static final String MODE_LOGIN = "login";
	static final String MODE_CREATE = "create";
	static final String MODE_UPDATE = "update";
	static final String MODE_DELETE = "delete";
	static final String URL = "http://suwashimizu.ap01.aws.af.cm/start/snsmap.php";

	private final int GROUP = 1;
	private final int PASS = 2;

	private AlertDialog dlg;
	private ReadGroupList group;
	private Button nowBtn;

	//Asyncで使うrequestCode
	private static final int GROUP_LIST = 1;//mode,show
	private static final int MEMBER_LIST = 2;//mode,login
	private static final int CREATE_CODE = 3;//mode,create
	public static final int SEND_DATE = 4;//mode,create
	public static final int DELETE_DATE = 5;//mode,delete

	public static String groupName;

	private Button countBtn;
	private TextView groupText;
	private LocationOverlay location;
	private GeoPoint gp;

	private MapView mapView;
	private MapController controller;

	public static int width,height,iconSize;

	//create CODE一覧
	private static final String SAME_NAME = "-101";
	private static final String CREATE_OK = "101";
	public static final String MY_DATE_INSERT ="301";
	public static final String MY_DATE_UPDATE = "302";
	public static final String SEND_MISS="-301";
	public static final String NO_GROUP="-302";
	public static final String NOT_CONECUT = "-1";
	public static final String NOT_IN_GROUP = "-303";
	public static final String MISSMATH_PASS = "-201";
	public static final String DELETE_OK="501";

	private MyOverlay myOverlay;
	private GroupMemberOverlay memberOverlay;
	private HelpOverlay helpOverlay;
	private boolean helpIs=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mapView = (MapView)findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		controller = mapView.getController();

		//グループリストの取得
		Button btn = (Button)findViewById(R.id.btnShow);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				group = new ReadGroupList(MainMap.this,MainMap.this,GROUP_LIST);
				group.execute(MODE_SHOW);
			}
		});

		//グループの作成
		btn = (Button)findViewById(R.id.btnNew);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LayoutInflater inflater = LayoutInflater.from(MainMap.this);
				final View view = inflater.inflate(R.layout.creategroup, null);

				new AlertDialog.Builder(MainMap.this).setTitle("グループの作成")
				.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText group = (EditText)view.findViewById(R.id.editText1);
						if("".equals(group.getText().toString()))
							Toast.makeText(MainMap.this, R.string.noname_group,Toast.LENGTH_SHORT).show();
						else{
							String groupName = group.getText().toString();

							group = (EditText)view.findViewById(R.id.editText2);
							String groupPass = group.getText().toString();
							PostCreateGroup postCreateGroup = new PostCreateGroup(MainMap.this, CREATE_CODE);
							postCreateGroup.execute(MODE_CREATE,groupName,groupPass);
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
			}
		});

		//更新ボタン
		btn = (Button)findViewById(R.id.textGroup);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str[] = readGroupAndPass();
				if(str[0] != null){
					Group g = new Group(str[0], str[1]);
					GroupLogin groupLogin = new GroupLogin(g, MainMap.this, MEMBER_LIST);
					groupLogin.execute(MODE_LOGIN,str[1]);
				}
			}
		});

		//アイコンの変更暫定
		btn = (Button)findViewById(R.id.btnConfig);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMap.this,SettingListActivity.class);
				startActivity(intent);
			}
		});

		//ヘルプボタンの設定
		btn = (Button)findViewById(R.id.btnHelp);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(helpIs){
					mapView.getOverlays().remove(helpOverlay);
				}else{
					if(helpOverlay == null)
						helpOverlay = new HelpOverlay(MainMap.this, mapView,countBtn);
					mapView.getOverlays().add(helpOverlay);
				}
				helpIs = !helpIs;
				mapView.invalidate();
			}
		});

		//人数ボタンを押した時にリストを表示する
		countBtn = (Button)findViewById(R.id.btnCount);
		countBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(memberOverlay!=null){
					final Person[] persons = memberOverlay.getMember();
					ShowList list = new ShowList(MainMap.this, persons);
					final AlertDialog dialog = new AlertDialog.Builder(MainMap.this).setView(list).show();;

					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							GeoPoint gp =persons[position].getGp();
							controller.setCenter(gp);
							dialog.dismiss();
						}
					});

				}
			}
		});

		location = new LocationOverlay(this, mapView);

		nowBtn = (Button)findViewById(R.id.btnNow);
		nowBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				nowBtn.setEnabled(false);
				location.enableMyLocation();
			}
		});		

		
			groupText = (TextView)findViewById(R.id.textGroup);
		if(readPref(GROUP) != null){
			groupName = readPref(GROUP);
			groupText.setText(groupName);
		}

		Display disp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();		
		//幅の小さい方を基準にする
		iconSize = width < height? width/10:height/10;		
		

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		location.disableMyLocation();
		nowBtn.setEnabled(true);
	}

	@Override
	public void callback(int responceCode, int requestCode,
			Object... result) {
		if(responceCode == Callback.ERROR){
			Toast.makeText(this, R.string.toast_not_sesson, Toast.LENGTH_SHORT).show();
		}else{
			if(GROUP_LIST == requestCode){
				Group[] group = (Group[])result;
				Log.d("main","GROUP_LISTcallbackkkkkkkkkkkkkkk");

				ShowList list = new ShowList(this,group);
				dlg = new AlertDialog.Builder(this).setView(list).show();
				list.setOnItemClickListener(new OnItemClickListener() {

					//グループへのログイン
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ListView list = (ListView)parent;
						Group g = (Group)list.getItemAtPosition(position);
						/*選択したリストがグループ名となる PASSも一緒にPOSTしてmemberListをGetすればおｋ
					パス無しは空文字投げればいいか
					サーバサイドでパスの成否すべき*/

						if(!g.pass.equals("")){
							dlg.dismiss();
							setPass(g);

						}else{

							groupName = g.name;
							writePref(PASS, "");
							GroupLogin login = new GroupLogin(g, MainMap.this, MEMBER_LIST);
							login.execute(MODE_LOGIN,"");
							//Toast.makeText(MainMap.this, item, Toast.LENGTH_SHORT).show();
							dlg.dismiss();
						}
					}
				});
			}
			if(MEMBER_LIST == requestCode || SEND_DATE == requestCode){
				//グループへのログインへ成功
				Log.d("main","MEMBER_LISTcallbackkkkkkkkkkkkkkk");

				if(result != null){
					//パスミス
					if(result[0].equals(MISSMATH_PASS)){
						groupName = getResources().getString(R.string.text_group);
						Toast.makeText(this, R.string.pass_missmatch, Toast.LENGTH_SHORT).show();
						return;
					}
					writePref(GROUP, groupName);
					//if(memberOverlay!=null)
					mapView.getOverlays().remove(memberOverlay);

					//if(myOverlay!=null)
					mapView.getOverlays().remove(myOverlay);
					//else
					myOverlay = new MyOverlay(this, mapView);

					if(result[0].equals("0")){

						//mapView.getOverlays().clear();
						countBtn.setText(0+""+getResources().getString(R.string.btn_count));
						mapView.getOverlays().add(myOverlay);
					}else{

						countBtn.setText(((Person)result[0]).getIconNumber()+""+getResources().getString(R.string.btn_count));

						Person[] persons = new Person[result.length-1];
						for(int i=1;i<result.length;i++){
							persons[i-1] = (Person) result[i];
						}
						//mapView.getOverlays().clear();
						mapView.getOverlays().add(myOverlay);

						memberOverlay = new GroupMemberOverlay(MainMap.this,persons);
						mapView.getOverlays().add(memberOverlay);



					}
					mapView.invalidate();
					groupText.setText(groupName);
				}
			}
			if(CREATE_CODE == requestCode){
				if(result == null){
					Toast.makeText(this, R.string.notAcces, Toast.LENGTH_SHORT).show();
					return;
				}
				if(SAME_NAME.equals(result[0])){
					Toast.makeText(this, R.string.CREATE_SAME_NAME, Toast.LENGTH_SHORT).show();
					return;
				}
				if(CREATE_OK.equals(result[0])){
					Toast.makeText(this, (String)result[1]+getResources().getString(R.string.CREATE_GROUP), Toast.LENGTH_SHORT).show();
				}
			}
			
			if(DELETE_DATE == requestCode){
				if(result == null){
					Toast.makeText(this, R.string.notAcces, Toast.LENGTH_SHORT).show();
					return;
				}
				String echo = (String)result[0];
				if(echo.equals(DELETE_OK))
					Toast.makeText(this, R.string.TOAST_DELETE, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void setPass(final Group g){


		final EditText et = new EditText(MainMap.this);
		et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		InputFilter[] inputFilters = new InputFilter[1];
		inputFilters[0] = new InputFilter.LengthFilter(12);
		et.setFilters(inputFilters);


		new AlertDialog.Builder(MainMap.this).setMessage(R.string.edit_pass_set)
		.setView(et).setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				groupName = g.name;
				writePref(PASS, et.getText().toString());
				GroupLogin login = new GroupLogin(g, MainMap.this, MEMBER_LIST);
				login.execute(MODE_LOGIN,et.getText().toString());
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				;
			}
		}).show();
	}

	@Override
	public void locationCallback() {
		gp = location.getMyLocation();
		if(gp != null)
			controller.setCenter(gp);
		nowBtn.setEnabled(true);
	}

	private void writePref(int code,String str){
		if(code == GROUP){
			SharedPreferences prefs = getSharedPreferences("group",MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("group", str);
			editor.commit();
		}
		if(code == PASS){
			SharedPreferences prefs = getSharedPreferences("pass",MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("pass", str);
			editor.commit();
		}
	}

	private String readPref(int code){
		if(code == GROUP){
			SharedPreferences prefs = getSharedPreferences("group",MODE_PRIVATE);
			return prefs.getString("group", null);
		}
		if(code == PASS){
			SharedPreferences prefs = getSharedPreferences("pass", MODE_PRIVATE);
			return prefs.getString("pass", "");
		}
		return null;
	}

	private String[] readGroupAndPass(){
		String[] str = new String[2];
		str[0] = readPref(GROUP);
		str[1] = readPref(PASS);
		return str;
	}
}
