package com.dots.newspaper.app.list;

import com.google.gson.annotations.SerializedName;

/**
 * CapTech Consulting Blog
 * 
 *Class that de-serializes all google search responses
 * 
 * @author Clinton Teegarden
 *
 */

public class SearchClass {
	@SerializedName("responseData")
	private ResponseData response;
	
	public ResponseData getResponse(){
		return response;
	}

	public class ResponseData {
		
		@SerializedName("item")
		private SearchResults[] results;

		public SearchResults[] getResults() {
			return results;
		}
	}

	public class SearchResults {
		@SerializedName("title")
		private String Title;
		
		@SerializedName("offer")
		private String Offer;
		
		@SerializedName("link")
		private String Link;
		
		@SerializedName("details")
		private String Content;
		
		
		
		
		@SerializedName("image")
		private String URL;
		
		public String getTitle(){
			return Title;
		}
		
		public String getOffer(){
			return Title;
		}
		
		public String getLink(){
			return Link;
		}
		
		public String getContent(){
			return Content;
		}
		public String getURL(){
			return URL;
		}
	}
}
