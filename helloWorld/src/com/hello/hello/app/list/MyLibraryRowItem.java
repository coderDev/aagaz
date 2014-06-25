package com.dots.newspaper.app.list;

import com.google.gson.annotations.SerializedName;

public class MyLibraryRowItem {
	@SerializedName("responseData")
	private ResponseData response;

	public ResponseData getResponse() {
		return response;
	}

	public class ResponseData {

		@SerializedName("item")
		private MyLibraryRow[] results;

		public MyLibraryRow[] getResults() {
			return results;
		}
	}

	public class MyLibraryRow {
		@SerializedName("Title")
		private String Title;

		@SerializedName("Name")
		private String Name;

		@SerializedName("Content")
		private String Link;

		@SerializedName("Cover")
		private String URL;
		
		@SerializedName("nid")
		private String nid;

		public String getTitle() {
			return Title;
		}

		public String getOffer() {
			return Title;
		}

		public String getLink() {
			return Link;
		}

		public String getURL() {
			return URL;
		}
		
		public String getNID() {
			return nid;
		}
	}
}
