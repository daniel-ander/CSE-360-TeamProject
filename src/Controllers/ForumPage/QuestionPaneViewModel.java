package Controllers.ForumPage;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QuestionPaneViewModel {
	
	private StringProperty textInputProperty = new SimpleStringProperty();
	private BooleanProperty textInputSubmittedProperty = new SimpleBooleanProperty();
	
	private String titleText = "";
	private String bodyText = "";
	
	public QuestionPaneViewModel() {
		
	}
	
	// ===================
    // 		 GETTERS
 	// ===================
	
	public String getTitleText() {
		return titleText;
	}
	
	public String getBodyText() {
		return bodyText;
	}
	
	public StringProperty getTextInputProperty() {
		return textInputProperty;
	}
	
	public BooleanProperty getTextInputSubmittedProperty() {
		return textInputSubmittedProperty;
	}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}
	
	public void setTextInputProperty(String text) {
    	this.textInputProperty.set(text);
    }
	
	public void setTextInputSubmittedProperty(boolean textWasSubmitted) {
    	this.textInputSubmittedProperty.set(textWasSubmitted);
    	this.textInputSubmittedProperty.set(false);
    }
	
	public void submitNewQuestion(String titleText, String bodyText) {
		this.titleText = titleText;
		this.bodyText = bodyText;
		setTextInputSubmittedProperty(true);
	}
	
}