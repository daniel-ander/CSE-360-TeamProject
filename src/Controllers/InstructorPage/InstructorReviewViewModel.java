package Controllers.InstructorPage;

import Database.Services.Interfaces.IUserService; 
import Database.Services.Interfaces.IQuestionService; 
import Database.Services.Interfaces.IAnswerService; 
import Database.Services.Interfaces.IMessageService; 
import Models.Answer; 
import Models.Message; 
import Models.Question; 
import Models.ReviewerRequest; 
import javafx.beans.property.ObjectProperty; 
import javafx.beans.property.SimpleObjectProperty; 
import javafx.collections.FXCollections; 
import javafx.collections.ObservableList;

import java.time.Instant; import java.util.HashMap; import java.util.List; import java.util.Map; import java.util.stream.Collectors;

public class InstructorReviewViewModel {

private final IUserService userService;
private final IQuestionService questionService;
// private final IRequestService requestService;
private final IAnswerService answerService;
private final IMessageService messageService;

private final ObservableList<String> users = FXCollections.observableArrayList();
private final ObservableList<Question> questions = FXCollections.observableArrayList();
private final ObservableList<Answer> answers = FXCollections.observableArrayList();
private final ObservableList<ReviewerRequest> reviews = FXCollections.observableArrayList();
private final ObservableList<Message> messages = FXCollections.observableArrayList();

// single ObservableList instance bound to the UI; contents are swapped on user selection
private final ObservableList<String> staffNotes = FXCollections.observableArrayList();

// per-user in-memory note storage
private final Map<String, ObservableList<String>> notesByUser = new HashMap<>();

// store selected username (UI provides usernames)
private final ObjectProperty<String> selectedUserProperty = new SimpleObjectProperty<>(null);

private Object currentContent = null; // for tracking current content displayed

public InstructorReviewViewModel(
    IUserService userService,
    IQuestionService questionService,
    /*IRequestService requestService,*/
    IAnswerService answerService,
    IMessageService messageService) {
    this.userService = userService;
    this.questionService = questionService;
    // this.requestService = requestService;
    this.answerService = answerService;
    this.messageService = messageService;

    // start with an empty in-memory notes list
    staffNotes.clear();

    loadAllUsers();

    // keep selection property in sync if needed elsewhere
    selectedUserProperty.addListener((obs, oldUser, newUser) -> {
        // when selection changes externally, ensure UI-facing staffNotes reflect the new user
        // (setSelectedUser already handles saving/loading; this just ensures consistency)
        if (newUser != null) {
            ObservableList<String> list = notesByUser.computeIfAbsent(newUser, k -> FXCollections.observableArrayList());
            staffNotes.setAll(list);
        } else {
            staffNotes.clear();
        }
    });
}

public ObservableList<String> getUsers() {
    return users;
}

public ObservableList<Question> getQuestions() {
    return questions;
}

public ObservableList<Answer> getAnswers() {
    return answers;
}

public ObservableList<ReviewerRequest> getReviews() {
    return reviews;
}

public ObservableList<Message> getMessages() {
    return messages;
}

public ObservableList<String> getStaffNotes() {
    return staffNotes;
}

/*
 * Load all usernames into the users list
 */
public void loadAllUsers() {
    users.setAll(userService.getAllUsernames());
    // initialize per-user note lists so switching is immediate (optional)
    for (String u : users) {
        notesByUser.putIfAbsent(u, FXCollections.observableArrayList());
    }
}

/*
 * Set the selected user by username.
 */
public void setSelectedUser(String selectedUsername) {
    // save current staffNotes into map for previous user
    String previous = selectedUserProperty.get();
    if (previous != null) {
        // replace previous user's list contents with current contents
        ObservableList<String> prevList = notesByUser.computeIfAbsent(previous, k -> FXCollections.observableArrayList());
        prevList.setAll(staffNotes);
    }

    selectedUserProperty.set(selectedUsername);

    // load notes for new user into the UI-bound list (mutate staffNotes)
    if (selectedUsername == null) {
        staffNotes.clear();
    } else {
        ObservableList<String> userNotes = notesByUser.computeIfAbsent(selectedUsername, k -> FXCollections.observableArrayList());
        staffNotes.setAll(userNotes);
    }

    loadUserData(selectedUsername);
}

/*
 * Load all content (questions, answers, reviews, messages) for the given username.
 */
private void loadUserData(String username) {
    // Clear previous data
    questions.clear();
    answers.clear();
    reviews.clear();
    messages.clear();

    if (username == null) return;

    // Fetch all questions and filter by author username
    try {
        List<Question> allQuestions = questionService.getAllQuestions();
        if (allQuestions != null) {
            List<Question> filtered = allQuestions.stream()
                .filter(q -> safeString(userService.getUserNameById(q.getAuthor().getId())).equals(username))
                .collect(Collectors.toList());
            questions.setAll(filtered);
        }
    } catch (Throwable ex) {
        // leave questions empty if service fails
    }

    // Fetch all answers and filter by author username
    try {
        List<Answer> allAnswers = answerService.getAllAnswers();
        if (allAnswers != null) {
            List<Answer> filtered = allAnswers.stream()
                .filter(a -> safeString(a.getAuthor().getUserName()).equals(username))
                .collect(Collectors.toList());
            answers.setAll(filtered);
        }
    } catch (Throwable ex) {
        // leave answers empty if service fails
    }

    // Messages: fetch and filter by sender/receiver if necessary
    try {
        // prefer a username-based API if messageService provides one, otherwise fallback
        messages.setAll(messageService.getMessagesForUsername(username));
    } catch (Throwable ignored) {
        try {
            List<Message> allMessages = messageService.getAllMessages();
            if (allMessages != null) {
                List<Message> filtered = allMessages.stream()
                    .filter(m -> safeString(m.getSenderUsername()).equals(username)
                        || safeString(m.getReceiverUsername()).equals(username))
                    .collect(Collectors.toList());
                messages.setAll(filtered);
            }
        } catch (Throwable ignore2) {
            // leave messages empty
        }
    }

    // Reviews: left as TODO since request service isn't wired in this VM
}

/*
 * Safe string helper to avoid nulls
 */
private static String safeString(String s) {
    return s == null ? "" : s;
}

/* 
 * Track the currently displayed content for flagging purposes
*/
public void setCurrentlyDisplayedContent(Object content) {
    this.currentContent = content;
}

/* 
 * Add a staff note for the current selection
*/
public void addStaffNote(String noteText) {
    if (noteText == null) return;
    String trimmed = noteText.trim();
    if (trimmed.isBlank()) return;

    String timestamped = Instant.now().toString() + " | " + trimmed;

    String currentUser = selectedUserProperty.get();
    if (currentUser == null) {
        // no user selected, add to a global 'unassigned' bucket
        ObservableList<String> global = notesByUser.computeIfAbsent("", k -> FXCollections.observableArrayList());
        global.add(timestamped);
    } else {
        ObservableList<String> userList = notesByUser.computeIfAbsent(currentUser, k -> FXCollections.observableArrayList());
        userList.add(timestamped);
    }

    // update UI list
    staffNotes.add(timestamped);
}

/* 
 * Remove a staff note at the given index for the current selection
*/
public void removeStaffNoteAt(int index) {
    if (index < 0 || index >= staffNotes.size()) return;

    String removed = staffNotes.remove(index);

    String currentUser = selectedUserProperty.get();
    if (currentUser == null) {
        ObservableList<String> global = notesByUser.get("");
        if (global != null) global.remove(removed);
    } else {
        ObservableList<String> userList = notesByUser.get(currentUser);
        if (userList != null) userList.remove(removed);
    }
}

/* 
 * Clear all staff notes for the current selection
*/
public void clearStaffNotes() {
    String currentUser = selectedUserProperty.get();
    if (currentUser == null) {
        ObservableList<String> global = notesByUser.get("");
        if (global != null) global.clear();
    } else {
        ObservableList<String> userList = notesByUser.get(currentUser);
        if (userList != null) userList.clear();
    }
    staffNotes.clear();
}

/* 
 * Flag the currently displayed content (question, answer, or message)
*/
public void flagCurrentContent() {
    if (currentContent == null) return;

    if (currentContent instanceof Question) {
        Question q = (Question) currentContent;
        // questionService.flagQuestion(q.getId());
    } else if (currentContent instanceof Answer) {
        Answer a = (Answer) currentContent;
        // answerService.flagAnswer(a.getId());
    } else if (currentContent instanceof Message) {
        Message m = (Message) currentContent;
        // messageService.flagMessage(m.getId());
    }
}

}