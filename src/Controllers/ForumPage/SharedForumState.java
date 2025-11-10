package Controllers.ForumPage;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SharedForumState {
    private final BooleanProperty isFavoredReviewerProperty = new SimpleBooleanProperty(false);

    public BooleanProperty getIsFavoredReviewerProperty() {
        return isFavoredReviewerProperty;
    }
    
    public void setIsFavoredReviewerProperty(boolean isFavored) {
        isFavoredReviewerProperty.set(isFavored);
    }
}
