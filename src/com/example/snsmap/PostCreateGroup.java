package com.example.snsmap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

public class PostCreateGroup extends AsyncTask<String, Integer, String[]>
implements OnCancelListener{

	private Context context;
	private Callback callback;
	private int requestCode;
	ProgressDialog dialog;
	
	public PostCreateGroup(final Callback callback,final int requestCode) {
		this.callback = callback;
		this.requestCode = requestCode;
		this.context = (Context)callback;
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
	protected String[] doInBackground(String... params) {
		
		for(String s:params)
			Log.d("post",s);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(MainMap.URL);
		
		List<NameValuePair> posts = new ArrayList<NameValuePair>();
		posts.add(new BasicNameValuePair("mode", params[0]));
		posts.add(new BasicNameValuePair("mapgroup", params[1]));
		posts.add(new BasicNameValuePair("pass", params[2]));
		
		try{
			post.setEntity(new UrlEncodedFormEntity(posts,"UTF-8"));
			byte[] byts = client.execute(post,new ResponseHandler<byte[]>() {

				@Override
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
			
			ByteArrayInputStream objStream = new ByteArrayInputStream(byts);

			InputStreamReader objReader = new InputStreamReader(objStream);
			BufferedReader objBuf = new BufferedReader(objReader);

			String sLine = objBuf.readLine();
			String[] str = {sLine,params[1]};
			return str;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(RuntimeException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			client.getConnectionManager().shutdown();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		dialog.dismiss();
		callback.callback(Callback.SUCCESS, requestCode, (Object[])result);
	}
}
