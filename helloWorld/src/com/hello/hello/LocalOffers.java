package com.dots.newspaper;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.dots.newspaper.app.AppController;
import com.dots.newspaper.app.MyVolley;
import com.dots.newspaper.app.list.VolleyCaptechApplication;
import com.dots.newspaper.app.volley.ImageLruCache;
import com.dots.newspaper.app.volley.LruBitmapCache;
import som.news.inapp.R;

public class LocalOffers extends Fragment {
	
	private Button makeRequest;
	private ImageView mImageView;
	private RequestQueue queue;
	private ImageLoader imageLoader;
	private ImageListener imageListener;
	private ImageLruCache imageCache;
	private NetworkImageView imgNetWorkView;

	private RequestQueue mRequestQueue;
	ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = LayoutInflater.from(getActivity()).inflate(
				R.layout.image_request_activity_view, null);

		makeRequest = (Button) v.findViewById(R.id.makeRequest);
		mImageView = (ImageView) v.findViewById(R.id.imageLoader);
		// queue = Volley.newRequestQueue(getActivity());

		imgNetWorkView = (NetworkImageView) v.findViewById(R.id.imgNetwork);
		fetchJsonResponse();
		// jary();
		jOpst();
		// jImg();

		return v;
	}

	private void fetchJsonResponse() {

		mRequestQueue = Volley.newRequestQueue(getActivity());

		// Pass second argument as "null" for GET requests
		JsonObjectRequest req = new JsonObjectRequest(
				"http://ip.jsontest.com/", null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							String result = "Your IP Address is "
									+ response.getString("ip");
							Toast.makeText(getActivity(), response.toString(),
									Toast.LENGTH_SHORT).show();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());
					}
				});

		/* Add your Requests to the RequestQueue to execute */
		mRequestQueue.add(req);
	}

	private void jary() {
		// Tag used to cancel the request

		String url = "http://api.androidhive.info/volley/person_array.json";

		final ProgressDialog pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading...");
		pDialog.show();
		RequestQueue queue = Volley.newRequestQueue(getActivity());

		JsonArrayRequest req = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						Log.d("jobj", response.toString());
						pDialog.hide();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("jobj", "Error: " + error.getMessage());
						pDialog.hide();
					}
				});
		Toast.makeText(getActivity(), req.toString(), Toast.LENGTH_SHORT)
				.show();
		// Adding request to request queue
		queue.add(req);

	}

	private void jOpst() {
		String url = Const.API_URL;

		final ProgressDialog pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading...");
		pDialog.show();
		RequestQueue queue = Volley.newRequestQueue(getActivity());
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d("jPost", response.toString());
						pDialog.hide();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("jPost", " error " + error.toString());
						VolleyLog.d("jPost", "Error: " + error.getMessage());
						VolleyLog.d("jPost", "Error: " + error);
						pDialog.hide();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				final JSONObject obj = new JSONObject();
				try {

					obj.put("userID",
							"224c6fcb49b02c6b7bef060ccca7e1ab6c2882f1b55d9a235e1621c1c1fc3aec");
					obj.put("to", Const.ADMIN_MAIL_ID);
					obj.put("from", "asdf");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				params.put("action", "test");
				params.put("json", obj.toString());
				params.put("email", "abc@androidhive.info");
				params.put("password", "password123");

				return params;
			}

		};

		/*final JSONObject obj = new JSONObject();
		try {

			obj.put("userID",
					"224c6fcb49b02c6b7bef060ccca7e1ab6c2882f1b55d9a235e1621c1c1fc3aec");
			obj.put("to", Const.ADMIN_MAIL_ID);
			obj.put("from", "asdf");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest myReq = new StringRequest(Method.POST, Const.API_URL,
				createMyReqSuccessListener(), createMyReqErrorListener()) {

			protected Map<String, String> getParams()
					throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("action", "get_user_status");
				params.put("json", obj.toString());
				return params;
			};
		};*/

		Log.d("jPost", " test " + jsonObjReq.toString());
		queue.add(jsonObjReq);
	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(getActivity(), response, Toast.LENGTH_LONG)
						.show();
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(), error.toString(),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	private void jImg() {
		RequestQueue queue = Volley.newRequestQueue(getActivity());
		ImageLoader imageLoader = new ImageLoader(queue, new LruBitmapCache());

		String url = "https://dl.dropboxusercontent.com/u/57707756/CapTech_Volley_Blog/ctvlogo.png";

		imgNetWorkView.setImageUrl(Const.URL_IMAGE, imageLoader);

		imageLoader.get(Const.URL_IMAGE, ImageLoader.getImageListener(
				mImageView, R.drawable.ic_tab_feedback, R.drawable.ic_restore));

	}

}
