package com.dots.newspaper;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;

import som.news.inapp.R;

public class SplashScreen extends Activity {

	public static final String MyPREFERENCES = "UserIdInfo";

	private static final int STOPSPLASH = 0;

	private static final long SPLASHTIME = 5000;

	public static SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		sharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
		String userID = sharedpreferences.getString("userID", null);
		if (userID == null) {
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("userID", generateDeviceId());
			editor.commit();
		}

		Log.d("User_ID", generateDeviceId());

		Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}

	@SuppressLint("HandlerLeak")
	private Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOPSPLASH:
				// remove SplashScreen from view
				Intent intent = new Intent(SplashScreen.this,
						MainActivity.class);

				intent.putExtra("activekey", "");
				startActivity(intent);
				finish();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	private String generateDeviceId() {
		final String macAddr, androidId;

		WifiManager wifiMan = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();

		macAddr = wifiInf.getMacAddress();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());

		return deviceUuid.toString();

		// Maybe save this: deviceUuid.toString()); to the preferences.
	}

}
