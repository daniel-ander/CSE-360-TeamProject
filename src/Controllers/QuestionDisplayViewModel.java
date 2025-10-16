package Controllers;


import java.util.List;
import java.util.function.Consumer;

import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import Models.Answer;
import Models.Question;
import Sessions.UserSession;

public class QuestionDisplayViewModel {
    private Question question;
    private List<String> permissionList; // fetched from a service in the controller
    private List<Answer> answers;
    private List<List<String>> answerUserPermissions;
    private IQuestionService questionService;
    private IAnswerService answerService;
    private QuestionListViewController controller;
    private final UserSession userSession;
    private boolean isEditingQuestion = false;
    private boolean isCommenting = false;
    
    private final Consumer<Integer> deleteAnswerCallBack;
    Consumer<Answer> addAnswerCallBack;
    private final Runnable refreshEntries;
    

    public QuestionDisplayViewModel(
    		Question question, 
    		List<String> permissionList, 
    		List<Answer> answers,  
    		List<List<String>> answerUserPermissions, 
    		UserSession userSession,
    		IQuestionService questionService,
    		IAnswerService answerService,
    		QuestionListViewController controller,
    		Consumer<Integer> deleteCallBack,
    		Consumer<Answer> addAnswerCallBack,
    		Runnable refreshEntries
    	) {
        this.question = question;
        this.permissionList = permissionList;
        this.answers = answers;
        this.answerUserPermissions = answerUserPermissions;
        this.questionService = questionService;
        this.answerService = answerService;
        this.userSession = userSession;
        this.controller = controller;
        this.deleteAnswerCallBack = deleteCallBack;
        this.addAnswerCallBack = addAnswerCallBack;
        this.refreshEntries = refreshEntries;
    }

    public Question getQuestion() { return question; }
    public List<String> getPermissionList() { return permissionList; }
    public List<Answer> getAnswersForQuestion() { return answers; }
    public List<List<String>> getAnswerUserPermissions() { return answerUserPermissions; }
    public boolean getIsEditingQuestion() {
    	return isEditingQuestion;
    }
    
    public boolean getIsCommenting() {
    	return isCommenting;
    }
    
    public void setIsEditingQuestion(boolean isEditing) {
    	this.isEditingQuestion = isEditing;
    }
    
    public void setIsCommenting(boolean isCommenting) {
    	this.isCommenting = isCommenting;
    }
    
    public void deleteQuestion() {
    	deleteAnswerCallBack.accept(question.getId());
    	
    	refreshEntries.run();
    }
    
    public void updateQuestion(String newTitle, String newBody) {
    	questionService.updateQuestion(question.getId(), newTitle, newBody);
    	
    	controller.refresh();
    }
    
    public void addAnswer(String newText) {
    	Answer newAnswer = new Answer(question.getId(), question.getAuthorId(), userSession.getCurrentUserName(), newText);
    	addAnswerCallBack.accept(newAnswer);
    	
    	refreshEntries.run();
    }
}