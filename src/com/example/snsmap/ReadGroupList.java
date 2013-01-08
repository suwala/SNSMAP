package com.example.snsmap;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

/*
 * mode
 *  show = GROUPテーブル一覧の取得(String[]) create=tableの作成(void)
 *  login = GROUPへログイン(member)
 *
 */
public class ReadGroupList extends AsyncTask<String, Integer, Object[]>
implements OnCancelListener{
	
	private Context context;
	private Callback callback;
	private int requestCode;
	ProgressDialog dialog;

	public ReadGroupList(Context context,final Callback callback,final int requestCode){
		this.context = context;
		this.callback = callback;
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
	protected void onPostExecute(Object[] result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if(result == null)
			callback.callback(Callback.ERROR, requestCode, result);
		else		
			callback.callback(Callback.SUCCESS, requestCode, result);
	}

	@Override
	synchronized protected Group[] doInBackground(String... params) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(MainMap.URL);

		ArrayList <NameValuePair> posts = new ArrayList<NameValuePair>();

		posts.add(new BasicNameValuePair("mode", params[0]));

		try {
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

			//while(!isCancelled()){
			if((sLine = objBuf.readLine()) != null)
				objJson.append(sLine);
			//}

			//JsonDateの整形をしよう　の前にgroupとPassの取得が先か
			JSONArray array = new JSONArray(objJson.toString());
			JSONObject json = array.getJSONObject(0);

			Iterator it = json.keys();
			Group[] group = new Group[array.length()];

			int i=0;
			while(i<array.length()){
				json = array.getJSONObject(i);
				it = json.keys();
				String key = (String)it.next();
				String key2 = (String)it.next();
				group[i++] = new Group(json.getString(key), json.getString(key2));					

			}
			return group;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(RuntimeException e){
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

	@Override
	public void onCancel(DialogInterface dialog) {
		//明示的にキャンセルしないとdoInBack~が走り続けたりする
		cancel(true);
	}
}
