package DisplayModels;

import java.util.List;

import Models.Question;

public class QuestionWithAnswersAndPermissions {
    private final Question question;
    private final List<String> permissions;
    private final List<PostWithPermissions> answers;

    public QuestionWithAnswersAndPermissions(Question question, List<String> permissions, List<PostWithPermissions> answers) {
        this.question = question;
        this.permissions = permissions;
        this.answers = answers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<PostWithPermissions> getAnswersWithPermissions() {
        return answers;
    }
}
