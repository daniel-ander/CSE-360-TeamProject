package Tests;

import Controllers.ForumPage.PostDisplayViewModel;
import Controllers.ForumPage.SharedForumState;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IUserService;
import Models.Answer;
import Models.IPost;
import Models.Question;
import Models.DBModels.UserDTO;
import Sessions.UserSession;
import Utils.RatingStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import Tests.Utils.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PostDisplayViewModel}.
 */
public class PostDisplayViewModelTest {

    private IUserService userService;
    private IAnswerService answerService;
    private UserSession userSession;
    private SharedForumState sharedForumState;

    private Consumer<Integer> deleteCallback;
    private Consumer<IPost> addCallback;
    private Runnable toggleExpandCallback;

    private PostDisplayViewModel questionVM;
    private PostDisplayViewModel answerVM;

    private Question question;
    private Answer answer;
    private UserDTO userDTO;

    /**
     * Initializes mocks, models, and view models before each test.
     */
    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        answerService = mock(IAnswerService.class);
        userSession = mock(UserSession.class);
        sharedForumState = mock(SharedForumState.class);
        deleteCallback = mock(Consumer.class);
        addCallback = mock(Consumer.class);

        toggleExpandCallback = mock(Runnable.class);

        userDTO = Utils.getUserDTOModel();

        question = new Question(1, userDTO, "Title", "Content", null, null, false, false, false, 0);
        answer = new Answer(1, 1, userDTO, "Answer Content", false);
        answer.setId(2);

        when(userSession.getCurrentUserId()).thenReturn(1);
        when(userSession.getDTOModel()).thenReturn(userDTO);

        questionVM = new PostDisplayViewModel(
                question,
                List.of("EDIT"),
                userSession,
                userService,
                answerService,
                sharedForumState,
                deleteCallback,
                addCallback,
                (id, t, b) -> {},
                toggleExpandCallback
        );

        answerVM = new PostDisplayViewModel(
                answer,
                List.of("EDIT"),
                userSession,
                userService,
                answerService,
                sharedForumState,
                deleteCallback,
                addCallback,
                (id, t, b) -> {},
                toggleExpandCallback
        );
    }

    /**
     * Tests that setting depth level updates the internal depth value.
     */
    @Test
    void testSetAndGetDepthLevel() {
        questionVM.setDepthLevel(3);
        assertEquals(3, questionVM.getDepthLevel());
    }

    /**
     * Tests that toggling expand updates the boolean and triggers callback.
     */
    @Test
    void testToggleExpand() {
        assertFalse(questionVM.isExpandButtonPressed());
        questionVM.toggleExpand();
        assertTrue(questionVM.isExpandButtonPressed());
        verify(toggleExpandCallback, times(1)).run();
    }

    /**
     * Tests that deleting a post marks it deleted and calls the callback.
     */
    @Test
    void testDeletePostTriggersCallbackAndSetsDeleted() {
        questionVM.setIsDeletedProperty(false);
        questionVM.deletePost();
        assertTrue(questionVM.getIsDeletedProperty().get());
        verify(deleteCallback).accept(question.getId());
    }

    /**
     * Tests that updating a question's text triggers the provided update callback.
     */
    @Test
    void testUpdatePostCallsCallbackForQuestion() {
        AtomicBoolean called = new AtomicBoolean(false);
        PostDisplayViewModel vm = new PostDisplayViewModel(
                question,
                List.of(),
                userSession,
                userService,
                answerService,
                sharedForumState,
                deleteCallback,
                addCallback,
                (id, title, body) -> called.set(true),
                toggleExpandCallback
        );
        vm.updatePost("New Title", "New Body");
        assertTrue(called.get());
    }

    /**
     * Tests that editing flag updates correctly.
     */
    @Test
    void testSetIsEditingProperty() {
        questionVM.setIsEditingProperty(true);
        assertTrue(questionVM.getIsEditingPostProperty().get());
    }

    /**
     * Tests that commenting and reviewing states are mutually exclusive.
     */
    @Test
    void testNonConcurrentProperties() {
        questionVM.setIsCommentingProperty(true);
        questionVM.setIsReviewingProperty(true);
        assertFalse(questionVM.getIsCommentingProperty().get());
        assertTrue(questionVM.getIsReviewingProperty().get());
    }

    /**
     * Tests that rating and vote count properties can be set and retrieved.
     */
    @Test
    void testSetRatingPropertyAndUpvoteCount() {
        BigDecimal rating = new BigDecimal("0.8");
        answerVM.setRatingProperty(rating);
        answerVM.setUpvoteCountProperty(5);

        assertEquals(rating, answerVM.getRatingProperty().get());
        assertEquals(5, answerVM.getUpvoteCountProperty().get());
    }

    /**
     * Tests that the button indicating whether a reviewer can be favored displays correctly.
     */
    @Test
    void testSetShouldDisplayFavoredReviewerButton() {
        when(userSession.getCurrentUserId()).thenReturn(2);
        Answer reviewAnswer = new Answer(1, 1, userDTO, "Review Content", true);
        PostDisplayViewModel reviewVM = new PostDisplayViewModel(
                reviewAnswer,
                List.of(),
                userSession,
                userService,
                answerService,
                sharedForumState,
                deleteCallback,
                addCallback,
                (id, t, b) -> {},
                toggleExpandCallback
        );

        reviewVM.setShouldDisplayFavoredReviewerButton();
        assertFalse(reviewVM.getShouldDisplayFavoredReviewerButtonProperty().get());
    }

    /**
     * Tests that setting the same rating removes the rating and updates stats accordingly.
     */
    @Test
    void testSetPersonalRatingPropertyRemovesRatingWhenSame() throws Exception {
        BigDecimal oldRating = BigDecimal.valueOf(0.6);
        RatingStats newStats = new RatingStats(BigDecimal.valueOf(0.0), 0);

        answerVM.setRatingProperty(oldRating);
        answerVM.setCurrentUserRatingProperty(oldRating);

        when(answerService.removeRating(answer.getId(), userSession.getCurrentUserId()))
                .thenReturn(newStats);

        answerVM.setPersonalRatingProperty(3);
        assertNull(answerVM.getCurrentUserRating());
        assertEquals(newStats.getAverageRating(), answerVM.getRatingProperty().get());
        assertEquals(newStats.getUpvoteCount(), answerVM.getUpvoteCountProperty().get());
    }

    /**
     * Tests conversion of personal rating value into number of stars.
     */
    @Test
    void testGetCurrentUserStarRating() {
        BigDecimal personalRating = BigDecimal.valueOf(0.7);
        answerVM.setCurrentUserRatingProperty(personalRating);

        BigDecimal expectedStars = personalRating.multiply(BigDecimal.valueOf(answerVM.getNumberOfRatingStars()));
        assertEquals(expectedStars, answerVM.getCurrentUserStarRating());
    }

    /**
     * Tests whether the view model correctly identifies whether the user rated the post.
     */
    @Test
    void testIsRatedByUser() {
        answerVM.setCurrentUserRatingProperty(BigDecimal.valueOf(0.5));
        assertTrue(answerVM.isRatedByUser());

        answerVM.setCurrentUserRatingProperty(BigDecimal.ZERO);
        assertFalse(answerVM.isRatedByUser());

        answerVM.setCurrentUserRatingProperty(null);
        assertFalse(answerVM.isRatedByUser());
    }

    /**
     * Tests that resetting rating restores the original average rating stored in the post model.
     */
    @Test
    void testResetRatingRestoresAnswerAverage() {
        BigDecimal oldAverage = BigDecimal.valueOf(0.6);

        ((Answer) answerVM.getPost()).setAverageRating(oldAverage);

        answerVM.setRatingProperty(BigDecimal.valueOf(0.2));
        answerVM.resetRating();

        assertEquals(oldAverage, answerVM.getRatingProperty().get());
    }
}
