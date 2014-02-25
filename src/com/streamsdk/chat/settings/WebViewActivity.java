package com.streamsdk.chat.settings;

import com.streamsdk.chat.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
	 
		private WebView webView;
	 
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.webview_layout);
			Intent intent = getIntent();
			String url = intent.getExtras().getString("url");
			webView = (WebView) findViewById(R.id.webView1);
			webView.loadUrl(url);
	 
		}

}