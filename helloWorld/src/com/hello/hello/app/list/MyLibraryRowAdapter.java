package com.dots.newspaper.app.list;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import som.news.inapp.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dots.newspaper.Const;
import com.dots.newspaper.MyLibraryList;
import com.dots.newspaper.ServiceClass;
import com.dots.newspaper.app.list.MyLibraryRowItem.MyLibraryRow;
import com.dots.newspaper.app.volley.LruBitmapCache;
import com.radaee.reader.LibraryPDFReader;

public class MyLibraryRowAdapter extends BaseAdapter {

	private static List<MyLibraryRow> results;
	private LayoutInflater mInflater;
	private ImageLoader loader;
	Activity context;
	MyLibraryList listacitivty;
	String downloadUrl , fileName;
	int leng ,notclick =0 , pos , nid;
	File pdfFile;

	ViewHolder holder;
	int mylength;

	public MyLibraryRowAdapter(Activity context, List<MyLibraryRow> results,
			RequestQueue queue, MyLibraryList list) {

		super();
		this.context = context;
		this.results = results;
		listacitivty = list;
		loader = new ImageLoader(queue, new LruBitmapCache());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return results.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return results.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.mylibrary_item, null, false);

			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.resultTitle);
			holder.offer = (TextView) view.findViewById(R.id.resultOffer);
			holder.offer.setTag(position);
			holder.btnDel = (Button) view.findViewById(R.id.swipe_btnDel);
			holder.progress = (ProgressBar) view.findViewById(R.id.progressBar);
			holder.status = (TextView) view.findViewById(R.id.resultContent);
			holder.resultImage = (NetworkImageView) view
					.findViewById(R.id.resultImage);
			view.setTag(holder);

		}

		MyLibraryRow selectedResult = (MyLibraryRow) getItem(position);

		holder = (ViewHolder) view.getTag();
		holder.title.setText(selectedResult.getTitle());

		// holder.content.setText(Html.fromHtml(selectedResult.getContent()));
		holder.resultImage.setImageUrl(selectedResult.getURL(), loader);
		nid = Integer.parseInt(selectedResult.getNID());
		downloadUrl = selectedResult.getLink();
		pos = position;

		fileName = downloadUrl.substring(
				downloadUrl.lastIndexOf('/') + 1, downloadUrl.length());

		pdfFile = new File(Environment.getExternalStorageDirectory()
				+ "/newspaper/" + fileName);


		if(Const.add.size() == results.size()){
		}
		else{
			try {


				mylength = new checkfilelength(downloadUrl , position).execute().get();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}



		if (pdfFile.exists()) {

			if(notclick == 0){

				if(Const.add.get(position) == pdfFile.getAbsoluteFile().length()){

					//	Toast.makeText(context, "inside len equal", Toast.LENGTH_LONG).show();

					holder.offer.setText("Tap to Open");
					//	holder.progress.setVisibility(View.GONE);
					holder.btnDel.setEnabled(true);
					holder.btnDel.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							deleteIssueList(nid, pdfFile);
						}
					});
				}

				else{

					//		Toast.makeText(context, "delete len equal", Toast.LENGTH_LONG).show();

					pdfFile.delete();
					fileName = downloadUrl.substring(
							downloadUrl.lastIndexOf('/') + 1, downloadUrl.length());

					pdfFile = new File(Environment.getExternalStorageDirectory()
							+ "/newspaper/" + fileName);

					holder.offer.setText("Tap to Download");
					holder.progress.setVisibility(View.GONE);
					holder.btnDel.setEnabled(false);

				}
			}


		} else {
			holder.offer.setText("Tap to Download");
			holder.progress.setVisibility(View.GONE);
			holder.btnDel.setEnabled(false);
		}


		holder.offer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*Intent i = new Intent(context, MyService.class);

					i.putExtra("position", pos);
					i.putExtra("downloadLink", downloadUrl);
					context.startService(i);*/	

				int val = position;//(Integer) holder.offer.getTag();

				notclick = 1;

				//	Toast.makeText(context,"CD"+position,Toast.LENGTH_SHORT).show();



				MyLibraryRow selectedResult = (MyLibraryRow) getItem(val);
				downloadUrl = selectedResult.getLink();

				String fileName = downloadUrl.substring(
						downloadUrl.lastIndexOf('/') + 1,
						downloadUrl.length());

				File pdfFile = new File(Environment
						.getExternalStorageDirectory()
						+ "/newspaper/" + fileName);


				if (pdfFile.exists()) {

					Log.e("add length in click",""+Const.add.size());

					//			Toast.makeText(context,"CD",Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(context,LibraryPDFReader.class);
					intent.putExtra("PDF_URL",Environment.getExternalStorageDirectory()+ "/newspaper/" + fileName);
					Log.d("PDF_URL",Environment.getExternalStorageDirectory()+"/newspaper/" + fileName);
					context.startActivity(intent);



				} else {

					if(ServiceClass.changeStatus == 0){
						holder.offer.setClickable(false);
						new ServiceClass(context,position,downloadUrl,holder.status,holder.offer , holder.progress , Const.add.get(position)).execute();
					}
					//		Toast.makeText(context,"asdfCD"+Const.add.size(),Toast.LENGTH_SHORT).show();
				}
			}
		});


		return view;

	}


	protected void deleteIssueList(int nid, File pdfFile) {
		//	Toast.makeText(context, "Delete: " + nid, Toast.LENGTH_SHORT).show();
		File file = pdfFile;
		boolean deleted = file.delete();
		Log.d("Delete", "" + deleted);

		/*
		 * Intent i = new Intent( context, MyLibraryList.class);
		 * context.startActivity(i);
		 */

	}

	static class ViewHolder {
		TextView title;
		TextView offer;
		Button btnDel;
		NetworkImageView resultImage;
		ProgressBar progress;
		TextView status;
	}

	public class checkfilelength extends AsyncTask<Integer , Integer , Integer>{
		int length , position;
		String downloadUr;
		ProgressDialog pd;


		public checkfilelength(String downloadUrl,int p) {
			downloadUr = downloadUrl;
			position = p;

			pd= new ProgressDialog(context);
			pd.setMessage("Loading");
			pd.show();
		}

		@Override
		protected Integer doInBackground(Integer... params) {


			try {
				URL url = new URL(downloadUr);
				URLConnection conexion;
				conexion = url.openConnection();
				conexion.connect();
				length = conexion.getContentLength();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Const.add.add(length);
			return length ;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			pd.cancel();

		}

	}
}
