package com.dots.newspaper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dots.newspaper.app.volley.ExtHttpClientStack;
import som.news.inapp.R;

public class Got_a_story extends Fragment {

	public static final int SELECT_FILE = 1887;
	public static final int SELECT_FILE2 = 1998;

	private static final int REQUEST_CAMERA = 1888;
	private static final int REQUEST_CAMERA2 = 1999;

	Button btn_send, btn_cancle;
	ImageView ivImage, ivImage2;
	EditText etFrom, etTo, etSubject, etComment;
	Bitmap mBitmap, mBitmap2;
	ProgressDialog pDialog;
	static String imgPath, imgPath2, mStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.gotstory,
				null);
		ivImage = (ImageView) v.findViewById(R.id.ivImage);
		ivImage2 = (ImageView) v.findViewById(R.id.ivImage2);
		btn_send = (Button) v.findViewById(R.id.btn_send);
		btn_cancle = (Button) v.findViewById(R.id.btn_cancle);
		etFrom = (EditText) v.findViewById(R.id.et_from);
		etTo = (EditText) v.findViewById(R.id.et_to);
		etSubject = (EditText) v.findViewById(R.id.et_subject);
		etComment = (EditText) v.findViewById(R.id.et_comment);
		
		btn_cancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				etFrom.setText("");	
				etSubject.setText("");	
				etComment.setText("");
				ivImage.setImageResource(R.drawable.addimage);
				ivImage2.setImageResource(R.drawable.addimage);;
				
			}
		});
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Sending...");
				pDialog.show();

				mBitmap = Bitmap.createBitmap(ivImage.getWidth(),
						ivImage.getHeight(), Bitmap.Config.RGB_565);
				// img1 = encodeTobase64(mBitmap);

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

				new Thread(new Runnable() {
					public void run() {

						mStatus = postmyData(obj.toString(), imgPath, imgPath2);

					}
				}).start();
				if (mStatus != null) {
					alert(mStatus);
				}

				/*
				 * RequestQueue queue = Volley.newRequestQueue(getActivity());
				 * 
				 * StringRequest myReq = new StringRequest(Method.POST,
				 * Const.API_URL, createMyReqSuccessListener(),
				 * createMyReqErrorListener()) {
				 * 
				 * protected Map<String, String> getParams() throws
				 * com.android.volley.AuthFailureError { Map<String, String>
				 * params = new HashMap<String, String>();
				 * 
				 * params.put("action", "test"); // emailwithattachment
				 * params.put("json", obj.toString());
				 * params.put("image",imgPath); return params; }; };
				 */
				// queue1.add(myReq1);

			}
		});

		ivImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});

		ivImage2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage2();
			}
		});

		return v;
	}

	// AsyncTask<Void, Void, String> uploadImg

	public void uploadFile(String imgPath) {

		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			entity.addPart("to", new StringBody(Const.ADMIN_MAIL_ID));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			entity.addPart("subject", new StringBody("rickroll"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File fileToUpload = new File(imgPath);
		FileBody fileBody = new FileBody(fileToUpload,
				"application/octet-stream");
		entity.addPart("image1", fileBody);
		try {
			entity.addPart("tos", new StringBody("agree"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// prepare post method
		HttpPost post = new HttpPost(Const.API_UPLOAD_URL);

		post.setEntity(entity);

		// create the client and execute the post method
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(post);
			Toast.makeText(getActivity(), response.toString(),
					Toast.LENGTH_LONG).show();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		}

	}

	private String postmyData(String userdata, String img, String img2) {
		try {
			Log.v("TU", "postURL: " + userdata + " \n " + img + " \n " + img2);

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Const.API_URL);
			StringBody data = new StringBody(userdata,
					Charset.forName(HTTP.UTF_8));
			StringBody data_action = new StringBody("emailwithattachment",
					Charset.forName(HTTP.UTF_8));

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			entity.addPart("action", data_action);
			entity.addPart("json", data);

			if (img != null) {
				entity.addPart("image1", new FileBody(new File(img),
						"image/png"));
			}
			if (img2 != null) {
				entity.addPart("image2", new FileBody(new File(img2),
						"image/png"));
			}

			httppost.setEntity(entity);
			HttpResponse WSresponse = httpclient.execute(httppost);

			if (WSresponse != null) {
				HttpEntity resEntity = WSresponse.getEntity();
				Log.d(Const.DEBUGTAG, resEntity.toString());
				pDialog.dismiss();
				return "Mail Send Sucessfully";

			}
		} catch (Throwable e) {
			pDialog.dismiss();
			Log.d(Const.DEBUGTAG, e.toString());
		}
		return "Please try again";
	}

	public String newImageString(String path) {

		File imgFile = new File(path);
		Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); // compress to
																// which format
																// you want.
		byte[] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeToString(byte_arr, 0);
		return image_str;
	}

	private Response.Listener<String> createMyReqSuccessListener() {

		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				pDialog.hide();

				Log.d("Got-Story", response);
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

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private void selectImage2() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

					startActivityForResult(intent, REQUEST_CAMERA2);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE2);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Toast.makeText(getActivity(), "rq:" + resultCode +" RESULT_OK:" +
		// getActivity().RESULT_OK, Toast.LENGTH_LONG).show();

		if (resultCode == FragmentActivity.RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					imgPath = f.getAbsolutePath();
					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
							btmapOptions);

					bm = Bitmap.createScaledBitmap(bm, ivImage2.getWidth(),
							ivImage2.getHeight(), true);
					ivImage.setImageBitmap(bm);

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream fOut = null;
					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");
					try {
						fOut = new FileOutputStream(file);
						bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (requestCode == REQUEST_CAMERA2) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					imgPath2 = f.getAbsolutePath();

					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
							btmapOptions);

					bm = Bitmap.createScaledBitmap(bm, ivImage2.getWidth(),
							ivImage2.getHeight(), true);
					ivImage2.setImageBitmap(bm);

					/*
					 * bm =
					 * Bitmap.createBitmap(ivImage2.getWidth(),ivImage2.getHeight
					 * (), Bitmap.Config.RGB_565); ivImage2.setImageBitmap(bm);
					 * Canvas canvas = new Canvas(bm); ivImage2.draw(canvas);
					 */

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream fOut = null;
					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");
					try {
						fOut = new FileOutputStream(file);
						bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, getActivity());
				imgPath = tempPath;
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				bm = Bitmap.createScaledBitmap(bm, ivImage.getWidth(),
						ivImage.getHeight(), true);

				ivImage.setImageBitmap(bm);
			}

			else if (requestCode == SELECT_FILE2) {
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, getActivity());
				imgPath2 = tempPath;
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				bm = Bitmap.createScaledBitmap(bm, ivImage2.getWidth(),
						ivImage2.getHeight(), true);
				ivImage2.setImageBitmap(bm);
			}
		}
	}

	public String getPath(Uri uri, Activity activity) {

		Uri selectedImage = uri;
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = activity.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);

		cursor.close();
		return picturePath;

		/*
		 * ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		 * imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		 * 
		 * 
		 * String[] projection = { MediaColumns.DATA };
		 * 
		 * 
		 * Cursor cursor = activity .managedQuery(uri, projection, null, null,
		 * null); int column_index =
		 * cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		 * cursor.moveToFirst(); return cursor.getString(column_index);
		 */
	}

	private String encodeTobase64(Bitmap image) {
		// RegistrationDetailToUpload.getInstance().mIsImageAttached = true;
		Bitmap immagex = image;
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

			Log.e("LOOK", imageEncoded);
			return imageEncoded;
		} else
			return null;
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(Const.DEBUGTAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

}
