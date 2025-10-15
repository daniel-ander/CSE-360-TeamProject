package Controllers;


import java.util.List;

import Database.Services.Interfaces.IQuestionService;
import Models.Answer;
import Models.Question;

public class QuestionDisplayViewModel {
    private Question question;
    private List<String> permissionList; // fetched from a service in the controller
    private List<Answer> answers;
    private List<List<String>> answerUserPermissions;
    private IQuestionService questionService;
    private QuestionListViewController controller;
    

    public QuestionDisplayViewModel(
    		Question question, 
    		List<String> permissionList, 
    		List<Answer> answers,  
    		List<List<String>> answerUserPermissions, 
    		IQuestionService questionService,
    		QuestionListViewController controller
    	) {
        this.question = question;
        this.permissionList = permissionList;
        this.answers = answers;
        this.answerUserPermissions = answerUserPermissions;
        this.questionService = questionService;
        this.controller = controller;
    }

    public Question getQuestion() { return question; }
    public List<String> getPermissionList() { return permissionList; }
    public List<Answer> getAnswersForQuestion() { return answers; }
    public  List<List<String>> getAnswerUserPermissions() { return answerUserPermissions; }
    
    public void deleteQuestion() {
    	questionService.deleteQuestion(question.getId());
    	
    	controller.refresh();
    }
    
    public void updateQuestion() {
    	questionService.updateQuestion(question.getId(), "Updated Value");
    	
    	controller.refresh();
    }
}