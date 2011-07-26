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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageLinks extends Composite {

	interface PageLinksUiBinder extends UiBinder<Widget, PageLinks> {}
	private static PageLinksUiBinder uiBinder = GWT.create(PageLinksUiBinder.class);

	@UiField HTMLPanel links;

	private BulletList pagination = new BulletList();
	
	/**
	 * Generate paging links at bottom of page or widget.
	 * 
	 * Refer to this discussion on stackoverflow about algorithms:
	 * 
	 * http://stackoverflow.com/questions/44542/algorithm-pseudo-code-to-create-paging-links
	 * 
	 * @param totalPages
	 * @param currentPage
	 * @param query
	 * @return 
	 */
	public PageLinks(int totalPages, int currentPage, String query) {
		initWidget(uiBinder.createAndBindUi(this));

        // How many pages to display before and after the current page
        int x = 2;
		
		// If we just have one page, show nothing
		if (totalPages <= 1) return;
		
		// If we are not at the first page, show the "Prev" button
		if (currentPage > 1) {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
			item.add(addPaginationResults("Prev", a, query, currentPage));
			pagination.add(item);
		}
		
        // Always display the first page
        if (currentPage == 1) {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults("[" + Integer.toString(1) + "]", a, query, currentPage));
        	pagination.add(item);
        } else {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults("1", a, query, currentPage));
        	pagination.add(item);
        }
		
        // Besides the first and last page, how many pages do we need to display?
        int how_many_times = 2 * x + 1;
        
        // We use the left and right to restrict the range that we need to display
        int left = Math.max(2, currentPage - 2 * x - 1);
        int right = Math.min(totalPages - 1, currentPage + 2 * x + 1);
        
        // The upper range restricted by left and right are more loosely than we need,
        // so we further restrict this range we need to display
        while (right - left > 2 * x) {
            if (currentPage - left < right - currentPage) {
                right--;
                right = right < currentPage ? currentPage : right;
            } else {
                left++;
                left = left > currentPage ? currentPage : left;
            }
        }
        
        // do we need display the left "..."
        if (left >= 3) {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults("...", a, query, currentPage));
        	pagination.add(item);
        }
        
        // Now display the middle pages, we display how_many_times pages from page left
        for (int j = 1, out = left; j <= how_many_times; j++, out++) {
            // there are some pages we need not to display
            if (out > right) {
                continue;
            }

            // display the actual page
            if (out == currentPage) {
				ListItem item = new ListItem();
				Anchor a = new Anchor();
            	item.add(addPaginationResults("[" + Integer.toString(out) + "]", a, query, currentPage));
            	pagination.add(item);
            } else {
				ListItem item = new ListItem();
				Anchor a = new Anchor();
            	item.add(addPaginationResults(Integer.toString(out), a, query, currentPage));
            	pagination.add(item);
            }
        }
        
        // do we need the right "..."
        if (totalPages - right >= 2) {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults("...", a, query, currentPage));
        	pagination.add(item);
        }
        
        // always display the last page
        if (currentPage == totalPages) {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults("[" + Integer.toString(totalPages) + "]", a, query, currentPage));
        	pagination.add(item);
        } else {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults(Integer.toString(totalPages), a, query, currentPage));
        	pagination.add(item);
        }

        // if we are not at the last page, then display the "Next" button
        if (currentPage < totalPages) {
			ListItem item = new ListItem();
			Anchor a = new Anchor();
        	item.add(addPaginationResults("Next", a, query, currentPage));
        	pagination.add(item);
        }
		links.add(pagination);
	}
	
	private Widget addPaginationResults(final String page, Anchor a, final String query, final int currentPage) {	
		a.setText(page);
		if (page == "...") return a;
		a.setHref("javascript:;");
		a.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String pageNo = "";
				if (page == "Prev") {
					pageNo = Integer.toString(currentPage - 1);
				} else if (page == "Next") {
					pageNo = Integer.toString(currentPage + 1);
				} else {
					pageNo = page;
				}
				History.newItem("q=" + query + "&p=" + pageNo);				
			}
		});
		return a;
	}
}
