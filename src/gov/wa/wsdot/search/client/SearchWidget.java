/*
 * Copyright (c) 2012 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package gov.wa.wsdot.search.client;

import gov.wa.wsdot.search.shared.BoostedResults;
import gov.wa.wsdot.search.shared.HighwayAlerts;
import gov.wa.wsdot.search.shared.HighwayAlertsItem;
import gov.wa.wsdot.search.shared.Photo;
import gov.wa.wsdot.search.shared.Photos;
import gov.wa.wsdot.search.shared.Results;
import gov.wa.wsdot.search.shared.Search;
import gov.wa.wsdot.search.shared.Words;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SearchWidget extends Composite implements ValueChangeHandler<String> {
	
	interface SearchWidgetUiBinder extends UiBinder<Widget, SearchWidget> { }
	private static SearchWidgetUiBinder uiBinder = GWT.create(SearchWidgetUiBinder.class);
	
	@UiField
	Button searchButton;
	
	@UiField
	static DisclosurePanel alertsDisclosurePanel;

	@UiField
	static DisclosurePanel photosDisclosurePanel;

	@UiField
	static HTML searchResultsForHTML;

	@UiField
	static HTML searchResultsHTML;

	@UiField
	static HTMLPanel alertsHTMLPanel;

	@UiField
	static HTMLPanel paginationHTMLPanel;

	@UiField
	static HTMLPanel bingLogoHTMLPanel;

	@UiField
	static HTMLPanel leftNavBoxHTMLPanel;

	@UiField
	static HTMLPanel relatedTopicsHTMLPanel;

	@UiField
	static HTMLPanel flickrPhotosHTMLPanel;

	@UiField
	static HTMLPanel boostedResultsHTMLPanel;

	@UiField
	static Image loadingImage;

	@UiField
	HorizontalPanel searchPanel;

	@UiField(provided=true)
	SuggestBox searchSuggestBox;
	
	private static final boolean ANALYTICS_ENABLED = true; 
	
	/**
	 * To use the Flickr API you need to have an application key. An application key can
	 * be applied for at the following URL:
	 * 
	 * http://www.flickr.com/services/api/misc.api_keys.html
	 */
	private static final String FLICKR_API_KEY = "INSERT_YOUR_FLICKR_API_KEY_HERE";
	private static final String FLICKR_USER_ID = "INSERT_YOUR_FLICKR_USER_ID_HERE";
	private static final String FLICKR_NUMBER_OF_PHOTOS = "12";	
	
	/**
	 * Use of the Search Data API requires you to register and join the
	 * USASearch Affiliate Program. Your site must also be affiliated with
	 * a government agency.
	 * 
	 * You can register or obtain more information at the following URL:
	 * 
	 * https://search.usa.gov/affiliates
	 */
	private static final String SEARCH_USA_AFFILIATE = "INSERT_YOUR_SEARCH_USA_AFFILIATE_HERE";
	private static final String SEARCH_USA_API_KEY = "INSERT_YOUR_SEARCH_USA_API_KEY_HERE";
	private static final String SEARCH_USA_AFFILIATE_ID = "INSERT_YOUR_SEARCH_USA_AFFILIATE_ID_HERE";
	
	private static final String JSON_URL = "http://search.usa.gov/api/search?affiliate=" + SEARCH_USA_AFFILIATE + "&api_key=" + SEARCH_USA_API_KEY + "&format=json&query=";
	private static final String JSON_URL_FLICKR = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + FLICKR_API_KEY + "&user_id=" + FLICKR_USER_ID + "&per_page=" + FLICKR_NUMBER_OF_PHOTOS + "&format=json&text=";
	private static final String JSON_URL_SUGGESTION = "http://search.usa.gov/sayt?aid=" + SEARCH_USA_AFFILIATE_ID + "&q=";
	private static final String JSON_URL_HIGHWAY_ALERTS = "http://data.wsdot.wa.gov/mobile/HighwayAlertsCallback.js";
	private static final String EVENT_TRACKING_CATEGORY = "Search"; // The Event Category title in Google Analytics 
	
	private static BulletList list = new BulletList();
	private Timer timer;
	
	private final static MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	
	public SearchWidget() {
		searchSuggestBox = new SuggestBox(oracle); // Need to create SuggestBox before initializing uiBinder
		initWidget(uiBinder.createAndBindUi(this));
		loadingImage.setVisible(false);
		bingLogoHTMLPanel.setVisible(false);
		leftNavBoxHTMLPanel.setVisible(false);
		boostedResultsHTMLPanel.setVisible(false);
		photosDisclosurePanel.setVisible(false);
		alertsDisclosurePanel.setVisible(false);
		
		String queryString = Window.Location.getParameter("q");
		if (queryString != null) {
			History.newItem("q=" + queryString);
		}
		
		/* Listen for keyboard events in the input box.
		 * 
		 * KeyPressEvent.getCharCode returns zero for keys like ENTER in FF.
		 * Workaround is to use KeyDown rather than KeyPress Handler.
		 */
		searchSuggestBox.addKeyUpHandler(new KeyUpHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onKeyUp(KeyUpEvent event) {
				int keyCode = event.getNativeKeyCode();
				if (keyCode == KeyCodes.KEY_ENTER) {
					History.newItem("q=" + searchSuggestBox.getText().trim());
				} else if ((keyCode == KeyCodes.KEY_DOWN) || (keyCode == KeyCodes.KEY_UP) ||
						(keyCode == KeyCodes.KEY_LEFT) || (keyCode == KeyCodes.KEY_RIGHT) ||
						(keyCode == KeyCodes.KEY_ALT) || (keyCode == KeyCodes.KEY_CTRL)) {
					return;
				} else if (keyCode == KeyCodes.KEY_ESCAPE) {
					// TODO: hideSuggestionList() is deprecated. Need to use DefaultSuggestionDisplay instead.
					searchSuggestBox.hideSuggestionList();
				} else {
					if (timer != null) timer.cancel();
					timer = new Timer() {
						@Override
						public void run() {
							if (!searchSuggestBox.getText().trim().isEmpty()) {
								getSuggestions();
							}
						}
					};
					timer.schedule(250);
				}
			}
		});
		
		// Listen for selection events from the type ahead SuggestBox.
		searchSuggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				History.newItem("q=" + event.getSelectedItem().getReplacementString());
			}});
				
		// Listen for click events on the search button.
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				History.newItem("q=" + searchSuggestBox.getText().trim());
			}
		});
		
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}

	private void getSuggestions() {
		String url = JSON_URL_SUGGESTION;
		String searchString = SafeHtmlUtils.htmlEscape(searchSuggestBox.getText().trim().replace("'", ""));
		
		// Append the name of the callback function to the JSON URL.
		url += searchString;
		url = URL.encode(url);
		getSuggestionData(url);
	}
	
	/**
	 * This method is called whenever the application's history changes.
	 * 
	 * Calling the History.newItem(historyToken) method causes a new history entry to be
	 * added which results in ValueChangeEvent being called as well.
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String url = JSON_URL;
		String url_flickr = JSON_URL_FLICKR;
		String url_highway_alerts = JSON_URL_HIGHWAY_ALERTS;
		String historyToken = event.getValue();
		
		// This handles the initial GET call to the page. Rewrites the ?q= to #q=
		String queryParameter = Window.Location.getParameter("q");
		if (queryParameter != null) {
			UrlBuilder urlBuilder = Window.Location.createUrlBuilder().removeParameter("q");
			urlBuilder.setHash(historyToken);
			String location = urlBuilder.buildString();
			Window.Location.replace(location);
		}
		
		String[] tokens = historyToken.split("&"); // e.g. #q=Ferries&p=2
		Map<String, String> map = new HashMap<String, String>();
		for (String string : tokens) {
			try {
				map.put(string.split("=")[0], string.split("=")[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: Need a better way to handle this.
			}
		}
		
		String query = map.get("q");
		String page = map.get("p");
		
		if (page == null) {
			page = "1";
		}
		
		searchSuggestBox.setText(query);
		String searchString = SafeHtmlUtils.htmlEscape(searchSuggestBox.getText().trim().replace("'", "").replace("\"", ""));
		loadingImage.setVisible(true);
		
		searchSuggestBox.setFocus(true);
		leftNavBoxHTMLPanel.setVisible(false);
		photosDisclosurePanel.setVisible(false);
		boostedResultsHTMLPanel.setVisible(false);
		alertsDisclosurePanel.setVisible(false);
		
		if (searchString.isEmpty()) {
			clearPage();
			loadingImage.setVisible(false);
			bingLogoHTMLPanel.setVisible(false);
		} else {
			if (ANALYTICS_ENABLED) {
				Analytics.trackEvent(EVENT_TRACKING_CATEGORY, "Keywords", searchString.toLowerCase());
			}
			clearPage();
			
			// Append the name of the callback function to the JSON URL.
			url += searchString;
			url += "&page=" + page;
			url = URL.encode(url);
			
			// Append search query to Flickr url.
			url_flickr += searchString;
			url_flickr = URL.encode(url_flickr);
			
			// Send requests to remote servers with calls to JSNI methods.
			getSearchData(url, searchString, page);
			getPhotoData(url_flickr, searchString);
			getHighwayAlertsData(url_highway_alerts, searchString);
		}		
	}	

	/**
	 * Clear content out of widgets on page.
	 */
	private void clearPage() {
		boostedResultsHTMLPanel.clear();
		searchResultsForHTML.setHTML("");
		searchResultsHTML.setHTML("");
		list.clear();
		relatedTopicsHTMLPanel.clear();
		flickrPhotosHTMLPanel.clear();
		paginationHTMLPanel.clear();
		oracle.clear();
		alertsHTMLPanel.clear();
	}
	
	/**
	 * Make call to remote search.usa.gov server for search result data
	 * 
	 * @param page 
	 * @param query 
	 * @param url 
	 */
	private static void getSearchData(String url, final String query, final String page) {
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();

		jsonp.requestObject(url, new AsyncCallback<Search>() {

			@Override
			public void onFailure(Throwable caught) {
				searchResultsForHTML.setHTML("<h4>Sorry. We had a problem getting the results for you.</h4>");
				loadingImage.setVisible(false);
			}

			@Override
			public void onSuccess(Search search) {
				if (search.getResults() != null) {
					updateResults(search, query, page);
				}
			}

		});		
	}

	/**
	 * Make call to remote Flickr server
	 * @param query 
	 * @param url_flickr 
	 */
	private static void getPhotoData(String url, final String query) {
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.setCallbackParam("jsoncallback");

		jsonp.requestObject(url, new AsyncCallback<Photos>() {

			@Override
			public void onFailure(Throwable caught) {
				// Just fail silently here.
			}

			@Override
			public void onSuccess(Photos photos) {
				if (photos.getPhotos().getPhoto() != null) {
					updatePhotoResults(photos, query);
				}
			}

		});	
	}

	/**
	 * Make call to remote search.usa.gov server for type ahead results
	 */	
	private static void getSuggestionData(String url) {
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();

		jsonp.requestObject(url, new AsyncCallback<Words>() {

			@Override
			public void onFailure(Throwable caught) {
				// Just fail silently here.
			}

			@Override
			public void onSuccess(Words words) {
				if (words.getWords() != null) {
					updateSuggestions(words);
				}
			}

		});	
	}	

	/**
	 * Make call to WSDOT data server to retrieve current high impact alerts.
	 */	
	private static void getHighwayAlertsData(String url, final String query) {
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.setPredeterminedId("HA");

		jsonp.requestObject(url, new AsyncCallback<HighwayAlerts>() {

			@Override
			public void onFailure(Throwable caught) {
				// Just fail silently here.
			}

			@Override
			public void onSuccess(HighwayAlerts alerts) {
				if (alerts.getAlerts().getItems() != null) {
					updateHighwayAlertsResults(alerts, query);
				}
			}

		});
	}	
	
	/**
	 * Populate highway alerts box with highest and high impact alerts.
	 * 
	 * @param alerts
	 * @param query
	 */
	private static void updateHighwayAlertsResults(HighwayAlerts alerts, String query) {
		JsArray<HighwayAlertsItem> items = alerts.getAlerts().getItems();
		
		if (items.length() > 0) {
			String[] tokens = query.toLowerCase().split("\\s+");
			for (int i=0; i < items.length(); i++) {
				String roadName = items.get(i).getStartRoadwayLocation().getRoadName();
				int temp = 0;
				try {
					temp = Integer.parseInt(roadName); // Convert to Integer to remove leading zeros.
				}
				catch (NumberFormatException e) { // Catches "097AR"
					temp = Integer.parseInt(roadName.substring(0, 3)); // Now convert and remove leading zeros.
				}
				roadName = temp + "";
				for (String t: tokens) {
					if (t.matches("(i|i-|us|sr)?" + roadName)) {
						alertsHTMLPanel.add(addAlerts(items.get(i)));
						alertsDisclosurePanel.setVisible(true);
					}
				}
			}
		}
	}

	private static Widget addAlerts(final HighwayAlertsItem item) {
		double lat = item.getStartRoadwayLocation().getLatitude();
		double lon = item.getStartRoadwayLocation().getLongitude();
		final String map = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lon + "&zoom=14&size=600x400&markers=|" + lat + "," + lon + "|&sensor=false";
		
		Anchor link = new Anchor();
		link.setText("View location");
		link.setHref("javascript:;");
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (ANALYTICS_ENABLED) {
					Analytics.trackEvent(EVENT_TRACKING_CATEGORY, "Travel Alerts", item.getEventCategory());
				}				
				String description = "<b>" + item.getEventCategory() + "</b> " + item.getHeadlineDescription();
				Popup popup = new Popup(map, description, item.getAlertID());
				popup.setAnimationEnabled(true);
				popup.setGlassEnabled(true);
				popup.setVisible(false);
				popup.show();
			}
		});
		
		HTML content = new HTML("<b>" + item.getEventCategory() +
				"</b><br />"  + item.getHeadlineDescription());
		HTMLPanel contentPanel = new HTMLPanel(content.toString());
		contentPanel.addStyleName("alert-content");
		contentPanel.add(link);
		
		return contentPanel;
	}

	/**
	 * Populate type ahead box.
	 * 
	 * @param words Words to add to suggestion oracle
	 */
	private static void updateSuggestions(JavaScriptObject words) {
	    for (int i = 0; i < ((JsArrayString) words).length(); ++i) {
	        oracle.add(((JsArrayString) words).get(i));
	    }
	}

	private static void updatePhotoResults(Photos photos, String query) {
		int totalPages = photos.getPhotos().getPages();
		JsArray<Photo> photo = photos.getPhotos().getPhoto();
		if (photo.length() > 0) {
			for (int i=0; i < photo.length(); i++) {
				flickrPhotosHTMLPanel.add(addImage(photo.get(i)));
			}
			if (totalPages > 1) {
				flickrPhotosHTMLPanel.add(new HTML("<p style=\"font-size:1em;\"><a href=\"http://www.flickr.com/search/?w=" + FLICKR_USER_ID + "&q=" + query + "\" style=\"color:#036\">View more photos</a></p>"));
			}
			photosDisclosurePanel.setVisible(true);
		}
		loadingImage.setVisible(false);
	}

	private static Widget addImage(final Photo photo) {
		Image image = new Image("http://farm" + photo.getFarm() + ".static.flickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_s.jpg");
		image.setTitle(photo.getTitle());
		image.addStyleName("photo-thumbnail");
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (ANALYTICS_ENABLED) {
					Analytics.trackEvent(EVENT_TRACKING_CATEGORY, "Photos", photo.getTitle() + " (" + photo.getId() + ")");
				}
				Popup popup = new Popup("http://farm" + photo.getFarm() + ".static.flickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + ".jpg", photo.getTitle(), photo.getId());
				popup.setAnimationEnabled(true);
				popup.setGlassEnabled(true);
				popup.setVisible(false);
				popup.show();
			}
		});
		
		return image;
	}

	private static void updateResults(Search searchData, String query, String page) {
		StringBuilder sb = new StringBuilder();
		JsArray<Results> searchResults = searchData.getResults();
		JsArrayString searchRelated = searchData.getRelated();
		JsArray<BoostedResults> boostedResults = searchData.getBoostedResults();
		NumberFormat fmt = NumberFormat.getDecimalFormat();
		
		// See if there are any related topics results.
		if (searchRelated.length() > 0) {
			for (int j=0; j < searchRelated.length(); j++) {
				ListItem item = new ListItem();
				Anchor a = new Anchor();
				item.add(addSearchRelated(a, searchRelated.get(j)));
				list.add(item);
			}
			relatedTopicsHTMLPanel.add(list);
			leftNavBoxHTMLPanel.setVisible(true);
		}
		
		// See if there are any boosted results.
		if (boostedResults.length() > 0) {
			boostedResultsHTMLPanel.setVisible(true);
			for (int i=0; i < boostedResults.length(); i++) {
				boostedResultsHTMLPanel.add(new HTML("<p><span style=\"font-size:1.2em;\"><a href=\""+ boostedResults.get(i).getUrl() + "\" style=\"color:#036;\">"+ boostedResults.get(i).getTitle() +"</a></span><br />" +
						boostedResults.get(i).getDescription() + "<br />" +
						"<a href=\""+ boostedResults.get(i).getUrl() +"\" style=\"color:#488048\">"+ boostedResults.get(i).getUrl() +"</a></p>"));
			}
		}
		
		// See if there are any returned results.
		if (searchResults.length() > 0) {
			bingLogoHTMLPanel.setVisible(true);
			searchResultsForHTML.setHTML("<h4 style=\"line-height:2em;\">Results " + searchData.getStartRecord() + "-" + searchData.getEndRecord() + " of about " + fmt.format(searchData.getTotal()) + " for \"" + query + "\"</h4>");
			for (int i=0; i < searchResults.length(); i++) {
				sb.append(buildResult(searchResults.get(i)));
			}		
			searchResultsHTML.setHTML(sb.toString());
			int totalPages = (searchData.getTotal() + 10 - 1) / 10; // (results + resultsPerPage - 1) / resultsPerPage
			PageLinks pageLinks = new PageLinks(totalPages, Integer.parseInt(page), query);
			paginationHTMLPanel.add(pageLinks);
		} else {
			searchResultsForHTML.setHTML("<h4>Sorry. No results found for \"" + query + "\".</h4>");
		}
		loadingImage.setVisible(false);
	}

	private static Widget addSearchRelated(Anchor a, final String result) {
		a.setText(result);
		a.setHref("javascript:;");
		a.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				History.newItem("q=" + result);
			}
		});
		return a;
	}

	private static String buildResult(Results result) {
		StringBuilder sb = new StringBuilder();
		String title = result.getTitle().replace("\ue000", "<b>").replace("\ue001", "</b>");
		String content = result.getContent().replace("\ue000", "<b>").replace("\ue001", "</b>");
		
		String[] parsedUrl = result.getUnescapedUrl().split("/");
		StringBuilder prettyUrl = new StringBuilder();
		
		if (parsedUrl.length > 4) {
			prettyUrl.append(parsedUrl[0]);
			prettyUrl.append("//");
			prettyUrl.append(parsedUrl[2]);
			prettyUrl.append("/.../");
			prettyUrl.append(parsedUrl[parsedUrl.length-1]);
		} else {
			prettyUrl.append(result.getUnescapedUrl());
		}
		
		sb.append("<p>");
		sb.append("<span style=\"font-size:1.1em;\"><a href=\"" + result.getUnescapedUrl() + "\" style=\"color:#036;\">");
		sb.append(title);
		sb.append("</a></span>");
		sb.append("<br />");
		sb.append(content);
		sb.append("<br />");
		sb.append("<a href=\"" + result.getUnescapedUrl() + "\" style=\"color:#488048\">");
		sb.append(prettyUrl.toString());
		sb.append("</a>");
		sb.append(" - ");
		sb.append("<a href=\"" + result.getCacheUrl() + "\" style=\"color:#488048\">");
		sb.append("Cached");
		sb.append("</a>");
		sb.append("</p>");
		return sb.toString();
	}
}
