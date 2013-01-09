package com.example.snsmap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class GroupLogin extends AsyncTask<String, Integer, Object[]>
implements OnCancelListener{

	ProgressDialog dialog;
	private Group group;
	private Context context;
	Callback callback;
	int requestCode;

	public GroupLogin(Group group,Context context,int requestCode) {
		this.group = group;
		this.context = context;
		this.callback = (Callback) context;
		this.requestCode = requestCode;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getResources().getString(R.string.session));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(this);
		dialog.show();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		cancel(true);
	}

	@Override
	protected Object[] doInBackground(String... params) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(MainMap.URL);

		ArrayList <NameValuePair> posts = new ArrayList<NameValuePair>();

		posts.add(new BasicNameValuePair("mode", params[0]));
		posts.add(new BasicNameValuePair("mapgroup", group.name));
		posts.add(new BasicNameValuePair("pass", params[1]));
		
		try{
			post.setEntity(new UrlEncodedFormEntity(posts,"UTF-8"));
			byte[] byts = client.execute(post,new ResponseHandler<byte[]>() {

				public byte[] handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					//レスポンスコードの判定　正常値は200
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
						return EntityUtils.toByteArray(response.getEntity());
					}else{
						//throw new RuntimeException("通信エラーです");
						return null;
					}
				}
			});
			if(byts == null)
				return null;			
			
			ByteArrayInputStream objStream = new ByteArrayInputStream(byts);

			InputStreamReader objReader = new InputStreamReader(objStream);
			BufferedReader objBuf = new BufferedReader(objReader);

			StringBuilder objJson = new StringBuilder();
			String sLine;
			
			if((sLine = objBuf.readLine()) != null)
				objJson.append(sLine);
			if(objJson.toString().equals(MainMap.MISSMATH_PASS))
				return new Object[]{MainMap.MISSMATH_PASS};
			
			JSONArray array = new JSONArray(objJson.toString());
			if(array.length() == 1)
				return new Object[]{"0"};
			JSONObject json = array.getJSONObject(0);

			Iterator it = json.keys();
			Person[] persons = new Person[array.length()];

			json = array.getJSONObject(0);
			persons[0] = new Person();
			persons[0].setIconNumber(json.getInt("count"));
			//HashMapか値を直に指定するか迷う
			//String[] keys=new String[5];
			int i=1;
			String[] dateStr;
			while(i<array.length()){
				json = array.getJSONObject(i);
				
				persons[i] = new Person();
				persons[i].setName(json.getString("name"));
				
				String[] gp = json.getString("gp").split(",");
				
				persons[i].setGp(new GeoPoint(Integer.valueOf(gp[0]), Integer.valueOf(gp[1])));
				
				persons[i].setMessage((json.getString("message")));
				persons[i].setIconNumber(Integer.valueOf(json.getString("item")));
				
				dateStr = json.getString("date").split(":");
				
				persons[i].setDate(getDate(dateStr));
				i++;
			}
			
			return persons;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(RuntimeException e){
			Log.d("きゃっち","きゃっち");
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			//HttpClientのリソースの解放
			client.getConnectionManager().shutdown();
		}		
		return null;
	}
	
	private String getDate(String[] str){
		int[] date=new int[str.length];
		for(int i=0;i<str.length;i++){
			date[i] = Integer.valueOf(str[i]);
		}
		
		if(date[0] > 24){
			return date[0]/24+context.getResources().getString(R.string.days_ago);
		}else if(date[0] > 1){
			return date[0]+context.getResources().getString(R.string.houh_ago);
		}else if(date[1] > 0){
			return date[1]+context.getResources().getString(R.string.mouch_ago);
		}else
			return date[2]+context.getResources().getString(R.string.sec_ago);
	}

	@Override
	protected void onPostExecute(Object[] result) {
		
		dialog.dismiss();
		if(result == null)
			callback.callback(Callback.ERROR, requestCode, result);
		else
			callback.callback(Callback.SUCCESS, requestCode, result);
	}
}


