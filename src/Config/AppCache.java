package Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import DisplayModels.PostWithPermissions;
import DisplayModels.QuestionWithAnswersAndPermissions;
import Models.Answer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppCache {
    private static final AppCache instance = new AppCache();

    private final ObjectProperty<List<PostWithPermissions>> initialQuestionDataProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<PostWithPermissions>> answerDataProperty = new SimpleObjectProperty<>(null);
    
    private final ObjectProperty<List<PostWithPermissions>> allInitialAnswersDataProperty = new SimpleObjectProperty<>(null);
    
    private final ObjectProperty<List<QuestionWithAnswersAndPermissions>> initialDataProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<QuestionWithAnswersAndPermissions>> fullDataProperty = new SimpleObjectProperty<>(null);
    
    private final Map<Integer, BigDecimal> ratings = new ConcurrentHashMap<>();
    
    private AppCache() {}

    public static AppCache getInstance() {
        return instance;
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setInitialQuestionsData(List<PostWithPermissions> questions) {
    	this.initialQuestionDataProperty.set(questions);
    }
    
    // Answers for every question - in order
    public void setAnswersDataProperty(List<PostWithPermissions> answers) {
        this.answerDataProperty.set(answers);

        var current = allInitialAnswersDataProperty.get();
        if (current == null) {
            this.allInitialAnswersDataProperty.set(List.copyOf(answers));
        } else {
            // merge safely by creating a new combined list
            var merged = new java.util.ArrayList<>(current);
            merged.addAll(answers);
            this.allInitialAnswersDataProperty.set(List.copyOf(merged));
        }
    }

    public void setInitialDataProperty(List<QuestionWithAnswersAndPermissions> initialData) {
        this.initialDataProperty.set(initialData);
    }

    public void setFullData(List<QuestionWithAnswersAndPermissions> fullData) {
        this.fullDataProperty.set(fullData);
    }
    
    public void setRatingForAnswer(int answerId, BigDecimal rating) {
//    	ratings.put(answerId, rating);
    }
    
    // ===================
    // 		 GETTERS 
 	// ===================
    
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
    
    public ObjectProperty<List<PostWithPermissions>> getInitialQuestionDataProperty() {
    	return initialQuestionDataProperty;
    }
    
    public List<Answer> getAnswerList() {
    	List<Answer> answerHolder = new ArrayList<Answer>();
    	
    	answerDataProperty.get().forEach(postWithPermissions -> {
    		answerHolder.add(isolateAnswer(postWithPermissions));
    	});
    	
    	return answerHolder;
    }
    
    public BigDecimal getRatingForAnswer(int answerId) {
    	return ratings.get(answerId);
    }
    
    // ===================
    // 		 HELPERS 
 	// ===================
    
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
    
    public void clearAllCache() {
    	 initialQuestionDataProperty.set(null);
    	 answerDataProperty.set(null);
    	    
    	 allInitialAnswersDataProperty.set(null);
    	    
    	 initialDataProperty.set(null);
    	 fullDataProperty.set(null);
    }
    
    private Answer isolateAnswer(PostWithPermissions postWithPermissions) throws ClassCastException {
    	return (Answer)postWithPermissions.getPost();
    }
}
