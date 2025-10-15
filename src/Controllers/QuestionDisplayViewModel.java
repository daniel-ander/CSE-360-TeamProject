package Controllers;


import java.util.List;

import Models.Answer;
import Models.Question;

public class QuestionDisplayViewModel {
    private Question question;
    private List<String> permissionList; // fetched from a service in the controller
    private List<Answer> answers;

    public QuestionDisplayViewModel(Question question, List<String> permissionList, List<Answer> answers) {
        this.question = question;
        this.permissionList = permissionList;
        this.answers = answers;
    }

    public Question getQuestion() { return question; }
    public List<String> getPermissionList() { return permissionList; }
    public List<Answer> getAnswersForQuestion() { return answers; }
}