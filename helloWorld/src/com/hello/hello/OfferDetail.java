package com.dots.newspaper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import som.news.inapp.R;

public class OfferDetail extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offer_detail);
		Button back = (Button)findViewById(R.id.btn_cancle);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		String OFFER_URL = b.getString("OFFER_URL");

		WebView wvMain;
		wvMain = (WebView) findViewById(R.id.offerDetail);
		wvMain.getSettings().setJavaScriptEnabled(true);
		wvMain.getSettings().setBuiltInZoomControls(false);
		wvMain.getSettings().setLoadsImagesAutomatically(true);
		wvMain.setHorizontalScrollBarEnabled(false);
		wvMain.setVerticalScrollBarEnabled(true);
		wvMain.getSettings().setCacheMode(
				android.webkit.WebSettings.LOAD_DEFAULT);

		wvMain.getSettings().setUseWideViewPort(true);

		wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}

		});

		wvMain.loadUrl(OFFER_URL);
		/*
		 * WebSettings webSettings = webView.getSettings();
		 * webSettings.setBuiltInZoomControls(true);
		 * 
		 * webView.setWebViewClient(new Callback());
		 * webView.loadUrl("http://ccpromo.blogspot.in/");
		 * 
		 * wvMain.loadUrl(OFFER_URL); private class Callback extends
		 * WebViewClient { // HERE IS THE MAIN // CHANGE.
		 * 
		 * @Override public boolean shouldOverrideUrlLoading(WebView view,
		 * String url) { return (false); }
		 * 
		 * }
		 */
	}

	private class Callback extends WebViewClient { // HERE IS THE MAIN CHANGE.

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return (false);
		}

	}
}