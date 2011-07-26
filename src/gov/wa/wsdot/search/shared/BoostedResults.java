package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class BoostedResults extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected BoostedResults() {}
	
	// JSNI methods to get stock data.
	public final native String getTitle() /*-{ return this.title }-*/;
	public final native String getDescription() /*-{ return this.description }-*/;
	public final native String getUrl() /*-{ return this.url }-*/;
}
