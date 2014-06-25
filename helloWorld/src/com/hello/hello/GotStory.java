package com.dots.newspaper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import som.news.inapp.R;

public class GotStory extends Fragment {

	public static final int SELECT_FILE = 1887;
	public static final int SELECT_FILE2 = 1998;

	private static final int REQUEST_CAMERA = 1888;
	private static final int REQUEST_CAMERA2 = 1999;

	Button btn_send;
	ImageView ivImage, ivImage2;
	EditText etFrom, etTo, etSubject, etComment;
	Bitmap  mBitmap, mBitmap2;
	ProgressDialog pDialog;

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
		etFrom = (EditText)v.findViewById(R.id.et_from);
		etTo = (EditText)v.findViewById(R.id.et_to);
		etSubject = (EditText)v.findViewById(R.id.et_subject);
		etComment = (EditText)v.findViewById(R.id.et_comment);

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Sending...");
				pDialog.show();  
				
				/*mBitmap =  Bitmap.createBitmap (ivImage.getWidth(), ivImage.getHeight(), Bitmap.Config.RGB_565);
				String img1 = encodeTobase64(mBitmap);*/

				final JSONObject obj = new JSONObject();
				try {

					obj.put("userID", "Android-user");
					obj.put("to", Const.ADMIN_MAIL_ID);
					obj.put("from", etFrom.getText().toString());
					obj.put("subject", etSubject.getText().toString());
					obj.put("body", etComment.getText().toString());
					/*obj.put("image1", img1);*/
					

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

					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
							btmapOptions);

					bm = Bitmap.createScaledBitmap(bm, ivImage2.getWidth(), ivImage2.getHeight(), true);
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

					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
							btmapOptions);

					bm = Bitmap.createScaledBitmap(bm, ivImage2.getWidth(), ivImage2.getHeight(), true);
					ivImage2.setImageBitmap(bm);
					
					/*bm = Bitmap.createBitmap(ivImage2.getWidth(),ivImage2.getHeight(), Bitmap.Config.RGB_565);
					ivImage2.setImageBitmap(bm);
					Canvas canvas = new Canvas(bm);
					ivImage2.draw(canvas);*/

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
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				ivImage.setImageBitmap(bm);
			}

			else if (requestCode == SELECT_FILE2) {
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, getActivity());
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
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
}
