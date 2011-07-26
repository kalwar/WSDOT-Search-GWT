package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class Search extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected Search() {}
	
	// JSNI methods to get stock data.
	public final native int getStartRecord() /*-{ return this.startrecord }-*/;
	public final native int getEndRecord() /*-{ return this.endrecord }-*/;
	public final native int getTotal() /*-{ return this.total }-*/;
	public final native JsArrayString getRelated() /*-{ return this.related; }-*/;
	public final native JsArray<BoostedResults> getBoostedResults() /*-{ return this.boosted_results; }-*/;
	public final native JsArray<Results> getResults() /*-{ return this.results; }-*/;
}
