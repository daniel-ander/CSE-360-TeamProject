package Config;

import java.util.List;

import DisplayModels.PostWithPermissions;
import DisplayModels.QuestionWithAnswersAndPermissions;
import Models.Question;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppCache {
    private static final AppCache instance = new AppCache();

    private final ObjectProperty<List<PostWithPermissions>> initialQuestionData = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<QuestionWithAnswersAndPermissions>> initialData = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<QuestionWithAnswersAndPermissions>> fullData = new SimpleObjectProperty<>(null);

    private AppCache() {}

    public static AppCache getInstance() {
        return instance;
    }
    
    public void setInitialQuestionsData(List<PostWithPermissions> questions) {
    	this.initialQuestionData.set(questions);
    }

    public void setInitialDataProperty(List<QuestionWithAnswersAndPermissions> initialData) {
        this.initialData.set(initialData);
    }

    public void setFullData(List<QuestionWithAnswersAndPermissions> fullData) {
        this.fullData.set(fullData);
    }
    
    public ObjectProperty<List<QuestionWithAnswersAndPermissions>> getInitialDataProperty() {
    	return initialData;
    }
    
    public ObjectProperty<List<QuestionWithAnswersAndPermissions>> getFullDataProperty() {
    	return fullData;
    }
    
    public ObjectProperty<List<PostWithPermissions>> getInitialQuestionData() {
    	return initialQuestionData;
    }
    
    public boolean isInitialDataLoaded() {
    	return initialData.get() != null;
    }

    public boolean isFullyLoaded() {
        return fullData.get() != null;
    }
}
