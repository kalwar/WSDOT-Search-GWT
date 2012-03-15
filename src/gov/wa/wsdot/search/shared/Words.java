package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class Words extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected Words() {}
	
	// JSNI methods to get type-ahead data.
	public final native JsArrayString getWords() /*-{ return this; }-*/;
}
