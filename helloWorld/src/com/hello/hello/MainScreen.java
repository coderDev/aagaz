package com.dots.newspaper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import som.news.inapp.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dots.newspaper.inapp.util.IabHelper;
import com.dots.newspaper.inapp.util.IabResult;
import com.dots.newspaper.inapp.util.Inventory;
import com.dots.newspaper.inapp.util.Purchase;

public class MainScreen extends Fragment {

	Button mylibrary;
	ProgressDialog pDialog;
	private RequestQueue queue;
	TextView reload, subsTypeDetail;
	ImageButton month_1, month_12, free_sample;
	View v;

	JSONObject obj;
	static final String UserID = Const.USERID;
	static final String SKU_1_MONTH = Const.SKU_1_MONTH;
	static final String SKU_1_YEAR = Const.SKU_1_YEAR;

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;
	static final int RC_REQUEST2 = 10002;

	boolean mIs_1_MONTH = false;
	// Does the user have an active subscription to the infinite gas plan?
	boolean mIs_1_YEAR = false;

	String base64EncodedPublicKey = Const.base64EncodedPublicKey;
	// The helper object
	IabHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main, null);

		/*
		 * reload = (TextView) v.findViewById(R.id.reload);
		 * reload.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { get_user_status(); } });
		 */
		mylibrary = (Button) v.findViewById(R.id.mylibrary);
		mylibrary.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), MyLibraryList.class); // MyLibrary
				startActivity(intent);
			}
		});

		free_sample = (ImageButton) v.findViewById(R.id.free_sample);
		free_sample.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				downloadFreeSample();
			}
		});

		// in-app purchase
		Log.d(Const.DEBUGTAG, "Creating IAB helper.");
		mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
		mHelper.enableDebugLogging(true);
		Log.d(Const.DEBUGTAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(Const.DEBUGTAG, "Setup finished.");
				if (!result.isSuccess()) {
					complain("Problem setting up in-app billing: " + result);
					return;
				}
				if (mHelper == null)
					return;
				Log.d(Const.DEBUGTAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		month_1 = (ImageButton) v.findViewById(R.id.month_1);
		month_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!mHelper.subscriptionsSupported()) {
					complain("Subscriptions not supported on your device yet. Sorry!");
					return;
				}

				String payload = "";
				Log.d(Const.DEBUGTAG,
						"Launching purchase flow for infinite gas subscription.");
				mHelper.launchPurchaseFlow(getActivity(), SKU_1_MONTH,
						IabHelper.ITEM_TYPE_SUBS, RC_REQUEST,
						mPurchaseFinishedListener, payload);

			}
		});

		month_12 = (ImageButton) v.findViewById(R.id.month_12);
		month_12.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!mHelper.subscriptionsSupported()) {
					complain("Subscriptions not supported on your device yet. Sorry!");
					return;
				}
				String payload = "";
				Log.d(Const.DEBUGTAG,
						"Launching purchase flow for infinite gas subscription.");
				mHelper.launchPurchaseFlow(getActivity(), SKU_1_YEAR,
						IabHelper.ITEM_TYPE_SUBS, RC_REQUEST2,
						mPurchaseFinishedListener, payload);
			}
		});

		subsTypeDetail = (TextView) v.findViewById(R.id.subsTypeDetail);
		return v;
	}

	// in-app purchase

	// Listener that's called when we finish querying the items and
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(Const.DEBUGTAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(Const.DEBUGTAG, "Query inventory was successful.");

			// Do we have the premium upgrade?
			Purchase premiumPurchase = inventory.getPurchase(SKU_1_MONTH);
			mIs_1_MONTH = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
			Log.d(Const.DEBUGTAG, "User is "
					+ (mIs_1_MONTH ? "mIs_1_MONTH" : "NOT mIs_1_MONTH"));

			Log.d(Const.DEBUGTAG, "=> " + premiumPurchase);

			if (mIs_1_MONTH) {

				Log.d(Const.DEBUGTAG, "==> " + premiumPurchase);

				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Loading...");
				pDialog.show();
				queue = Volley.newRequestQueue(getActivity());

				try {
					obj = new JSONObject();
					obj.put("userID", Const.USERID);
					obj.put("sid", "2");
					obj.put("orderId", premiumPurchase.getOrderId());
					obj.put("packageName", premiumPurchase.getPackageName());
					obj.put("productId", premiumPurchase.getSku());
					obj.put("purchaseTime", premiumPurchase.getPurchaseTime());
					obj.put("purchaseState", premiumPurchase.getPurchaseState());
					obj.put("developerPayload", Const.USERID);
					obj.put("purchaseToken", premiumPurchase.getToken());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_LONG).show();*/

				Log.d(Const.DEBUGTAG, obj.toString());

				StringRequest myReq = new StringRequest(Method.POST,
						Const.API_URL, createMyReqSuccessListener(),
						createMyReqErrorListener()) {

					protected Map<String, String> getParams()
							throws com.android.volley.AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();

						params.put("action", "get_android_user_status");
						params.put("json", obj.toString());
						return params;
					};
				};
				queue.add(myReq);

				subsTypeDetail.setText("You are currentely subscribed for 1 Month");
			}

			// Do we have the infinite gas plan?
			Purchase infiniteGasPurchase = inventory.getPurchase(SKU_1_YEAR);

			mIs_1_YEAR = (infiniteGasPurchase != null && verifyDeveloperPayload(infiniteGasPurchase));
			Log.d(Const.DEBUGTAG, "User "
					+ (mIs_1_YEAR ? "HAS" : "DOES NOT HAVE")
					+ " infinite gas subscription.");
			if (mIs_1_YEAR) {
				Log.d(Const.DEBUGTAG, "==> " + premiumPurchase);

				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Loading...");
				pDialog.show();
				queue = Volley.newRequestQueue(getActivity());

				try {
					obj = new JSONObject();
					obj.put("userID", Const.USERID);
					obj.put("sid", "2");
					obj.put("orderId", premiumPurchase.getOrderId());
					obj.put("packageName", premiumPurchase.getPackageName());
					obj.put("productId", premiumPurchase.getSku());
					obj.put("purchaseTime", premiumPurchase.getPurchaseTime());
					obj.put("purchaseState", premiumPurchase.getPurchaseState());
					obj.put("developerPayload", Const.USERID);
					obj.put("purchaseToken", premiumPurchase.getToken());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_LONG)
						.show();

				Log.d(Const.DEBUGTAG, obj.toString());

				StringRequest myReq = new StringRequest(Method.POST,
						Const.API_URL, createMyReqSuccessListener(),
						createMyReqErrorListener()) {

					protected Map<String, String> getParams()
							throws com.android.volley.AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();

						params.put("action", "get_android_user_status");
						params.put("json", obj.toString());
						return params;
					};
				};
				queue.add(myReq);

				subsTypeDetail
						.setText("You are currentely subscribed for 1 Year");

			}

			if(mIs_1_MONTH == false && mIs_1_YEAR == false){
				subsTypeDetail
				.setText("You are currentely Free Subscribed");
			}
			
			Log.d(Const.DEBUGTAG,"Initial inventory query finished; enabling main UI.");
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(Const.DEBUGTAG, "onActivityResult(" + requestCode + ","
				+ resultCode + "," + data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {

			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(Const.DEBUGTAG, "onActivityResult handled by IABUtil.");
		}
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		return true;
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(Const.DEBUGTAG, "Purchase finished: " + result
					+ ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				// setWaitScreen(false);
				return;
			}

			Log.d(Const.DEBUGTAG, "Purchase successful.");

			if (purchase.getSku().equals(SKU_1_MONTH)) {

				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Loading...");
				pDialog.show();
				queue = Volley.newRequestQueue(getActivity());

				final JSONObject obj = new JSONObject();
				try {

					obj.put("userID", Const.USERID);
					obj.put("sid", "2");
					obj.put("orderId", purchase.getOrderId());
					obj.put("packageName", purchase.getPackageName());
					obj.put("productId", purchase.getSku());
					obj.put("purchaseTime", purchase.getPurchaseTime());
					obj.put("purchaseState", purchase.getPurchaseState());
					obj.put("developerPayload", purchase.getDeveloperPayload());
					obj.put("purchaseToken", purchase.getToken());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Purchase successful SKU_1_MONTH ", "" + obj);

				StringRequest myReq = new StringRequest(Method.POST,
						Const.API_URL, createMyReqSuccessListener(),
						createMyReqErrorListener()) {

					protected Map<String, String> getParams()
							throws com.android.volley.AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();

						params.put("action", "get_android_user_status");
						params.put("json", obj.toString());
						return params;
					};
				};
				queue.add(myReq);
				Log.d(Const.DEBUGTAG, " 1 MONTH subscription purchased.");
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				Log.d(Const.DEBUGTAG, purchase.getOriginalJson().toString());
				Log.d(Const.DEBUGTAG, purchase.getSignature().toString());
			} else if (purchase.getSku().equals(SKU_1_YEAR)) {
				// bought the infinite gas subscription
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Loading...");
				pDialog.show();
				queue = Volley.newRequestQueue(getActivity());

				final JSONObject obj = new JSONObject();
				try {
					obj.put("userID", Const.USERID);
					obj.put("sid", "4");
					obj.put("orderId", purchase.getOrderId());
					obj.put("packageName", purchase.getPackageName());
					obj.put("productId", purchase.getSku());
					obj.put("purchaseTime", purchase.getPurchaseTime());
					obj.put("purchaseState", purchase.getPurchaseState());
					obj.put("developerPayload", purchase.getDeveloperPayload());
					obj.put("purchaseToken", purchase.getToken());

				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("Purchase successful SKU_1_MONTH ", "" + obj);

				Log.d(Const.DEBUGTAG, " 1 YEAR subscription purchased.");
				StringRequest myReq = new StringRequest(Method.POST,
						Const.API_URL, createMyReqSuccessListener(),
						createMyReqErrorListener()) {

					protected Map<String, String> getParams()
							throws com.android.volley.AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();

						params.put("action", "get_android_user_status");
						params.put("json", obj.toString());
						return params;
					};
				};
				queue.add(myReq);

				alert("Thank you for subscribing to 1 YEAR!");
				Log.d(Const.DEBUGTAG, purchase.getOriginalJson().toString());
				Log.d(Const.DEBUGTAG, purchase.getSignature().toString());
			}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(Const.DEBUGTAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				Log.d(Const.DEBUGTAG, "Consumption successful. Provisioning.");
				// mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
				saveData();
				// alert("You filled 1/4 tank. Your tank is now " +
				// String.valueOf(mTank) + "/4 full!");
			} else {
				complain("Error while consuming: " + result);
			}
			// updateUi();
			// setWaitScreen(false);
			Log.d(Const.DEBUGTAG, "End consumption flow.");
		}
	};

	// We're being destroyed. It's important to dispose of the helper here!
	@Override
	public void onDestroy() {
		super.onDestroy();

		// very important:
		Log.d(Const.DEBUGTAG, "Destroying helper.");
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	// Enables or disables the "please wait" screen.
	void setWaitScreen(boolean set) {
	}

	void complain(String message) {
		Log.e(Const.DEBUGTAG, "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(Const.DEBUGTAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	void saveData() {

		/*
		 * WARNING: on a real application, we recommend you save data in a
		 * secure way to prevent tampering. For simplicity in this sample, we
		 * simply store the data using a SharedPreferences.
		 */

	}

	void loadData() {
		// SharedPreferences sp = getPreferences(MODE_PRIVATE);
		// mTank = sp.getInt("tank", 2);
		Log.d(Const.DEBUGTAG, "Loaded data: loadData");
	}

	public void get_user_status() {

		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading...");
		pDialog.show();
		queue = Volley.newRequestQueue(getActivity());

		final JSONObject obj = new JSONObject();
		try {

			obj.put("userID", Const.USERID);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest myReq = new StringRequest(Method.POST, Const.API_URL,
				createMyReqSuccessListener(), createMyReqErrorListener()) {

			protected Map<String, String> getParams()
					throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("action", "get_android_user_status");
				params.put("json", obj.toString());
				return params;
			};
		};
		queue.add(myReq);
	}

	public void downloadFreeSample() {
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading...");
		pDialog.show();
		queue = Volley.newRequestQueue(getActivity());

		final JSONObject obj = new JSONObject();
		try {
			obj.put("userID", Const.USERID);
			obj.put("sid", "1");
			obj.put("orderId", "");
			obj.put("packageName", "");
			obj.put("productId", "");
			obj.put("purchaseTime", "");
			obj.put("purchaseState", "");
			obj.put("developerPayload", "");
			obj.put("purchaseToken", "");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(Const.DEBUGTAG, " 1 YEAR subscription purchased.");
		StringRequest myReq = new StringRequest(Method.POST, Const.API_URL,
				createMyReqSuccessListener(), createMyReqErrorListener()) {

			protected Map<String, String> getParams()
					throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("action", "get_android_user_status");
				params.put("json", obj.toString());
				return params;
			};
		};
		queue.add(myReq);
	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Long inLong = (long) Integer.parseInt(getCurrentTimeStamp());

				try {
					pDialog.hide();

					final JSONObject obj = new JSONObject(response);

					Log.d("get_user_status", "" + obj);

					String mexpires_date_ms = obj.get("expires_date_ms")
							.toString();
					Long inexpires_date_ms = null;
					if(mexpires_date_ms != ""){
					 inexpires_date_ms = (long) Integer
							.parseInt(mexpires_date_ms);
					}
					Log.d("get_user_status", " Search result " + obj);
					Log.d("user_id", obj.get("uid").toString());
					if (obj.get("sid").toString() == "1"
							&& inexpires_date_ms >= inLong) {
						subsTypeDetail = (TextView) v
								.findViewById(R.id.subsTypeDetail);
						subsTypeDetail
								.setText("You are currentely free subscribed");

					} else if (obj.get("sid").toString() == "2"
							&& inexpires_date_ms >= inLong) {
						subsTypeDetail = (TextView) v
								.findViewById(R.id.subsTypeDetail);
						subsTypeDetail
								.setText("You are currentely subscribed for 1 Month");

					} else if (obj.get("sid").toString() == "4"
							&& inexpires_date_ms >= inLong) {
						subsTypeDetail = (TextView) v
								.findViewById(R.id.subsTypeDetail);
						subsTypeDetail
								.setText("You are currentely subscribed for 12 Month");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Log.d("jPost", " String to json ");

			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				pDialog.hide();
				Toast.makeText(getActivity(), error.toString(),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	public static String getCurrentTimeStamp() {
		try {

			Long tsLong = System.currentTimeMillis() / 1000;
			String ts = tsLong.toString();

			return ts;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

}
