package Controllers;

import Database.DBConnectionStore;
import Database.Services.RequestService;
import Database.Services.Interfaces.IQuestionService;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IUserService;

import Models.Question;
import Models.Answer;

import Sessions.UserSession;
import UI.Pages.InstructorRequestsPage;
import UI.Pages.InstructorHomePage;

import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.stream.Collectors;

// instructor requests page controller
public class InstructorRequestsPageController {
    private final DBConnectionStore store;
    private final Stage primaryStage;
    private final UserSession userSession;
    private final InstructorRequestsPage view;

    private final RequestService requestService;
    private final IQuestionService questionService;
    private final IAnswerService answerService;
    private final IUserService userService;
    // currently selected student username from the requests list
    private String selectedStudentUserName = null;

    public InstructorRequestsPageController(DBConnectionStore store, Stage primaryStage, UserSession userSession, InstructorRequestsPage view) {
        this.store = store;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        this.view = view;
        this.requestService = new RequestService(store);
        this.questionService = store.getQuestionService();
        this.answerService = store.getAnswerService();
        this.userService = store.getUserService();

        // wire UI
        wireActions();
    }

    public void show() {
        view.setInitialLayout(primaryStage);
        refreshRequests();
    }

    private void wireActions() {
        view.getBackButton().setOnAction(_evt -> {
            // go back to instructor home
            new InstructorHomePageController(store, primaryStage, userSession, new InstructorHomePage());
        });
        //instructor selects a student request + load their performance
        view.getRequestsList().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedStudentUserName = newV;
                loadPerformanceForStudent(newV);
            } else {
                view.setQuestions(List.of());
                view.setAnswers(List.of());
            }
        });
        
        // approve request
        view.getApproveButton().setOnAction(_evt -> {
            String student = view.getRequestsList().getSelectionModel().getSelectedItem();
            if (student == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Grant reviewer role to '" + student + "'?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) { // approve button
                    boolean ok = requestService.grantReviewerRole(student);
                    Alert res = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, ok ? "Reviewer role granted." : "Failed to grant reviewer role.");
                    res.showAndWait();
                    refreshRequests();
                }
            });
        });
        
        // deny request
        view.getDenyButton().setOnAction(_evt -> {
            String student = view.getRequestsList().getSelectionModel().getSelectedItem();
            if (student == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deny reviewer role to '" + student + "'?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) { // deny button
                    boolean ok = requestService.denyReviewerRequest(student);
                    Alert res = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, ok ? "Request denied." : "Failed to deny request.");
                    res.showAndWait();
                    refreshRequests();
                }
            });
        });

        // instructor clicks a post + show full details
        view.getPostsList().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                view.setAnswers(List.of());
                return;
            }

            try { // load post details
                String s = newV.trim();
                if (s.startsWith("[A")) {
                    // answer entry format: [A<id>] ...
                    int ansId = parseAnswerIdPrefix(s);
                    if (ansId > 0) {
                        Answer a = answerService.getAnswerById(ansId);
                        if (a != null) {
                            Question q = questionService.getQuestionById(a.getQuestionId());
                            // prefer the question title + but have body text when title is empty
                            String qTitle = "(unknown question)";
                            if (q != null) {
                                qTitle = q.getTitleText();
                                if (qTitle == null || qTitle.trim().isEmpty() || "Default Title".equals(qTitle)) {
                                    qTitle = q.getBodyText();
                                }
                            }

                            // answer author's username by id
                            String authorName = a.getAuthorName();
                            try {
                                String resolved = userService.getUserNameById(a.getAuthorId());
                                if (resolved != null && !resolved.trim().isEmpty()) authorName = resolved;
                            } catch (Exception ignore) { }
                            // show full details: question title + question body, then answer metadata and body
                            java.util.List<String> details = new java.util.ArrayList<>();
                            details.add(String.format("Question [%d]: %s", a.getQuestionId(), qTitle));
                            // add question body lines indented (no blank line between title and body)
                            if (q != null && q.getBodyText() != null) {
                                java.util.Arrays.stream(q.getBodyText().split("\\r?\\n")).forEach(line -> details.add("    " + line));
                            }
                            // directly add answer metadata (no extra blank line)
                            details.add(String.format("Answer by %s (Author ID: %d)", authorName == null ? "(unknown)" : authorName, a.getAuthorId()));
                            if (a.getBodyText() != null) {
                                java.util.Arrays.stream(a.getBodyText().split("\\r?\\n")).forEach(line -> details.add("    " + line));
                            }
                            view.setAnswers(details);
                        } else {
                            view.setAnswers(List.of("Answer not found."));
                        }
                    }
                } else if (s.startsWith("[Q")) {
                    // question entry: show full question details (title + body)
                    int qid = parseIdPrefix(s);
                    if (qid > 0) {
                        Question q = questionService.getQuestionById(qid);
                        if (q != null) {
                            String qTitle = q.getTitleText();
                            if (qTitle == null || qTitle.trim().isEmpty() || "Default Title".equals(qTitle)) {
                                qTitle = q.getBodyText();
                            }
                            java.util.List<String> details = new java.util.ArrayList<>();
                            details.add(String.format("Question [%d]: %s", q.getId(), qTitle));
                            if (q.getBodyText() != null) {
                                java.util.Arrays.stream(q.getBodyText().split("\\r?\\n")).forEach(line -> details.add("    " + line));
                            }
                            view.setAnswers(details);
                        } else {
                            view.setAnswers(List.of("Question not found."));
                        }
                    }
                } else {
                    view.setAnswers(List.of());
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error loading post details: " + e.getMessage()).showAndWait();
            }
        });

    }

    // load both questions + answers authored by the student into list
    private void loadPerformanceForStudent(String studentUserName) {
        try {
            int userId = userService.getUserIdByName(studentUserName);

            // questions by student user
            List<Question> questions = questionService.getQuestionsFromUser(userId);
            List<String> qStrings = questions.stream()
                    .map(q -> {
                        String displayTitle = q.getTitleText();
                        if (displayTitle == null || displayTitle.trim().isEmpty() || "Default Title".equals(displayTitle)) {
                            displayTitle = q.getBodyText();
                        }
                        // show the question id and title/body
                        return String.format("[Q%d] %s", q.getId(), displayTitle);
                    })
                    .collect(Collectors.toList());

            // answers authored by the student + show the question title they answered
            List<Answer> authored = answerService.getAnswersByUser(userId);
            List<String> aStrings = authored.stream().map(a -> {
                String qTitle = "(unknown question)";
                try {
                    Question q = questionService.getQuestionById(a.getQuestionId());
                    if (q != null) {
                        qTitle = q.getTitleText();
                        if (qTitle == null || qTitle.trim().isEmpty() || "Default Title".equals(qTitle)) qTitle = q.getBodyText();
                    }
                } catch (Exception ignore) { }
                // posts list: show only answer id and the question title — full answer shown in details when selected
                return String.format("[A%d] Answer to Q[%d] \"%s\"", a.getId(), a.getQuestionId(), qTitle);
            }).collect(Collectors.toList());

            // show questions first then answers
            List<String> combined = new java.util.ArrayList<>();
            combined.addAll(qStrings);
            combined.addAll(aStrings);
            view.setQuestions(combined);
            view.setAnswers(List.of()); // clear details
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error loading student performance: " + e.getMessage()).showAndWait();
        }
    }

    // load answers for specific question into the details view
    //mark which answer is by the selected student
    private void loadAnswersForQuestion(int questionId) {
        try {
            List<Answer> answers = answerService.getAnswersByQuestionID(questionId);
            List<String> aStrings = answers.stream().map(a -> {
                String base = String.format("[%d] %s", a.getId(), a.getBodyText());
                String authorName = a.getAuthorName();
                try { String n = userService.getUserNameById(a.getAuthorId()); if (n != null) authorName = n; } catch (Exception ignore) {}
                if (selectedStudentUserName != null && selectedStudentUserName.equalsIgnoreCase(authorName)) {
                    return base + "    (Author: Selected Student)";
                } else {
                    return base + "    (Author: " + authorName + ")";
                }
            }).collect(Collectors.toList());
            view.setAnswers(aStrings);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error loading answers: " + e.getMessage()).showAndWait();
        }
    }

    // answer id from strings that start with 'A<id>'
    private int parseAnswerIdPrefix(String s) {
        try {
            if (!s.startsWith("[A")) return -1;
            int end = s.indexOf(']');
            if (end <= 2) return -1;
            String num = s.substring(2, end);
            return Integer.parseInt(num);
        } catch (NumberFormatException ignore) {
            return -1;
        }
    }
    // generic id from strings that start with '<id>'
    private int parseIdPrefix(String s) {
        try {
            int end = s.indexOf(']');
            if (end <= 1) return -1;
            // support formats: [Q22] or [22]
            if (s.startsWith("[Q") || s.startsWith("[q")) {
                String num = s.substring(2, end);
                return Integer.parseInt(num);
            } else if (s.startsWith("[")) {
                String num = s.substring(1, end);
                return Integer.parseInt(num);
            }
         } catch (NumberFormatException ignore) {}
         return -1;
     }
    // refresh pending requests list
    private void refreshRequests() {
        try {
            // get all pending requests
            List<String> requests = requestService.getPendingRequests();
            view.getRequestsList().getItems().setAll(requests);

            // clear details
            view.setQuestions(List.of());
            view.setAnswers(List.of());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error refreshing requests: " + e.getMessage()).showAndWait();
        }
    }
}
