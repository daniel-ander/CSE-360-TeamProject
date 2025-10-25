package Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Controllers.PostDisplayViewModel;
import DisplayModels.PostWithPermissions;
import DisplayModels.QuestionWithAnswersAndPermissions;
import Models.Question;
import Utils.KeyPairHolder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppCache {
    private static final AppCache instance = new AppCache();

    private final ObjectProperty<List<PostWithPermissions>> initialQuestionDataProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<PostWithPermissions>> answerDataProperty = new SimpleObjectProperty<>(null);
    
    private final ObjectProperty<List<PostWithPermissions>> allInitialAnswersDataProperty = new SimpleObjectProperty<>(null);
    
    private final ObjectProperty<List<QuestionWithAnswersAndPermissions>> initialDataProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<QuestionWithAnswersAndPermissions>> fullDataProperty = new SimpleObjectProperty<>(null);
    
    private AppCache() {}

    public static AppCache getInstance() {
        return instance;
    }
    
    public void setInitialQuestionsData(List<PostWithPermissions> questions) {
    	this.initialQuestionDataProperty.set(questions);
    }
    
    // Answers for every question - in order
    public void setAnswersData(List<PostWithPermissions> answers) {
    	this.answerDataProperty.set(answers);
    	this.allInitialAnswersDataProperty.get().addAll(answers);
    }

    public void setInitialDataProperty(List<QuestionWithAnswersAndPermissions> initialData) {
        this.initialDataProperty.set(initialData);
    }

    public void setFullData(List<QuestionWithAnswersAndPermissions> fullData) {
        this.fullDataProperty.set(fullData);
    }
    
    public ObjectProperty<List<QuestionWithAnswersAndPermissions>> getInitialDataProperty() {
    	return initialDataProperty;
    }
    
    public ObjectProperty<List<PostWithPermissions>> getAllInitialAnswersDataProperty() {
    	return allInitialAnswersDataProperty;
    }
    
    public ObjectProperty<List<PostWithPermissions>> getAnswersDataProperty() {
    	return answerDataProperty;
    }
    
    public ObjectProperty<List<QuestionWithAnswersAndPermissions>> getFullDataProperty() {
    	return fullDataProperty;
    }
    
    public ObjectProperty<List<PostWithPermissions>> getInitialQuestionData() {
    	return initialQuestionDataProperty;
    }
    
    public boolean areInitialAnswersLoaded() {
    	return allInitialAnswersDataProperty.get() != null;
    }
    
    public boolean areInitialQuestionsLoaded() {
    	return initialQuestionDataProperty.get() != null;
    }
    
    public boolean isInitialDataLoaded() {
    	return initialDataProperty.get() != null;
    }

    public boolean isFullyLoaded() {
        return fullDataProperty.get() != null;
    }
}
