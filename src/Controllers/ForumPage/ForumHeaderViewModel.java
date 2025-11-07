package Controllers.ForumPage;

import javafx.beans.property.SimpleBooleanProperty;
import Models.User;
import Sessions.UserSession;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ForumHeaderViewModel {
	
	private final UserSession userSession;
	
	private final StringProperty searchFieldTextProperty = new SimpleStringProperty();
	private final BooleanProperty searchButtonPressedProperty = new SimpleBooleanProperty();
	
	public ForumHeaderViewModel(UserSession userSession) {
		this.userSession = userSession;
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
	
	public boolean isReviewer() {
		return userSession.getCurrentUserRole() == User.Role.reviewer;
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