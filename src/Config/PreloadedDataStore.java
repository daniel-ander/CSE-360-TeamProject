package Config;

import java.util.List;
import java.util.Map;

import Models.Answer;
import Models.Question;

public class PreloadedDataStore {
    private final List<Question> questions;
    private final Map<Integer, List<Answer>> answers;
    private final Map<Integer, List<String>> permissions;

    public PreloadedDataStore(List<Question> questions,
                            Map<Integer, List<Answer>> answers,
                            Map<Integer, List<String>> permissions) {
        this.questions = questions;
        this.answers = answers;
        this.permissions = permissions;
    }
    
    public List<Question> getInitialQuestions() {
    	return questions;
    }

}