package Controllers.ForumPage;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ForumHeaderViewModel {
	
	private StringProperty searchFieldTextProperty = new SimpleStringProperty();
	private BooleanProperty searchButtonPressedProperty = new SimpleBooleanProperty();
	
	public ForumHeaderViewModel() {
		
	}
	
	// ===================
    // 		 GETTERS
 	// ===================
	
	public StringProperty getSearchFieldTextProperty() {
		return searchFieldTextProperty;
	}
	
	public BooleanProperty getSearchButtonPressedProperty() {
		return searchButtonPressedProperty;
	}
	
	public String getSearchFieldText() {
		return searchFieldTextProperty.get();
	}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setSearchFiledTextProperty(String text) {
    	this.searchFieldTextProperty.set(text);
    }
	
	public void triggerPostsFilter() {
		searchButtonPressedProperty.set(true);
		searchButtonPressedProperty.set(false); // reset for future trigger
	}
	
}