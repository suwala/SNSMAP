package com.example.snsmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.example.snsmap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	/*
	 * XmlPPでphpの解析に成功
	 * Dialog#show();はバグ持ち？Dia表示中に画面を横にするとメモリリークが発生する件
	 * showDialog()を使えば大丈夫だ問題ない
	 * 
	 * user+passと存在しないuserの判定完了
	 * 新規登録のregistarion.phpがまだ
	 * 
	 */
	private final int CHECK_BOX = 0;
	private final int DIALOG_CHECK = 1;
	private final int USER_PREFS = 2;
	private InputFilter[] filter = {new MyFilter(8)};
	private InputFilter[] filterPass = {new MyFilter(16)};
	private String user,pass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
				
		EditText user = (EditText)findViewById(R.id.logUser);
		user.setFilters(this.filter);
		user = (EditText)findViewById(R.id.logPass);
		user.setFilters(this.filterPass);
		
		this.readPreferences(DIALOG_CHECK);
		
		this.readPreferences(CHECK_BOX);
		

			
	}
	
	public void onClick(View v){
		EditText user = (EditText)findViewById(R.id.logUser);
		TextView tv = (TextView)findViewById(R.id.logErrer);
		String errerMes="";
		if(user.getText().toString().length() < 6){
			errerMes = ("UserNameは6文字以上入れてください\n");
			tv.setTextColor(Color.RED);
			//Intent i = new Intent(this,MainActivity.class);
			//startActivity(i);
		}
		EditText editpass = (EditText)findViewById(R.id.logPass);
		
		if(editpass.getText().toString().length() < 6){
			errerMes = errerMes + "Passwordは6文字以上入れてください";
			tv.setTextColor(Color.RED);
		}
	
		if(errerMes.equals("")){//エラーなし情報をhttpへ
			
			CheckBox box = (CheckBox)findViewById(R.id.logCheckBox);
			//チェックボックスの状態の確認
			if(box.isChecked())
				this.writePreferences(CHECK_BOX);
			
			String name = user.getText().toString();
			String pass = editpass.getText().toString();
			
			
			String setUrl = getResources().getString(R.string.url) + "testver.php";
			Log.d("url",setUrl);
			
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams httpParams = httpclient.getParams();
			httpParams.setParameter("http.useragent", "snsmap");//User-agentの設定　通常はブラウザとか端末とか
			HttpPost httppost = new HttpPost(setUrl);
			
			//POST送信するデータを格納
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("user", name));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			
			XmlPullParser xmlPP = Xml.newPullParser();
			
			
			try{
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
				HttpResponse respone = httpclient.execute(httppost);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				respone.getEntity().writeTo(byteArrayOutputStream);
				
				//鯖からの応答を取得　　XmlPullParserで解析し必要な情報だけ格納する
				if(respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					
					//byteArrya~に全ての文字が格納されるのでそれをStringReader型に入れる
					StringReader strReader = new StringReader(byteArrayOutputStream.toString());
					xmlPP.setInput(strReader);
					
					Log.d("String",byteArrayOutputStream.toString());
					
					Integer eventType = xmlPP.getEventType();	
					Boolean phpFlag=false;
					while(eventType != XmlPullParser.END_DOCUMENT){
						/*
						if(eventType == XmlPullParser.START_DOCUMENT)
							Log.d("xml","Start document");
						else if(eventType == XmlPullParser.END_DOCUMENT)
							Log.d("xml","END Document");
						else if(eventType == XmlPullParser.START_TAG)
							Log.d("xml","Start Tag:"+xmlPP.getName()+":"+xmlPP.getText());
						else if(eventType == XmlPullParser.END_TAG)
							Log.d("xml","END Tag:"+xmlPP.getName());
						else if(eventType == XmlPullParser.TEXT)
							Log.d("xml","TEXT:"+xmlPP.getName());
						*/
						
						
						//php側でflagタグが記述してあるのでそれを参照してログインの成否を判定する
						if(eventType == XmlPullParser.START_TAG && "flag".equals(xmlPP.getName())){//bodyタグのチェック
							if("ok".equals(xmlPP.nextText())){
								Log.d("xmlPP",xmlPP.getText());
								this.user = name;
								phpFlag = true;
							}else if("p_miss".equals(xmlPP.getText())){
								phpFlag = false;
								Log.d("xmlPP",xmlPP.getText());
							}else{
								tv.setText("存在しないユーザー");
								//this.newUser(user.getText().toString(),editpass.getText().toString());
								this.user = user.getText().toString();
								this.pass = editpass.getText().toString();
								this.showDialog(0);
								break;
							}
						}
						
						//xmlPP.nextTextでタグの中身をゲット
						if(phpFlag == false && eventType == XmlPullParser.START_TAG && "string".equals(xmlPP.getName())){
							tv.setText(xmlPP.nextText());
						}
						
						eventType = xmlPP.next();
						
					}
					
					if(phpFlag){
						
						tv.setText("");
						writePreferences(USER_PREFS);
						Toast.makeText(this, "ログインしました", Toast.LENGTH_SHORT).show();
						Intent i = new Intent(this,MainActivity.class);
						startActivity(i);
					}
					
				}else{
					Toast.makeText(this, "応答無し"+respone.getStatusLine().getStatusCode(), Toast.LENGTH_SHORT).show();
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(this, "サーバーと接続できません", Toast.LENGTH_SHORT).show();
			}
		}else{
			tv.setText(errerMes);
		}
	}
	
	public void newUser(final String user,final String pass){
		final EditText input = new EditText(this);
		input.setFilters(filterPass);
		input.setInputType( InputType.TYPE_CLASS_TEXT |
				InputType.TYPE_TEXT_VARIATION_PASSWORD);
		new AlertDialog.Builder(this).setTitle("新規ユーザーで登録").setMessage("パスワードをもう一度入力してください")
			.setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					if(pass.equals(input.getText().toString()))
						newUserSet(user,pass);
					else
						Toast.makeText(Login.this, "パスワードが違います", Toast.LENGTH_SHORT).show();
					
				}
			}).show();
			
	}
	
	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
		
		this.writePreferences(this.DIALOG_CHECK);
		if(((CheckBox)findViewById(R.id.logCheckBox)).isChecked())
			this.writePreferences(this.CHECK_BOX);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO 自動生成されたメソッド・スタブ
		Dialog dialog = super.onCreateDialog(id);
		
		if(id == 0){
			
			final EditText input = new EditText(this);
			input.setFilters(filterPass);
			input.setInputType( InputType.TYPE_CLASS_TEXT |
					InputType.TYPE_TEXT_VARIATION_PASSWORD);
			
			dialog = new AlertDialog.Builder(this).setTitle("新規ユーザーで登録").setMessage("パスワードをもう一度入力してください")
			.setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					if(pass.equals(input.getText().toString()))
						newUserSet(user,pass);
					else
						Toast.makeText(Login.this, "パスワードが違います", Toast.LENGTH_SHORT).show();
					
				}
			}).create();
			
			return dialog;
		}
		
		return super.onCreateDialog(id);
	}
	

	public void newUserSet(String user,String pass){
		String setUrl = this.getResources().getString(R.string.url) + "registarion.php";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams httpParams = httpclient.getParams();
		httpParams.setParameter("http.useragent", "snsmap");//User-agentの設定　通常はブラウザとか端末とか
		HttpPost httppost = new HttpPost(setUrl);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("user", user));
		nameValuePairs.add(new BasicNameValuePair("pass", pass));
		
		try{
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			HttpResponse respone = httpclient.execute(httppost);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			respone.getEntity().writeTo(byteArrayOutputStream);
			
			
			if(respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				Log.d("newUser",byteArrayOutputStream.toString());
				Toast.makeText(this, this.user+"で新規作成しました", Toast.LENGTH_SHORT).show();
				
				this.writePreferences(this.USER_PREFS);
				
				
				Intent i = new Intent(this,MainActivity.class);
				startActivity(i);
				
				
			}else
				Log.d("newUser","errer");
			
			
		}catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "サーバーと接続できません", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	public void writePreferences(int i){
		if(i == DIALOG_CHECK){
			SharedPreferences prefs = getSharedPreferences("loginDia", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("user", ((EditText)findViewById(R.id.logUser)).getText().toString());
			editor.putString("pass", ((EditText)findViewById(R.id.logPass)).getText().toString());
			editor.commit();
		}
		
		if(i == CHECK_BOX){
			SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("user", ((EditText)findViewById(R.id.logUser)).getText().toString());
			editor.putString("pass", ((EditText)findViewById(R.id.logPass)).getText().toString());
			editor.putBoolean("box", ((CheckBox)findViewById(R.id.logCheckBox)).isChecked());
			editor.commit();
		}
		
		if(i == this.USER_PREFS){
			SharedPreferences prefs = getSharedPreferences("snsmap", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("user", this.user);
			editor.commit();
		}
	}
	
	public void readPreferences(int i){
		if(i == this.DIALOG_CHECK){
			SharedPreferences prefs = getSharedPreferences("loginDia", MODE_PRIVATE);
			this.user = prefs.getString("user", null);
			this.pass = prefs.getString("pass", null);
		}
		
		if(i == this.CHECK_BOX){
			SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
			CheckBox box = (CheckBox)findViewById(R.id.logCheckBox);
			box.setChecked(prefs.getBoolean("box", false));
			//チェックボックスの状態の確認
			if(box.isChecked()){
				this.user = prefs.getString("user", "");
				this.pass = prefs.getString("pass", "");
				((EditText)findViewById(R.id.logUser)).setText(this.user);
				((EditText)findViewById(R.id.logPass)).setText(this.pass);
			}
		}
	}
	
}
