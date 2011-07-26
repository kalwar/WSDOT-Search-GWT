package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class HighwayAlerts extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected HighwayAlerts() {}
	
	// JSNI methods to get stock data.
	public final native HighwayAlertsItems getAlerts() /*-{ return this.alerts }-*/;
}
