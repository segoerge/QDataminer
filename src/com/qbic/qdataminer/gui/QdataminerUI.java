package com.qbic.qdataminer.gui;

import javax.servlet.annotation.WebServlet;

import com.qbic.qdataminer.backend.Variant;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.*;

import java.util.*;

import org.json.*;

@SuppressWarnings("serial")
@Theme("qdataminer")
@Title("Qdataminer")
public class QdataminerUI extends UI {
	
	private VariantLayout lastVariantLayout = null;
	private VariantLayout resultVariantLayout;

	@Override
	protected void init(VaadinRequest request) {
			
		final VerticalLayout mainLayout = new VerticalLayout();
		Label globalHeading = new Label("QVM - The QBiC Variant Miner");
		globalHeading.setStyleName("customh1");
		mainLayout.addComponent(globalHeading);
		
		setContent(mainLayout);
		mainLayout.setStyleName("blue");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		/*
		 * Layout with search fields
		 */
		
		final HorizontalLayout searchLayout = new HorizontalLayout();
		searchLayout.setStyleName("layoutborder");
		searchLayout.setMargin(true);
		searchLayout.setWidth("100%");
		SearchForm varQueryForm = new SearchForm();
		
		searchLayout.addComponent(varQueryForm);
		searchLayout.addComponent(varQueryForm.getSearchResultTable());
		mainLayout.addComponent(searchLayout);
		mainLayout.setExpandRatio(globalHeading, 1);
		mainLayout.setExpandRatio(searchLayout, 20);
		
		/*
		 * Listener
		 */
		varQueryForm.getSearchResultTable().addSelectionListener(selectionEvent -> {
			/*
			 * Show the selected variant in detail
			 */
			
			// This disables the null pointer exception on double-selecting
		    Set<Object> selected = selectionEvent.getSelected();
		    if (selected == null || selected.isEmpty()) {
		        Set<Object> removed = selectionEvent.getRemoved();
		        removed.stream().filter(Objects::nonNull).forEach(varQueryForm.getSearchResultTable()::select);
		    }
		    
		    if (lastVariantLayout != null){
		    	mainLayout.removeComponent(lastVariantLayout);
		    }
			
			Variant selectedVariant = (Variant)(((SingleSelectionModel) varQueryForm.getSearchResultTable().getSelectionModel()).getSelectedRow());
			
			resultVariantLayout = null;
			
			for(Iterator<JSONObject> res = varQueryForm.getJsonResults().iterator(); res.hasNext();){
				JSONObject curJson = res.next();
				if (curJson.getString("_id").equals(selectedVariant.getVariantID())){
					resultVariantLayout = new VariantLayout(curJson);
				}
			}

			resultVariantLayout.setVisible(true);
			resultVariantLayout.setStyleName("layoutborder");
			mainLayout.addComponent(resultVariantLayout);
			mainLayout.addComponent(new Label(""));
			mainLayout.setExpandRatio(globalHeading, 1);
			mainLayout.setExpandRatio(searchLayout, 7);
			mainLayout.setExpandRatio(resultVariantLayout, 10);
			lastVariantLayout = resultVariantLayout;
	
		});
	
	}
	
    public void clearOnEmptySearch() {
    	if (resultVariantLayout != null){
    		resultVariantLayout.setVisible(false);    		
    	}
    }
	
    //  Deployed as a Servlet or Portlet.
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = QdataminerUI.class)
	public static class Servlet extends VaadinServlet {
	}

}