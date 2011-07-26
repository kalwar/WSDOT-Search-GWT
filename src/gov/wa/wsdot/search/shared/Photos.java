package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class Photos extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected Photos() {}
	
	// JSNI methods to get Flickr photo data.
	public final native FlickrPhotos getPhotos() /*-{ return this.photos }-*/;
}
