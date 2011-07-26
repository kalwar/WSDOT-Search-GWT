package gov.wa.wsdot.search.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class Photo extends JavaScriptObject {
	// Overlay types always have protected, zero argument constructors.
	protected Photo() {}
	
	// JSNI methods to get individual Flickr photo data.
	public final native String getId() /*-{ return this.id }-*/;
	public final native String getOwner() /*-{ return this.owner }-*/;
	public final native String getSecret() /*-{ return this.secret }-*/;
	public final native String getServer() /*-{ return this.server }-*/;
	public final native int getFarm() /*-{ return this.farm }-*/;
	public final native String getTitle() /*-{ return this.title }-*/;
	public final native int getIsPublic() /*-{ return this.ispublic }-*/;
	public final native int getIsFriend() /*-{ return this.isfriend }-*/;
	public final native int getIsFamily() /*-{ return this.isfamily }-*/;
}
