package com.dots.newspaper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import som.news.inapp.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ServiceClass extends AsyncTask<Integer,Integer,Integer> {

	public int position = -1;
	public static int Statuss=0  ,lenghtOfFile;
	public static int changeStatus =0;
	private String downloadLink;
	private TextView txtStatus , txtOffer;
	private Handler handler = new Handler();
	ProgressBar progress;
	Activity context;
	OutputStream output;
	byte data[] ;
	ProgressDialog pd;

	public ServiceClass(Activity ctx,int pos, String downloadLin, TextView status , TextView offer , ProgressBar pb , int leng) {
		this.position = pos;
		this.downloadLink = downloadLin;

		context = ctx;
		pd = new ProgressDialog(context);
		pd.setMessage("Loading");
		pd.show();

		txtOffer = offer;
		txtStatus = status;
		progress = pb;
		lenghtOfFile = leng;

		Log.d("a",""+position + downloadLink);

		//Toast.makeText(context.getApplicationContext(), "a"+position + downloadLink, Toast.LENGTH_LONG).show();

	}

	@Override
	protected Integer doInBackground(Integer... params) {

		String urlDownload = downloadLink;

		Log.d("ANDRO_ASYNC", "urlDownload: " + urlDownload);
		int count = 0;
		try {

			URL url = new URL(urlDownload);
			URLConnection conexion = url.openConnection();
			conexion.connect();

		/*  int lenghtOfFile = conexion.getContentLength();
			Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);*/

			InputStream input = new BufferedInputStream(url.openStream());

			// Get File Name from URL
			String fileName = urlDownload.substring(urlDownload.lastIndexOf('/') + 1,urlDownload.length());

			File direct = new File(Environment.getExternalStorageDirectory() + "/newspaper/");

			if (!direct.exists()) {
				direct.mkdirs();
			}

			output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/newspaper/" + fileName);

			data = new byte[1024];
			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				Statuss = (int) ((total * 100) / lenghtOfFile);
				output.write(data, 0, count);

				// Update ProgressBar
				handler.post(new Runnable() {
					public void run() {
						updateStatus(position, Statuss);

					}
				});
			}
			//new updateStatus(position,Statuss, txtStatus , txtOffer, pd ,downloadLink).execute();

			output.flush();
			output.close();
			input.close();

		} catch (Exception e) {

			Log.d("ANDRO_ASYNC","ERROR In Start download: " + e.toString());

		}
		return Statuss;
	}

	public int updateStatus(int index, int Status) {

		if(position!= -1){
		
		View v = MyLibraryList.swipelistview.getChildAt(position);
		progress = (ProgressBar) v.findViewById(R.id.progressBar);

		txtStatus = (TextView) v.findViewById(R.id.resultContent);
		txtOffer = (TextView) v.findViewById(R.id.resultOffer);


		//txtOffer.setText("Downloading");
		txtOffer.setClickable(false);

		Log.e("IN updateStatus" , " In updateStatus" + Status + downloadLink + "output : ");
		// Update ProgressBar
		progress.setVisibility(View.VISIBLE);
		progress.setProgress(Status);

		// Update Text to ColStatus
		txtStatus.setVisibility(View.VISIBLE);
		txtStatus.setPadding(10, 0, 0, 0);
		txtStatus.setText("Load : " + String.valueOf(Status) + "%");

		pd.dismiss();

		/*if(Status >0){
			pd.dismiss();
		}*/

		// Enabled Button View
		if (Status >= 100) {
			progress.setVisibility(View.GONE);
			txtStatus.setVisibility(View.GONE);
			txtOffer.setText("Tap to Open");
			Statuss = Status;
			txtOffer.setClickable(true);
		}

		changeStatus = Status;

		return Status;
		
		}
		return Status;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		
	}
}
