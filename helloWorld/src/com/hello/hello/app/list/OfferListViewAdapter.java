package com.dots.newspaper.app.list;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dots.newspaper.app.list.SearchClass.SearchResults;
import com.dots.newspaper.app.volley.LruBitmapCache;
import som.news.inapp.R;

public class OfferListViewAdapter extends BaseAdapter {

	private Context context;
	private List<SearchResults> results;
	private ImageLoader loader;

	// private RequestQueue queue;

	public OfferListViewAdapter(Context context, List<SearchResults> results,
			RequestQueue queue) {
		super();
		this.context = context;
		this.results = results;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.list_item,
					null, false);
			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.resultTitle);
			holder.offer = (TextView) view.findViewById(R.id.resultOffer);
			holder.content = (TextView) view.findViewById(R.id.resultContent);
			holder.resultImage = (NetworkImageView) view
					.findViewById(R.id.resultImage);
			view.setTag(holder);
		}

		SearchResults selectedResult = (SearchResults) getItem(position);

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.title.setText(selectedResult.getTitle());
		holder.offer.setText(selectedResult.getOffer());
		holder.content.setText(Html.fromHtml(selectedResult.getContent()));
		holder.resultImage.setImageUrl(selectedResult.getURL(), loader);

		return view;
	}

	static class ViewHolder {
		TextView title;
		TextView offer;
		TextView content;
		NetworkImageView resultImage;
	}

}
