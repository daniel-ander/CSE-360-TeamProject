package Controllers;


import java.util.List;

import Models.Question;

public class QuestionDisplayViewModel {
    private Question question;
    private List<String> userStatus; // fetched from a service in the controller

    public QuestionDisplayViewModel(Question question, List<String> userStatus) {
        this.question = question;
        this.userStatus = userStatus;
    }

    public Question getQuestion() { return question; }
    public List<String> getUserStatus() { return userStatus; }
}