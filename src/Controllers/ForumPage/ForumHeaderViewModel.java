package Controllers.ForumPage;

import javafx.beans.property.SimpleBooleanProperty;
import Sessions.UserSession;
import Models.User;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ForumHeaderViewModel {
	
	private StringProperty searchFieldTextProperty = new SimpleStringProperty();
	
	private BooleanProperty searchButtonPressedProperty = new SimpleBooleanProperty();
	private BooleanProperty seeReviewsButtonPressedProperty = new SimpleBooleanProperty();
	
	private final UserSession userSession;
	
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
	
	public BooleanProperty getSeeReviewsButtonPressedProperty() {
		return seeReviewsButtonPressedProperty;
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
	
	public void setSeeReviewsButtonPressedProperty(boolean isPressed) {
    	this.seeReviewsButtonPressedProperty.set(isPressed);
    }
	
	public void triggerPostsFilter() {
		searchButtonPressedProperty.set(true);
		searchButtonPressedProperty.set(false); // reset for future trigger
	}
	
}