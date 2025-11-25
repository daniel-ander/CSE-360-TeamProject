/*
	Unit tests for StaffViewModel using JUnit and Mockito.
	Tests cover user loading, per-user staff notes management,
	and loading of questions, answers, and messages for selected users.
*/

package Tests;

import Controllers.StaffPage.StaffViewModel; 
import Database.Services.Interfaces.IUserService; 
import Database.Services.Interfaces.IQuestionService; 
import Database.Services.Interfaces.IAnswerService; 
import Database.Services.Interfaces.IMessageService; 
import Models.Question; 
import Models.Answer; 
import Models.Message; 
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test; 
import org.mockito.Mockito;

import javafx.collections.ObservableList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;

public class StaffViewModelTest {

private IUserService mockUserService;
private IQuestionService mockQuestionService;
private IAnswerService mockAnswerService;
private IMessageService mockMessageService;
private StaffViewModel vm;

/*
	Setup mocks and default behaviors before each test.
*/

@BeforeEach
public void setUp() throws Exception {
    mockUserService = Mockito.mock(IUserService.class);
    mockQuestionService = Mockito.mock(IQuestionService.class);
    mockAnswerService = Mockito.mock(IAnswerService.class);
    mockMessageService = Mockito.mock(IMessageService.class);

    when(mockUserService.getAllUsernames()).thenReturn(List.of("alice", "bob"));

    // default empty returns; tests override as needed
    when(mockQuestionService.getAllQuestions()).thenReturn(List.of());
    when(mockAnswerService.getAllAnswers()).thenReturn(List.of());
    when(mockMessageService.getMessagesForUsername(anyString())).thenReturn(List.of());
    when(mockMessageService.getAllMessages()).thenReturn(List.of());

    vm = new StaffViewModel(
        mockUserService,
        mockQuestionService,
        mockAnswerService,
        mockMessageService
    );
}

/*
	Test that loading all users populates the users list correctly.
*/

@Test
public void loadAllUsers_populatesUsersList() {
    ObservableList<String> users = vm.getUsers();
    assertEquals(2, users.size());
    assertTrue(users.contains("alice"));
    assertTrue(users.contains("bob"));
}

/*
	Test that adding, switching, and managing staff notes works per user.
*/
@Test
public void switchingUsers_preservesPerUserNotes_and_updatesStaffNotesList() {
    // select alice and add a note
    vm.setSelectedUser("alice");
    vm.addStaffNote("Alice note");
    ObservableList<String> aliceNotes = vm.getStaffNotes();
    assertEquals(1, aliceNotes.size());
    assertTrue(aliceNotes.get(0).contains("Alice note"));
    assertTrue(aliceNotes.get(0).contains(" | ")); // timestamp separator present

    // switch to bob and add a different note
    vm.setSelectedUser("bob");
    vm.addStaffNote("Bob note");
    ObservableList<String> bobNotes = vm.getStaffNotes();
    assertEquals(1, bobNotes.size());
    assertTrue(bobNotes.get(0).contains("Bob note"));

    // switch back to alice -> ensure alice's note is restored
    vm.setSelectedUser("alice");
    ObservableList<String> restoredAlice = vm.getStaffNotes();
    assertEquals(1, restoredAlice.size());
    assertTrue(restoredAlice.get(0).contains("Alice note"));
    assertFalse(restoredAlice.get(0).contains("Bob note"));
}
/*
	Test removing a staff note works correctly for the current user.
*/
@Test
public void removeStaffNoteAt_removesFromCurrentUserStorage() {
    vm.setSelectedUser("alice");
    vm.addStaffNote("to be removed");
    assertEquals(1, vm.getStaffNotes().size());

    vm.removeStaffNoteAt(0);
    assertEquals(0, vm.getStaffNotes().size());

    // switch away and back to confirm storage is empty for alice
    vm.setSelectedUser("bob");
    vm.setSelectedUser("alice");
    assertTrue(vm.getStaffNotes().isEmpty());
}
/*
	Test clearing staff notes only affects the current user's notes.
*/
@Test
public void clearStaffNotes_clearsCurrentUserNotesOnly() {
    vm.setSelectedUser("alice");
    vm.addStaffNote("a1");
    vm.addStaffNote("a2");
    assertEquals(2, vm.getStaffNotes().size());

    vm.clearStaffNotes();
    assertTrue(vm.getStaffNotes().isEmpty());

    // bob should be unaffected
    vm.setSelectedUser("bob");
    vm.addStaffNote("b1");
    assertEquals(1, vm.getStaffNotes().size());

    // return to alice -> still empty
    vm.setSelectedUser("alice");
    assertTrue(vm.getStaffNotes().isEmpty());
}

/*
	Test that loading questions for the selected user works correctly.
*/
@Test
public void loadUserData_populatesQuestionsForSelectedUser() {
    // create a mock question whose authorId maps to "alice"
    Question q = mock(Question.class);
    when(q.getAuthorId()).thenReturn(1);
    when(mockUserService.getUserNameById(1)).thenReturn("alice");
    when(mockQuestionService.getAllQuestions()).thenReturn(List.of(q));

    vm.setSelectedUser("alice");

    ObservableList<Question> questions = vm.getQuestions();
    assertEquals(1, questions.size());
    assertSame(q, questions.get(0));
}
/*
	Test that loading answers for the selected user works correctly.
*/
@Test
public void loadUserData_populatesAnswersForSelectedUser() {
    // create a mock answer with authorName "alice"
    Answer a = mock(Answer.class);
    when(a.getAuthorName()).thenReturn("alice");
    when(mockAnswerService.getAllAnswers()).thenReturn(List.of(a));

    vm.setSelectedUser("alice");

    ObservableList<Answer> answers = vm.getAnswers();
    assertEquals(1, answers.size());
    assertSame(a, answers.get(0));
}
/*
	Test that loading messages for the selected user works correctly
	using the getMessagesForUsername API.
*/
@Test
public void loadUserData_populatesMessagesForSelectedUser_usingUsernameApi() {
    // create a mock message and ensure messageService.getMessagesForUsername is used
    Message m = mock(Message.class);
    when(mockMessageService.getMessagesForUsername("alice")).thenReturn(List.of(m));
    // ensure fallback won't be used
    when(mockMessageService.getAllMessages()).thenReturn(List.of());

    vm.setSelectedUser("alice");

    ObservableList<Message> messages = vm.getMessages();
    assertEquals(1, messages.size());
    assertSame(m, messages.get(0));
}

}