package Controllers;


import java.util.List;

import Models.Question;

public class QuestionDisplayViewModel {
    private Question question;
    public List<String> permissionList; // fetched from a service in the controller

    public QuestionDisplayViewModel(Question question, List<String> permissionList) {
        this.question = question;
        this.permissionList = permissionList;
    }

    public Question getQuestion() { return question; }
    public List<String> getPermissionList() { return permissionList; }
}