package com.dots.newspaper.app.list;

import som.news.inapp.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class WebViewPDFReader extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.web);
		
		
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		String PDF_URL = b.getString("PDF_URL");
		
		//Uri path = Uri.fromFile(file);
		
		
	}
}
