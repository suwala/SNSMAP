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
	 * XmlPP��php�̉�͂ɐ���
	 * Dialog#show();�̓o�O�����HDia�\�����ɉ�ʂ����ɂ���ƃ��������[�N���������錏
	 * showDialog()���g���Α��v�����Ȃ�
	 * 
	 * user+pass�Ƒ��݂��Ȃ�user�̔��芮��
	 * �V�K�o�^��registarion.php���܂�
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
			errerMes = ("UserName��6�����ȏ����Ă�������\n");
			tv.setTextColor(Color.RED);
			//Intent i = new Intent(this,MainActivity.class);
			//startActivity(i);
		}
		EditText editpass = (EditText)findViewById(R.id.logPass);
		
		if(editpass.getText().toString().length() < 6){
			errerMes = errerMes + "Password��6�����ȏ����Ă�������";
			tv.setTextColor(Color.RED);
		}
	
		if(errerMes.equals("")){//�G���[�Ȃ�����http��
			
			CheckBox box = (CheckBox)findViewById(R.id.logCheckBox);
			//�`�F�b�N�{�b�N�X�̏�Ԃ̊m�F
			if(box.isChecked())
				this.writePreferences(CHECK_BOX);
			
			String name = user.getText().toString();
			String pass = editpass.getText().toString();
			
			
			String setUrl = getResources().getString(R.string.url) + "testver.php";
			Log.d("url",setUrl);
			
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams httpParams = httpclient.getParams();
			httpParams.setParameter("http.useragent", "snsmap");//User-agent�̐ݒ�@�ʏ�̓u���E�U�Ƃ��[���Ƃ�
			HttpPost httppost = new HttpPost(setUrl);
			
			//POST���M����f�[�^���i�[
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("user", name));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			
			XmlPullParser xmlPP = Xml.newPullParser();
			
			
			try{
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
				HttpResponse respone = httpclient.execute(httppost);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				respone.getEntity().writeTo(byteArrayOutputStream);
				
				//�I����̉������擾�@�@XmlPullParser�ŉ�͂��K�v�ȏ�񂾂��i�[����
				if(respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					
					//byteArrya~�ɑS�Ă̕������i�[�����̂ł����StringReader�^�ɓ����
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
						
						
						//php����flag�^�O���L�q���Ă���̂ł�����Q�Ƃ��ă��O�C���̐��ۂ𔻒肷��
						if(eventType == XmlPullParser.START_TAG && "flag".equals(xmlPP.getName())){//body�^�O�̃`�F�b�N
							if("ok".equals(xmlPP.nextText())){
								Log.d("xmlPP",xmlPP.getText());
								this.user = name;
								phpFlag = true;
							}else if("p_miss".equals(xmlPP.getText())){
								phpFlag = false;
								Log.d("xmlPP",xmlPP.getText());
							}else{
								tv.setText("���݂��Ȃ����[�U�[");
								//this.newUser(user.getText().toString(),editpass.getText().toString());
								this.user = user.getText().toString();
								this.pass = editpass.getText().toString();
								this.showDialog(0);
								break;
							}
						}
						
						//xmlPP.nextText�Ń^�O�̒��g���Q�b�g
						if(phpFlag == false && eventType == XmlPullParser.START_TAG && "string".equals(xmlPP.getName())){
							tv.setText(xmlPP.nextText());
						}
						
						eventType = xmlPP.next();
						
					}
					
					if(phpFlag){
						
						tv.setText("");
						writePreferences(USER_PREFS);
						Toast.makeText(this, "���O�C�����܂���", Toast.LENGTH_SHORT).show();
						Intent i = new Intent(this,MainActivity.class);
						startActivity(i);
					}
					
				}else{
					Toast.makeText(this, "��������"+respone.getStatusLine().getStatusCode(), Toast.LENGTH_SHORT).show();
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(this, "�T�[�o�[�Ɛڑ��ł��܂���", Toast.LENGTH_SHORT).show();
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
		new AlertDialog.Builder(this).setTitle("�V�K���[�U�[�œo�^").setMessage("�p�X���[�h��������x���͂��Ă�������")
			.setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					if(pass.equals(input.getText().toString()))
						newUserSet(user,pass);
					else
						Toast.makeText(Login.this, "�p�X���[�h���Ⴂ�܂�", Toast.LENGTH_SHORT).show();
					
				}
			}).show();
			
	}
	
	@Override
	protected void onStop() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onStop();
		
		this.writePreferences(this.DIALOG_CHECK);
		if(((CheckBox)findViewById(R.id.logCheckBox)).isChecked())
			this.writePreferences(this.CHECK_BOX);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		Dialog dialog = super.onCreateDialog(id);
		
		if(id == 0){
			
			final EditText input = new EditText(this);
			input.setFilters(filterPass);
			input.setInputType( InputType.TYPE_CLASS_TEXT |
					InputType.TYPE_TEXT_VARIATION_PASSWORD);
			
			dialog = new AlertDialog.Builder(this).setTitle("�V�K���[�U�[�œo�^").setMessage("�p�X���[�h��������x���͂��Ă�������")
			.setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					if(pass.equals(input.getText().toString()))
						newUserSet(user,pass);
					else
						Toast.makeText(Login.this, "�p�X���[�h���Ⴂ�܂�", Toast.LENGTH_SHORT).show();
					
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
		httpParams.setParameter("http.useragent", "snsmap");//User-agent�̐ݒ�@�ʏ�̓u���E�U�Ƃ��[���Ƃ�
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
				Toast.makeText(this, this.user+"�ŐV�K�쐬���܂���", Toast.LENGTH_SHORT).show();
				
				this.writePreferences(this.USER_PREFS);
				
				
				Intent i = new Intent(this,MainActivity.class);
				startActivity(i);
				
				
			}else
				Log.d("newUser","errer");
			
			
		}catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "�T�[�o�[�Ɛڑ��ł��܂���", Toast.LENGTH_SHORT).show();
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
			//�`�F�b�N�{�b�N�X�̏�Ԃ̊m�F
			if(box.isChecked()){
				this.user = prefs.getString("user", "");
				this.pass = prefs.getString("pass", "");
				((EditText)findViewById(R.id.logUser)).setText(this.user);
				((EditText)findViewById(R.id.logPass)).setText(this.pass);
			}
		}
	}
	
}
