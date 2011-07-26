WSDOT-Search-GWT
================

The search page makes use of USASearch's [Search Data API](http://search.usa.gov/api) to return data allowing 
us to control the look and feel of our results page. Data includes 
sections for search results, summary stats, boosted sites, spelling 
suggestions, and related searches, among other data.

This browser-based application is built using [Google Web Toolkit (GWT)](http://code.google.com/webtoolkit/) and 
makes use of the [JavaScript Native Interface (JSNI)](http://code.google.com/webtoolkit/doc/latest/DevGuideCodingBasicsJSNI.html) and [JavaScript overlay types](http://code.google.com/webtoolkit/doc/latest/DevGuideCodingBasicsOverlay.html) 
features.

###Setup

The `war/Search.html` and `war/Search.css` files form the base for 
implementing our standard Web template look and feel. These files 
will need to substituted with your own template and CSS files. 

The `gov.wa.wsdot.search.client.SearchWidget.java` file needs to have 
the following Strings set with your Flickr and USASearch information:

``` java
private static final String FLICKR_API_KEY = "INSERT_YOUR_FLICKR_API_KEY_HERE";
private static final String FLICKR_USER_ID = "INSERT_YOUR_FLICKR_USER_ID_HERE";
private static final String SEARCH_USA_AFFILIATE = "INSERT_YOUR_SEARCH_USA_AFFILIATE_HERE";
private static final String SEARCH_USA_API_KEY = "INSERT_YOUR_SEARCH_USA_API_KEY_HERE";
private static final String SEARCH_USA_AFFILIATE_ID = "INSERT_YOUR_SEARCH_USA_AFFILIATE_ID_HERE";
```

The `gov.wa.wsdot.search.client.Popup.java` file needs to have this 
String set with your Flickr username:

``` java
private static final String FLICKR_USERNAME = "INSERT_YOUR_FLICKR_USERNAME_HERE";
```

###Features

####Noscript

If user has JavaScript disabled or is accessing the page from a screen 
reader the page will still be functional. Search results will be shown 
from [Search.USA.gov](http://search.usa.gov/) rather than via Ajax on the WSDOT site.

####Analytics - Event Tracking

If you wish to enable event tracking on the search terms and photos clicked 
on you will need to add your Google Analytics UA code to the follwing 
JavaScript block located in the head of the `war/Search.html` template.

``` javascript
<script type="text/javascript"> 
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'INSERT_YOUR_ANALYTICS_UA_CODE_HERE']);
  _gaq.push(['_trackPageview']);
  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
```

You will also need to set the ANALYTICS_ENABLED constant to `true` in the 
`gov.wa.wsdot.search.client.SearchWidget.java` file.

``` java
private static final boolean ANALYTICS_ENABLED = false;
```

####Type ahead search results

Type ahead search results are done using a SuggestBox. A SuggestBox 
is a text box or text area which displays a pre-configured set of 
selections that match the user's input. Each SuggestBox is associated 
with a single SuggestOracle. The SuggestOracle is used to provide a 
set of selections given a specific query string.

####Photos

Photos shown are based on data returned from the [Flickr Photos Search API](http://www.flickr.com/services/api/flickr.photos.search.html). 
Results are shown using a DisclosurePanel widget beneath the search box. 
Photo content can be shown or hidden by the user clicking on the "Photo 
results" header.

####Travel alerts

"Highest" and "High" impact travel alerts based on search 
results. Travel alert results are displayed in a DisclosurePanel 
on the right side of the search page. The "View Location" link uses 
the [Google Static Maps API](http://code.google.com/apis/maps/documentation/staticmaps/) to show the location based on the 
latitude and longitude of the alert.

###Contributing

Find a bug? Got an idea? Send us an email or a patch (that would be cool) 
and we'll take a look at it.
