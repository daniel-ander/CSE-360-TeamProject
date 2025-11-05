package Tests;

import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Models.Answer;
import Models.IPost;
import Models.Question;
import Sessions.UserSession;
import Utils.KeyPairHolder;
import javafx.collections.ObservableList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Controllers.ForumPage.PostDisplayViewModel;
import Controllers.ForumPage.PostListViewModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PostListViewModelTest {

    private IQuestionService questionService;
    private IAnswerService answerService;
    private UserSession userSession;
    private PostListViewModel viewModel;

    @BeforeEach
    void setUp() {
        questionService = mock(IQuestionService.class);
        answerService = mock(IAnswerService.class);
        userSession = mock(UserSession.class);
        viewModel = new PostListViewModel(questionService, answerService, userSession);
    }

    /**
     * Test that adding a new question does indeed populate visible posts
     * and makes sure the proper post is displayed
     */
    @Test
    public void testPopulateQuestionsAddsQuestionsToVisiblePosts() {
        // Arrange
        Question question = new Question(1, 1, "Title", "Content", null, null, false, false, false, 0);
        PostWithPermissions pw = new PostWithPermissions(question, List.of("EDIT"));

        // Act
        viewModel.populateQuestions(List.of(pw));

        // Assert
        assertEquals(1, viewModel.getVisiblePosts().size());
        assertEquals(question, viewModel.getVisiblePosts().get(0).getPost());
    }

    /**
     * Tests that updating the question properly sets the label text
     */
    @Test
    public void testUpdateQuestionUpdatesLabelText() {
        // Arrange
        Question q = new Question(1, 1, "Title", "Content", null, null, false, false, false, 0);
        PostWithPermissions pw = new PostWithPermissions(q, List.of("EDIT"));
        viewModel.populateQuestions(List.of(pw));

        // Act
        viewModel.updateQuestion(q.getId(), "New Title", "New Content");

        // Assert
        assertEquals("New Title", viewModel.getVisiblePosts().get(0).getLabelTextProperty().get());
    }

    /**
     * Tests that adding the post via "addPostToVisibleList" method properly 
     * adds new answer and sets 
     */
    @Test
    public void testAddPostToVisibleListAddsAnswerAndExpandsParent() {
        // Arrange
        Question q = new Question(1, "Q", "Content");
        q.setId(1);
        viewModel.populateQuestions(List.of(new PostWithPermissions(q, List.of("EDIT"))));
        PostDisplayViewModel questionVM = viewModel.getVisiblePosts().get(0);
        Answer newAnswer = new Answer(1, 1, "A1", "Text");
        newAnswer.setId(10);

        // Act
        viewModel.addAnswerToVisibleList(newAnswer);

        // Assert
        ObservableList<PostDisplayViewModel> visible = viewModel.getVisiblePosts();
        assertEquals(2, visible.size());
        assertEquals(q, visible.get(0).getPost());
        assertEquals(newAnswer, visible.get(1).getPost());
        assertTrue(questionVM.getDoesHaveChildren().get());
        assertTrue(questionVM.getExpandedProperty().get());

        PostDisplayViewModel answerVM = visible.get(1);
        assertEquals(1, answerVM.getDepthLevel());
    }

    /**
     * Tests the expanding functionality in making children answers visible
     */
    @Test
    public void testExpandChildrenMakesAnswersVisible() {
        // Arrange
        Question q = new Question(1, "Q", "Content");
        q.setId(1);
        viewModel.populateQuestions(List.of(new PostWithPermissions(q, List.of("EDIT"))));

        Answer a1 = new Answer(1, 1, "A1", "Text");
        a1.setId(10);
        Answer a2 = new Answer(1, 1, "A2", "Text");
        a2.setId(11);
        viewModel.addToAnswerPool(List.of(
                new PostWithPermissions(a1, List.of("EDIT")),
                new PostWithPermissions(a2, List.of("EDIT"))
        ));

        PostDisplayViewModel questionVM = viewModel.getVisiblePosts().get(0);
        questionVM.setIsExpanded(true);

        // Act
        viewModel.toggleExpand(1, new KeyPairHolder<>(1, null), IPost.Type.Question);

        // Assert
        ObservableList<PostDisplayViewModel> visible = viewModel.getVisiblePosts();
        assertEquals(3, visible.size());
        assertEquals(q, visible.get(0).getPost());
        assertEquals(a1, visible.get(1).getPost());
        assertEquals(a2, visible.get(2).getPost());
    }

    /**
     * Tests the collapsing functionality in making children answers disappear
     * from the visible post list
     */
    @Test
    public void testCollapseChildrenHidesAnswers() {
        // Arrange
        Question q = new Question(1, "Q", "Content");
        q.setId(1);
        viewModel.populateQuestions(List.of(new PostWithPermissions(q, List.of("EDIT"))));

        Answer a1 = new Answer(1, 1, "A1", "Text");
        a1.setId(10);
        Answer a2 = new Answer(1, 1, "A2", "Text");
        a2.setId(11);
        viewModel.addToAnswerPool(List.of(
                new PostWithPermissions(a1, List.of("EDIT")),
                new PostWithPermissions(a2, List.of("EDIT"))
        ));

        PostDisplayViewModel questionVM = viewModel.getVisiblePosts().get(0);
        questionVM.setIsExpanded(true);
        viewModel.toggleExpand(1, new KeyPairHolder<>(1, null), IPost.Type.Question);

        // Assert initial expanded state
        ObservableList<PostDisplayViewModel> visible = viewModel.getVisiblePosts();
        assertEquals(3, visible.size());
        assertEquals(q, visible.get(0).getPost());
        assertEquals(a1, visible.get(1).getPost());
        assertEquals(a2, visible.get(2).getPost());

        // Act: collapse question
        questionVM.setIsExpanded(false);
        viewModel.toggleExpand(1, new KeyPairHolder<>(1, null), IPost.Type.Question);

        // Assert collapsed state
        visible = viewModel.getVisiblePosts();
        assertEquals(1, visible.size());
        assertEquals(q, visible.get(0).getPost());
    }
}