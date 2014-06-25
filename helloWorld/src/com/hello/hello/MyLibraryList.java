package com.dots.newspaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import som.news.inapp.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dots.newspaper.app.list.ItemAdapter;
import com.dots.newspaper.app.list.ItemRow;
import com.dots.newspaper.app.list.MyLibraryRowAdapter;
import com.dots.newspaper.app.list.MyLibraryRowItem;
import com.dots.newspaper.app.list.MyLibraryRowItem.MyLibraryRow;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.gson.Gson;

public class MyLibraryList extends Activity {

	public static SwipeListView swipelistview;
	ItemAdapter adapter;
	private Gson gson;
	private ListView resultList;
	private MyLibraryRowAdapter adapter1;
	private List<MyLibraryRow> imageResults;
	//	public static ProgressBar progress;
	public static TextView txtStatus;
	public static View v;

	List<ItemRow> itemData;
	private Handler handler = new Handler();
	ProgressDialog pDialog;
	private RequestQueue queue;
	TextView IssueTitle;
	String UserId;
	ProgressDialog pd;
	TextView tv1, tv2, tv3, tv4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.swipe_lv);

		pd = new ProgressDialog(this);
		pd.setMessage("Loading");
		pd.show();

		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		UserId = TelephonyMgr.getDeviceId();
		Log.d("UserID: ", "kn" + UserId );

		loadData();


		/*if(ServiceClass.indexclicked.size()>0){
			for(int i=0;i<ServiceClass.indexclicked.size();i++){
				Log.v("My", "MY"+i);
				View v = MyLibraryList.swipelistview.getChildAt(ServiceClass.position-1);//- MyLibraryList.swipelistview.getFirstVisiblePosition());
				ProgressBar progress = (ProgressBar) v.findViewById(R.id.progressBar);
				progress.setProgress(ServiceClass.Statuss);
				progress.setVisibility(View.VISIBLE);	

			}
	}
		else{
			Log.v("My", "n");
		}
		 */


		/*Log.d("aa",""+ServiceClass.position);

		if(ServiceClass.position == -1){

		}else{
			Toast.makeText(getApplicationContext(), "ca"+ServiceClass.position, Toast.LENGTH_LONG).show();
			View v = swipelistview.getChildAt(ServiceClass.position);//- swipelistview.getFirstVisiblePosition());
			ProgressBar progress = (ProgressBar) v.findViewById(R.id.progressBar);
			progress.setVisibility(View.VISIBLE);
			progress.setProgress(ServiceClass.Status);
		}*/


		IssueTitle = (TextView) findViewById(R.id.IssueTitle);
		IssueTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadData();
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	public  void loadData() {


		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Loading...");
		pDialog.show();
		queue = Volley.newRequestQueue(this);

		tv1 = (TextView) findViewById(R.id.swipe_home);
		tv2 = (TextView) findViewById(R.id.swipe_got_a_story);
		tv3 = (TextView) findViewById(R.id.swipe_local_offers);
		tv4 = (TextView) findViewById(R.id.swipe_feedback);

		tv1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//	finish();
				Intent intent = new Intent(MyLibraryList.this,MainActivity.class); //MyLibrary
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("activekey", "home");
				startActivity(intent);
				//finish();
			}
		});

		tv2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MyLibraryList.this,MainActivity.class); //MyLibrary
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("activekey", "got_a_story");
				startActivity(intent);
				//finish();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MyLibraryList.this,MainActivity.class); //MyLibrary
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("activekey", "local_offers");
				startActivity(intent);
				finish();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MyLibraryList.this,MainActivity.class); //MyLibrary
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("activekey", "feedback");
				startActivity(intent);
				finish();
			}
		});

		final JSONObject obj = new JSONObject();
		try {

			obj.put("userID", Const.User_ID);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest myReq = new StringRequest(Method.POST, Const.API_URL,
				createMyReqSuccessListener(), createMyReqErrorListener()) {

			protected Map<String, String> getParams()
					throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("action", "getIssueList");
				params.put("json", obj.toString());
				return params;
			};
		};
		queue.add(myReq);
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 60f);
		return (int) px;
	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					pDialog.hide();
					final JSONObject obj = new JSONObject(response);

					JSONObject resObj = new JSONObject(response);
					resObj.put("responseData", obj);

					Log.d("issue List", " Search result " + resObj);

					setUpResults(resObj);

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
				Toast.makeText(getApplicationContext(), error.toString(),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	private void setUpResults(JSONObject response) {
		imageResults = new ArrayList<MyLibraryRow>();
		gson = new Gson();
		MyLibraryRowItem searchClass = gson.fromJson(response.toString(),
				MyLibraryRowItem.class);

		// make sure there is data in the response

		if (searchClass.getResponse() != null) {
			MyLibraryRow[] results = searchClass.getResponse().getResults();

			List<MyLibraryRow> tempList = Arrays.asList(results);
			imageResults.addAll(tempList);
			adapter1 = new MyLibraryRowAdapter(MyLibraryList.this,
					imageResults, queue, MyLibraryList.this);

			swipelistview = (SwipeListView) findViewById(R.id.example_swipe_lv_list);

			/*swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
				@Override
				public void onOpened(int position, boolean toRight) {
				}

				@Override
				public void onClosed(int position, boolean fromRight) {
				}

				@Override
				public void onListChanged() {
				}

				@Override
				public void onMove(int position, float x) {
				}

				@Override
				public void onStartOpen(int position, int action,
						boolean right) {
					// Log.d("swipe",
							// String.format("onStartOpen %d - action %d",
					// position, action));
				}

				@Override
				public void onStartClose(int position, boolean right) {
					// Log.d("swipe", String.format("onStartClose %d",
					// position));
				}

				@Override
				public void onClickFrontView(int position) {
					Log.d("swipe", String.format("onClickFrontView %d",
							position));

					Toast.makeText(getApplicationContext(),"swip",Toast.LENGTH_SHORT).show();
					// startDownload(position);

					Object downloadLink = swipelistview
							.getItemAtPosition(position);
					MyLibraryRow full = (MyLibraryRow) downloadLink;
					final String downloadUrl = full.getLink();
					if (downloadLink != "") {

						String fileName = downloadUrl.substring(
								downloadUrl.lastIndexOf('/') + 1,
								downloadUrl.length());

						File pdfFile = new File(Environment
								.getExternalStorageDirectory()
								+ "/newspaper/" + fileName);

						if (pdfFile.exists()) {

							Toast.makeText(getApplicationContext(),"CD",Toast.LENGTH_SHORT).show();

							Intent intent = new Intent(
									MyLibraryList.this,
									LibraryPDFReader.class);
							intent.putExtra("PDF_URL",Environment.getExternalStorageDirectory()+ "/newspaper/" + fileName);
							Log.d("PDF_URL",Environment.getExternalStorageDirectory()+"/newspaper/" + fileName);
							startActivity(intent);

						} else {

							Toast.makeText(getApplicationContext(),"AB",Toast.LENGTH_SHORT).show();
							//	startDownload(position, downloadUrl);

							Intent i = new Intent(MyLibraryList.this, MyService.class);
							i.putExtra("position", position);
							i.putExtra("downloadLink", downloadUrl);
							startService(i);
						}
					}
				}

				@Override
				public void onClickBackView(int position) {
				}

				@Override
				public void onDismiss(int[] reverseSortedPositions) {

				}

			});*/




			swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
			swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE);
			swipelistview.setOffsetLeft(convertDpToPixel(80f));
			swipelistview.setAnimationTime(500); // Animation time

			swipelistview.setAdapter(adapter1);
			pd.cancel();


			// swipelistview.setVisibility(View.VISIBLE);

			/*if(MyService.isInstanceCreated()){
				ServiceClass.execute(null);
			}*/

		}

	}


	/*public void startDownload(final int position, final String downloadLink) {
		Log.d("ANDRO_ASYNC", "startDownload: " + position);
		Runnable runnable = new Runnable() {
			int Status = 0;

			public void run() {


	 *//** String urlDownload =
	 * "http://cleethorpeschronicle.co.uk/newspaper/administrator/uploades/pdf/newspaper/1399617106May-8th-2014-edition.pdf"
	 * ;*//*

				String urlDownload = downloadLink;

				Log.d("ANDRO_ASYNC", "urlDownload: " + urlDownload);
				int count = 0;
				try {

					URL url = new URL(urlDownload);
					URLConnection conexion = url.openConnection();
					conexion.connect();

					int lenghtOfFile = conexion.getContentLength();
					Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

					InputStream input = new BufferedInputStream(
							url.openStream());

					// Get File Name from URL
					String fileName = urlDownload.substring(
							urlDownload.lastIndexOf('/') + 1,
							urlDownload.length());

					File direct = new File(
							Environment.getExternalStorageDirectory()
									+ "/newspaper/");

					if (!direct.exists()) {
						direct.mkdirs();
					}

					OutputStream output = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ "/newspaper/" + fileName);

					byte data[] = new byte[1024];
					long total = 0;

					while ((count = input.read(data)) != -1) {
						total += count;
						Status = (int) ((total * 100) / lenghtOfFile);
						output.write(data, 0, count);

						// Update ProgressBar
						handler.post(new Runnable() {
							public void run() {
								updateStatus(position, Status);
							}
						});

					}

					output.flush();
					output.close();
					input.close();

				} catch (Exception e) {

					Log.d("ANDRO_ASYNC",
							"ERROR In Start download: " + e.toString());

				}

			}
		};
		new Thread(runnable).start();
	}
	  */

	/*private void updateStatus(int index, int Status) {

		View v = swipelistview.getChildAt(index
				- swipelistview.getFirstVisiblePosition());

		// Update ProgressBar
		ProgressBar progress = (ProgressBar) v.findViewById(R.id.progressBar);
		progress.setVisibility(View.VISIBLE);
		progress.setProgress(Status);

		// Update Text to ColStatus
		TextView txtStatus = (TextView) v.findViewById(R.id.resultContent);
		txtStatus.setVisibility(View.VISIBLE);
		txtStatus.setPadding(10, 0, 0, 0);
		txtStatus.setText("Load : " + String.valueOf(Status) + "%");

		// Enabled Button View
		if (Status >= 100) {
			progress.setVisibility(View.GONE);
			txtStatus.setVisibility(View.GONE);
		}

	}
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//stopService(new Intent(this, MyService.class));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
}
