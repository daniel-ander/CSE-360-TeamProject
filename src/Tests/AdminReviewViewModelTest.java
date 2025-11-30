// language: java
package Tests;

import Controllers.AdminPage.AdminReviewViewModel;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IMessageService;
import Database.Services.Interfaces.IQuestionService;
import Database.Services.Interfaces.IUserService;
import Models.Answer;
import Models.Message;
import Models.Question;
import Models.User;
import Models.DBModels.UserDTO;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminReviewViewModelTest {

    private IUserService userService;
    private IQuestionService questionService;
    private IAnswerService answerService;
    private IMessageService messageService;
    private AdminReviewViewModel vm;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        questionService = mock(IQuestionService.class);
        answerService = mock(IAnswerService.class);
        messageService = mock(IMessageService.class);

        // default user list and empty data; individual tests override as needed
        when(userService.getAllUsernames()).thenReturn(List.of("alice", "bob"));
        when(questionService.getAllQuestions()).thenReturn(List.of());
        when(answerService.getAllAnswers()).thenReturn(List.of());
        when(messageService.getMessagesForUsername(anyString())).thenReturn(List.of());
        when(messageService.getAllMessages()).thenReturn(List.of());

        vm = new AdminReviewViewModel(userService, questionService, answerService, messageService);
    }
    /*
     * Test that loading all users populates the users list correctly.
     */
    @Test
    public void loadAllUsers_populatesUsersList() {
        ObservableList<String> users = vm.getUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains("alice"));
        assertTrue(users.contains("bob"));
    }
    /*
	 * Test that switching between users preserves their individual notes
	 * and updates the visible staff notes list accordingly.
	 */
    @Test
    public void switchingUsers_preservesPerUserNotes_and_updatesStaffNotesList() {
        vm.setSelectedUser("alice");
        vm.addStaffNote("Alice note");
        ObservableList<String> aliceNotes = vm.getStaffNotes();
        assertEquals(1, aliceNotes.size());
        assertTrue(aliceNotes.get(0).contains("Alice note"));
        assertTrue(aliceNotes.get(0).contains(" | "));

        vm.setSelectedUser("bob");
        vm.addStaffNote("Bob note");
        ObservableList<String> bobNotes = vm.getStaffNotes();
        assertEquals(1, bobNotes.size());
        assertTrue(bobNotes.get(0).contains("Bob note"));

        vm.setSelectedUser("alice");
        ObservableList<String> restoredAlice = vm.getStaffNotes();
        assertEquals(1, restoredAlice.size());
        assertTrue(restoredAlice.get(0).contains("Alice note"));
        assertFalse(restoredAlice.get(0).contains("Bob note"));
    }
    /*
	 * Test that removing a staff note affects only the current user's notes.
	 */
    
    @Test
    public void removeStaffNoteAt_removesFromCurrentUserStorage() {
        vm.setSelectedUser("alice");
        vm.addStaffNote("to be removed");
        assertEquals(1, vm.getStaffNotes().size());

        vm.removeStaffNoteAt(0);
        assertEquals(0, vm.getStaffNotes().size());

        vm.setSelectedUser("bob");
        vm.setSelectedUser("alice");
        assertTrue(vm.getStaffNotes().isEmpty());
    }
    /*
	 * Test that clearing staff notes affects only the current user's notes.
	 */
    @Test
    public void clearStaffNotes_clearsCurrentUserNotesOnly() {
        vm.setSelectedUser("alice");
        vm.addStaffNote("a1");
        vm.addStaffNote("a2");
        assertEquals(2, vm.getStaffNotes().size());

        vm.clearStaffNotes();
        assertTrue(vm.getStaffNotes().isEmpty());

        vm.setSelectedUser("bob");
        vm.addStaffNote("b1");
        assertEquals(1, vm.getStaffNotes().size());

        vm.setSelectedUser("alice");
        assertTrue(vm.getStaffNotes().isEmpty());
    }

    /*
     * Test that loading user data populates questions for the selected user.
     */
    @Test
    public void loadUserData_populatesQuestionsForSelectedUser() {
        Question q = mock(Question.class);
        UserDTO mockAuthor = mock(UserDTO.class);
        when(mockAuthor.getUserName()).thenReturn("alice");
        when(q.getAuthor()).thenReturn(mockAuthor);
        when(questionService.getAllQuestions()).thenReturn(List.of(q));

        vm.setSelectedUser("alice");

        ObservableList<Question> questions = vm.getQuestions();
        assertEquals(1, questions.size());
        assertSame(q, questions.get(0));
    }
    
	/*
	 * Test that loading user data populates answers for the selected user.
	 */
    @Test
    public void loadUserData_populatesAnswersForSelectedUser() {
        Answer a = mock(Answer.class);
        UserDTO mockAuthor = mock(UserDTO.class);
        when(mockAuthor.getUserName()).thenReturn("alice");
        when(a.getAuthor()).thenReturn(mockAuthor);
		when(mockAuthor.getId()).thenReturn(1);
		when(userService.getUserNameById(1)).thenReturn("alice");
        when(answerService.getAllAnswers()).thenReturn(List.of(a));

        vm.setSelectedUser("alice");

        ObservableList<Answer> answers = vm.getAnswers();
        assertEquals(1, answers.size());
        assertSame(a, answers.get(0));
    }

    /*
	 * Test that loading user data populates messages for the selected user using username API.
	 */
    @Test
    public void loadUserData_populatesMessagesForSelectedUser_usingUsernameApi() {
        Message m = mock(Message.class);
        when(messageService.getMessagesForUsername("alice")).thenReturn(List.of(m));
        when(messageService.getAllMessages()).thenReturn(List.of());

        vm.setSelectedUser("alice");

        ObservableList<Message> messages = vm.getMessages();
        assertEquals(1, messages.size());
        assertSame(m, messages.get(0));
    }

    /*
	 * Test that flagCurrentContent does not throw exceptions when called
	 * with different content types, even if the implementation is commented out.
	 */
    @Test
    public void flagCurrentContent_noExceptionWhenCommentedOut() {
        // ensure calling flagCurrentContent for different content types does not throw
        Question q = mock(Question.class);
        vm.setCurrentlyDisplayedContent(q);
        vm.flagCurrentContent();

        Answer a = mock(Answer.class);
        vm.setCurrentlyDisplayedContent(a);
        vm.flagCurrentContent();

        Message m = mock(Message.class);
        vm.setCurrentlyDisplayedContent(m);
        vm.flagCurrentContent();
    }
}
