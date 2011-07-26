package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class Results extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected Results() {}
	
	// JSNI methods to get stock data.
	public final native String getTitle() /*-{ return this.title }-*/;
	public final native String getCacheUrl() /*-{ return this.cacheUrl }-*/;
	public final native String getContent() /*-{ return this.content }-*/;
	public final native String getUnescapedUrl() /*-{ return this.unescapedUrl }-*/;
}
