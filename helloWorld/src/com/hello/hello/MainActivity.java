package com.dots.newspaper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import som.news.inapp.R;

public class MainActivity extends FragmentActivity {

	private static final String TAB_1_TAG = "Home";
	private static final String TAG = "MyAlert";
	private static final String TAB_2_TAG = "Got a Story?";
	private static final String TAB_3_TAG = "Local Offers";
	private static final String TAB_4_TAG = "Feedback";
	public static final String MyPREFERENCES = "UserInfo";

	String value = "";

	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bottom_tabs);
		Bundle bb = getIntent().getExtras();
		value = bb.getString("activekey");

		// save userId

		
		String userID = SplashScreen.sharedpreferences.getString("userID", "userID");

		if(userID != null){
		//Log.d("userID: ", userID );
		//alert(userID);
		}else{
		//	Log.d("userID: ", "null");
		}

		initView();


	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	@SuppressLint("NewApi")
	private void initView() {
		Resources res = getResources();
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		Bundle b = new Bundle();
		b.putString("key", "Simple");

		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_1_TAG).setIndicator("Home",
						res.getDrawable(R.drawable.tab_home)),
				MainScreen.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_2_TAG).setIndicator("Got a Story?",
						res.getDrawable(R.drawable.tab_got_story)),
				Got_a_story.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_3_TAG).setIndicator("Local Offers",
						res.getDrawable(R.drawable.tab_local_offer)),

				Offer.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_4_TAG).setIndicator("Feedback",
						res.getDrawable(R.drawable.tab_feedback)),
				Feedback.class, null);

		// Feedback

		for (int i = 0; i < mTabHost.getTabWidget().getTabCount(); i++) {

			mTabHost.getTabWidget().setBackground(
					getResources().getDrawable(R.drawable.tab_bg));
			mTabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 80;
			TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title); // for Selected Tab
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.TRANSPARENT);
			tv.setTextColor(Color.parseColor("#666666"));
		}

		Log.e("value", "CA" + value);

		if (value.equals("home")) {
			mTabHost.setCurrentTab(0);
		} else if (value.equals("got_a_story")) {
			mTabHost.setCurrentTab(1);
		} else if (value.equals("local_offers")) {
			mTabHost.setCurrentTab(2);
		} else if (value.equals("feedback")) {
			mTabHost.setCurrentTab(3);
		}

		// ============== Start of color inflation ==================
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@SuppressLint("NewApi")
			@Override
			public void onTabChanged(String tabId) {

				// Inflating color when tab is selected.
				for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
					/*
					 * mTabHost.getTabWidget().getChildAt(i)
					 * .setBackgroundColor(Color.parseColor("#181818"));
					 */

					mTabHost.getTabWidget()
							.getChildAt(i)
							.setBackground(
									getResources().getDrawable(
											R.drawable.tab_bg));

				}

				/*
				 * mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
				 * .setBackgroundColor(Color.parseColor("#424542"));
				 */
				mTabHost.getTabWidget()
						.getChildAt(mTabHost.getCurrentTab())
						.setBackground(
								getResources().getDrawable(R.drawable.tab_bg));
				mTabHost.getTabWidget().setBackground(
						getResources().getDrawable(R.drawable.tab_bg));
				// ============== End of color inflation ==================

				TextView tv = (TextView) mTabHost.getTabWidget()
						.getChildAt(mTabHost.getCurrentTab())
						.findViewById(android.R.id.title); // for Selected Tab
				tv.setTextColor(Color.parseColor("#666666"));

			}
		});

	}

	public String getUserId() {
		TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
