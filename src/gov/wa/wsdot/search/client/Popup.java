/*
 * Copyright (c) 2011 Washington State Department of Transportation
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Popup extends PopupPanel {

    interface Binder extends UiBinder<Widget, Popup> { } 
    private static final Binder binder = GWT.create(Binder.class);
    private static final String FLICKR_USERNAME = "INSERT_YOUR_FLICKR_USERNAME_HERE";
    
    @UiField HTMLPanel photo;
    
    public Popup(String url, String title, int id) {
        // The popup's constructor's argument is a boolean specifying that it 
        // auto-close itself when the user clicks outside of it. 
        super(true);
        add(binder.createAndBindUi(this));

        final Image image = new Image(url);
        image.addLoadHandler(new LoadHandler() {
      	    @Override
      	    public void onLoad(LoadEvent event) {
      	        // Since the image has been loaded, the dimensions are known
      	    	photo.setWidth(Integer.toString(image.getWidth()) + "px");
      	        center(); 
      	        setVisible(true);
      	    }
      	 });
        
        photo.add(image);
        photo.add(new HTML("<div style=\"float:left;width:80%;\"><span style=\"padding:5px;font-size:1.2em;display:block;\">" + title + "" +
        		" <a href=\"http://www.wsdot.wa.gov/traffic/trafficalerts/default.aspx?refnum="+ id +"&action=2\">View full alert</a></span></div>"));

        Image close = new Image("images/lightbox-btn-close.gif");
        close.setStyleName("btn-close");
        close.addClickHandler(new ClickHandler() {
  		@Override
  		public void onClick(ClickEvent event) {
  			hide();			
  		}});
        
        photo.add(close);
      }
    
    public Popup(String url, String title, String id) {
      // The popup's constructor's argument is a boolean specifying that it 
      // auto-close itself when the user clicks outside of it. 
      super(true);
      add(binder.createAndBindUi(this));

      final Image image = new Image(url);
      image.addLoadHandler(new LoadHandler() {
    	    @Override
    	    public void onLoad(LoadEvent event) {
    	        // Since the image has been loaded, the dimensions are known
    	    	photo.setWidth(Integer.toString(image.getWidth()) + "px");
    	        center(); 
    	        setVisible(true);
    	    }
    	 });
      
      photo.add(image);
      photo.add(new HTML("<div style=\"float:left;width:70%;\"><span style=\"padding:5px;font-size:1.2em;display:block;\">" + title + "</span>" +
      		"<span style=\"padding:5px;font-size:1.2em;display:block;\"><a href=\"http://www.flickr.com/photos/"+ FLICKR_USERNAME +"/" + id + "/\" style=\"color:#036\">View photo on Flickr</a></span>" +
      		"</div>"));

      Image close = new Image("images/lightbox-btn-close.gif");
      close.setStyleName("btn-close");
      close.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			hide();			
		}});
      
      photo.add(close);
    }
}
