package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FlickrPhotos extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected FlickrPhotos() {}
	
	// JSNI method to get Flickr photo data.
	public final native int getPage() /*-{ return this.page }-*/;
	public final native int getPages() /*-{ return this.pages }-*/;
	public final native int getPerPage() /*-{ return this.perpage }-*/;
	public final native String getTotal() /*-{ return this.total }-*/;
	public final native JsArray<Photo> getPhoto() /*-{ return this.photo }-*/;
}
