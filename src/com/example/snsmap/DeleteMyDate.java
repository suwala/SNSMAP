package com.example.snsmap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

public class DeleteMyDate extends AsyncTask<String, Integer, Object[]>
implements OnCancelListener{

	ProgressDialog dialog;
	Callback callback;
	int requestCode;
	Context context;
	
	public DeleteMyDate(Context context,int requestCode){
		this.requestCode = requestCode;
		this.callback = (Callback)context;
		this.context = context;
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getResources().getString(R.string.session));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(this);
		dialog.show();
	}
	
	@Override
	public void onCancel(DialogInterface dialog){
		cancel(true);
	}
	
	@Override
	protected String[] doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(MainMap.URL);
		
		List<NameValuePair> posts = new ArrayList<NameValuePair>();
		
		posts.add(new BasicNameValuePair("mode", params[0]));
		posts.add(new BasicNameValuePair("mapgroup", params[1]));
		posts.add(new BasicNameValuePair("pass", params[2]));
		posts.add(new BasicNameValuePair("uuid", params[3]));
		
		try{
			post.setEntity(new UrlEncodedFormEntity(posts,"UTF-8"));
			byte[] byts = client.execute(post,new ResponseHandler<byte[]>() {

				@Override
				public byte[] handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					// TODO Auto-generated method stub
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
						return EntityUtils.toByteArray(response.getEntity());
					else
						return null;				
				}
			});
			
			if(byts == null)
				return null;
			ByteArrayInputStream objStream = new ByteArrayInputStream(byts);
			
			InputStreamReader objReader = new InputStreamReader(objStream);
			BufferedReader objBuf = new BufferedReader(objReader);
			//String line = objBuf.readLine();
			String[] sLine = new String[1];
			if((sLine[0] = objBuf.readLine()) != null)
				;
			Log.d("delete",sLine[0]);
			return sLine;
			
		}catch(IOException e){
			e.printStackTrace();
		}return null;
	}
	
	@Override
	protected void onPostExecute(Object[] result){
		dialog.dismiss();
		if(result == null)
			callback.callback(Callback.ERROR, requestCode,result);
		else
			callback.callback(Callback.SUCCESS, requestCode, result);
	}

}
