package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class StartRoadwayLocation extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected StartRoadwayLocation() {}
	
	// JSNI methods to get alert item data.
	public final native String getDescription() /*-{ return this.Description }-*/;
	public final native String getRoadName() /*-{ return this.RoadName }-*/;
	public final native double getLongitude() /*-{ return this.Longitude }-*/;
	public final native double getLatitude() /*-{ return this.Latitude }-*/;
}
