package com.dots.newspaper.app.list.listener;

import java.util.List;

import com.dots.newspaper.MainActivity;
import com.dots.newspaper.OfferDetail;
import com.dots.newspaper.SplashScreen;
import com.dots.newspaper.app.list.SearchClass.SearchResults;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;



public class ListListener implements OnItemClickListener {

	// List item's reference
	List<SearchResults> listItems;
	// Calling activity reference
	Activity activity;
	
	public ListListener(List<SearchResults> aListItems, Activity anActivity) {
		listItems = aListItems;
		activity  = anActivity;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		// We create an Intent which is going to display data
       // Intent i = new Intent(Intent.ACTION_VIEW);
        // We have to set data for our new Intent
       // Toast.makeText(activity, listItems.get(pos).getLink(), Toast.LENGTH_SHORT).show();
       // i.setData(Uri.parse(listItems.get(pos).getLink()));
        // And start activity with our Intent
       // activity.startActivity(OfferDetail.class, i);
        
		
		Intent intent = new Intent();
		intent.setClass(activity, OfferDetail.class);
		intent.putExtra("OFFER_URL", listItems.get(pos).getLink());
		Log.d("link",listItems.get(pos).getLink());
		activity.startActivity(intent);
		
		
     
	}
	
}

