package com.dots.newspaper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import som.news.inapp.R;

public class Feedback extends Fragment {
	EditText etFrom , etTo, etSubject, etComment;
	ProgressDialog pDialog;
	Button btn_cancle,btnsend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.feedback,
				null);
		btnsend = (Button) v.findViewById(R.id.btn_send);
		
		etFrom = (EditText)v.findViewById(R.id.et_from);
		etTo = (EditText)v.findViewById(R.id.et_to);
		etSubject = (EditText)v.findViewById(R.id.et_subject);
		etComment = (EditText)v.findViewById(R.id.et_comment);
		btn_cancle = (Button) v.findViewById(R.id.btn_cancle);
		btn_cancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				etFrom.setText("");	
				etSubject.setText("");	
				etComment.setText("");
				
				
			}
		});
		
		btnsend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Sending...");
				pDialog.show();  
				
				final JSONObject obj = new JSONObject();
				try {
				
					obj.put("userID", "Android-user");
					obj.put("to", Const.ADMIN_MAIL_ID);
					obj.put("from", etFrom.getText().toString());
					obj.put("subject", etSubject.getText().toString());
					obj.put("body", etComment.getText().toString());
					

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				RequestQueue queue = Volley.newRequestQueue(getActivity());

				StringRequest myReq = new StringRequest(Method.POST,
						Const.API_URL, createMyReqSuccessListener(),
						createMyReqErrorListener()) {

					protected Map<String, String> getParams()
							throws com.android.volley.AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						
						params.put("action", "emailwithattachment"); //emailwithattachment
						params.put("json", obj.toString());
						return params;
					};
				};
				queue.add(myReq);

			}
		});

		return v;
	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				pDialog.hide();  
				Toast.makeText(getActivity(), "Mail Send", Toast.LENGTH_LONG)
						.show();
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
}
