package com.streamsdk.search;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.stream.api.HttpUtils;
import com.stream.api.StreamUtils;
import com.streamsdk.cache.ImageCache;

public class SearchThread implements Runnable{

	private int startPage;
    private SearchDoneCallback searchDoneCallback;
	
	public SearchThread(int start){
		startPage = start;
	}
	
	public void setSearchDoneCallback(SearchDoneCallback sdc){
		searchDoneCallback = sdc;
	}
	
	public void run() {
	
	    String url = "https://ajax.googleapis.com/ajax/services/search/images?";
	    try {
	    	String queryPart = "v=1.0&q=car&imgsz=" +   URLEncoder.encode("small|medium|large|xlarge", "UTF-8") + "&rsz=8&as_filetype=jpg&start=" + startPage;
			String jsonResponse = executeGet(url + queryPart);
			JSONObject jsonData = new JSONObject(jsonResponse);
			JSONObject responseData = jsonData.getJSONObject("responseData");
			JSONArray ja = responseData.getJSONArray("results");
			int size = ja.length();
			for (int i=0; i < size; i++){
				JSONObject jo = ja.getJSONObject(i);
				String imageUrl = jo.getString("url");
				InputStream in = HttpUtils.executeGet(imageUrl);
				Bitmap img = BitmapFactory.decodeStream(in);
				ImageCache.getInstance().addTempImages(imageUrl, img);
				if (i != size - 1){
					searchDoneCallback.searchDone(imageUrl, false);
				}else{
					searchDoneCallback.searchDone(imageUrl, true);
				}
				Log.i("", imageUrl);
			}
			
			
	    } catch (ClientProtocolException e) {
	
		} catch (IOException e) {
		
		} catch (JSONException e) {
			
		}
	    	
		
		
	}
	
    public String executeGet(String url) throws ClientProtocolException, IOException{
		
		DefaultHttpClient client = new DefaultHttpClient(createHttpParams());
		HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		String res = new String(StreamUtils.readByteArray(entity.getContent()));
		return res;
		
	}
    
    private static HttpParams createHttpParams(){
		
    	HttpParams httpParameters = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
		HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
		int timeoutConnection = 16000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 19000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		return httpParameters;
	
    }

}
