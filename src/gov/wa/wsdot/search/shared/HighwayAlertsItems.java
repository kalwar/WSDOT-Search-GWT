package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class HighwayAlertsItems extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected HighwayAlertsItems() {}
	
	// JSNI methods to get alert item data.
	public final native JsArray<HighwayAlertsItem> getItems() /*-{ return this.items }-*/; 	
}
