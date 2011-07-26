package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class HighwayAlertsItem extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected HighwayAlertsItem() {}
	
	// JSNI methods to get alert item data.
	public final native String getPriority() /*-{ return this.Priority }-*/;
	public final native int getAlertID() /*-{ return this.AlertID }-*/;
	public final native String getEventCategory() /*-{ return this.EventCategory }-*/;
	public final native String getHeadlineDescription() /*-{ return this.HeadlineDescription }-*/;
	public final native StartRoadwayLocation getStartRoadwayLocation() /*-{ return this.StartRoadwayLocation }-*/;
}
