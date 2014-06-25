package com.dots.newspaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dots.newspaper.app.list.OfferListViewAdapter;
import com.dots.newspaper.app.list.SearchClass;
import com.dots.newspaper.app.list.SearchClass.SearchResults;
import com.dots.newspaper.app.list.listener.ListListener;
import som.news.inapp.R;
import com.google.gson.Gson;

public class Offer extends Fragment {

	private ListView resultList;
	private ProgressBar progressBar;
	private RequestQueue queue;
	private Gson gson;

	ProgressDialog pDialog;

	private OfferListViewAdapter adapter;

	private List<SearchResults> imageResults;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(
				R.layout.networkimage_search_activity_view, null);
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading...");
		pDialog.show();
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		resultList = (ListView) v.findViewById(R.id.resultsList);
		queue = Volley.newRequestQueue(getActivity());

		final JSONObject obj = new JSONObject();
		try {

			obj.put("userID", "");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest myReq = new StringRequest(Method.POST, Const.API_URL,
				createMyReqSuccessListener(), createMyReqErrorListener()) {

			protected Map<String, String> getParams()
					throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("action", "localoffer");
				params.put("json", obj.toString());
				return params;
			};
		};
		queue.add(myReq);

		return v;
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

					// Log.d("jPost", " Search result " + resObj);

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
				Toast.makeText(getActivity(), error.toString(),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	private void setUpResults(JSONObject response) {
		imageResults = new ArrayList<SearchResults>();
		gson = new Gson();
		SearchClass searchClass = gson.fromJson(response.toString(),
				SearchClass.class);

		/* Log.d("jPost", " Search result " + searchClass.getResponse()); */

		// make sure there is data in the response

		if (searchClass.getResponse() != null) {
			SearchResults[] results = searchClass.getResponse().getResults();

			Log.d("jPost", " Search result " + results);
			List<SearchResults> tempList = Arrays.asList(results);

			imageResults.addAll(tempList);
			adapter = new OfferListViewAdapter(getActivity(), imageResults,
					queue);
			resultList.setAdapter(adapter);

			resultList.setOnItemClickListener(new ListListener(imageResults,
					getActivity()));
			resultList.setVisibility(View.VISIBLE);
			// get the next 8 results in the list
			/*
			 * String searchURL = url + searchText + "&start=8&rsz=8";
			 * extendedRequest = new JsonObjectRequest(Request.Method.GET,
			 * searchURL, null, new ExtendedResponseListener(), new
			 * ErrorListener()); queue.add(extendedRequest);
			 */
			progressBar.setVisibility(View.GONE);
		}

	}

	/*
	 * private void addExtendedResults(JSONObject response) { SearchClass
	 * searchClass = gson.fromJson(response.toString(), SearchClass.class); if
	 * (searchClass.getResponse() != null) { SearchResults[] results =
	 * searchClass.getResponse().getResults(); List<SearchResults> tempList =
	 * Arrays.asList(results); imageResults.addAll(tempList);
	 * resultList.setAdapter(adapter); } }
	 */
}
