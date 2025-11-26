package Controllers.ForumPage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Config.AppCache;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IUserService;
import Exceptions.DBExceptions.DataAccessException;
import Models.Answer;
import Models.IPost;
import Models.IPost.Type;
import Models.Question;
import Sessions.UserSession;
import Utils.AnswerRatingInfo;
import Utils.TriConsumer;

import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

public class PostDisplayViewModel {
	private IPost post;
	private List<String> permissionList; // fetched from a service in the controller

	private final UserSession userSession;
	private final BooleanProperty isEditingPostProperty = new SimpleBooleanProperty(false);
	
	private final IUserService userService;
	private final IAnswerService answerService;

	private final BooleanProperty isExpandedProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty isCommentingProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty isDeletedProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty doesHaveChildrenProperty = new SimpleBooleanProperty(true);
	private final BooleanProperty isReviewingProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty shouldDisplayFavoredReviewerButtonProperty = new SimpleBooleanProperty(false);
	private final ObjectProperty<FilterType> filterProperty = new SimpleObjectProperty<>(FilterType.TIME);
	private final ObjectProperty<BigDecimal> ratingProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);
	
	private final IntegerProperty upvoteCountProperty = new SimpleIntegerProperty(0);
	private final ObjectProperty<BigDecimal> currentUserRatingProperty = new SimpleObjectProperty<>(null);

	private StringProperty labelTextProperty = new SimpleStringProperty();
	private StringProperty bodyTextProperty = new SimpleStringProperty();

	private int depthLevel;
	
	private final SharedForumState sharedForumState;
	
	private final Consumer<Integer> deleteCallback;
	private final Consumer<IPost> addCallBack;
	private final TriConsumer<Integer, String, String> updateCallBack;
	private final Runnable toggleExpandCallback;
	
	private final int ratingStarCount = 5;

	public enum FilterType {
		TIME
	}
	
	public enum RatingUpdateType {
		UPDATE,
		DELETE
	}

	public PostDisplayViewModel(
			IPost post, 
			List<String> permissionList, 
			UserSession userSession,
			IUserService userService,
			IAnswerService answerService,
			SharedForumState sharedForumState,
			Consumer<Integer> deleteCallBack, 
			Consumer<IPost> addCallBack,
			TriConsumer<Integer, String, String> updateCallBack, 
			Runnable toggleExpandCallback
	) {
		this.post = post;
		this.permissionList = permissionList;
		this.userSession = userSession;
		this.userService = userService;
		this.answerService = answerService;
		this.sharedForumState = sharedForumState;
		this.deleteCallback = deleteCallBack;
		this.addCallBack = addCallBack;
		this.updateCallBack = updateCallBack;
		this.toggleExpandCallback = toggleExpandCallback;

		bodyTextProperty.set(post.getBodyText());

		if (isQuestion()) {
			labelTextProperty.set(((Question) post).getTitleText());
		} else {
			setRatingProperty(((Answer)post).getAverageRating());
			setUpvoteCountProperty(((Answer)post).getUpvoteCount());
		}
		
		try {
		    if (post.getPostType() == Type.Review) {
		        int currentUserId = userSession.getCurrentUserId();
		        int postUserId = post.getAuthorId();
		        
		        boolean favoredReviewerStatus = userService.checkIfFavoredReviewerExists(currentUserId, postUserId);
		        setIsFavoredReviewerProperty(favoredReviewerStatus);
		        
		        setShouldDisplayFavoredReviewerButton();
		    }
		} catch (DataAccessException e) {
		    System.out.println(e.getLocalizedMessage());
		}

	}

	// ===================
	// GETTERS
	// ===================

	public IPost getPost() {
		return post;
	}
	
	public int getNumberOfRatingStars() {
		return ratingStarCount;
	}

	public List<String> getPermissionList() {
		return permissionList;
	}

	public int getDepthLevel() {
		return depthLevel;
	}

	public BooleanProperty getIsEditingPostProperty() {
		return isEditingPostProperty;
	}

	public BooleanProperty getIsReviewingProperty() {
		return isReviewingProperty;
	}

	public FilterType getFilterType() {
		return filterProperty.get();
	}

	public BooleanProperty getIsCommentingProperty() {
		return isCommentingProperty;
	}
	
	private final BooleanProperty isFavoredReviewerProperty = new SimpleBooleanProperty(false);

	public BooleanProperty getIsFavoredReviewerProperty() {
	    return isFavoredReviewerProperty;
	}

	public void setIsFavoredReviewerProperty(boolean value) {
	    isFavoredReviewerProperty.set(value);
	}

	
	public BooleanProperty getShouldDisplayFavoredReviewerButtonProperty() {
		return shouldDisplayFavoredReviewerButtonProperty;
	}

	public BooleanProperty getDoesHaveChildren() {
		return doesHaveChildrenProperty;
	}

	public BooleanProperty getIsDeletedProperty() {
		return isDeletedProperty;
	}

	public String getMainLabelText() {
		try {
			return ((Question) post).getTitleText();
		} catch (ClassCastException e) {
			return null;
		}
	}

	public StringProperty getLabelTextProperty() {
		return labelTextProperty;
	}

	public StringProperty getBodyTextProperty() {
		return bodyTextProperty;
	}

	public boolean isQuestion() {
		return post.getPostType() == IPost.Type.Question;
	}

	public boolean getIsDeleted() {
		return post.getIsDeleted();
	}

	public boolean isExpandButtonPressed() {
		return isExpandedProperty.get();
	}

	// Properties that cannot be active at once
	private List<BooleanProperty> nonConcurrentProperties() {
		return new ArrayList<BooleanProperty>(List.of(isReviewingProperty, isCommentingProperty));
	}
	
	public ObjectProperty<BigDecimal> getRatingProperty() {
	    return ratingProperty;
	}
	
	public BigDecimal getStarRating() {
 		BigDecimal starsToFillRating = getCurrentUserRating() != null ? currentUserRatingProperty.get() : ratingProperty.get();

		return starsToFillRating.multiply(new BigDecimal(ratingStarCount));
	}
	
	public IntegerProperty getUpvoteCountProperty() {
	    return upvoteCountProperty;
	}
	
	public BigDecimal getCurrentUserRating() {
		return currentUserRatingProperty.get();
	}
	
	public boolean isRatedByUser() {
		return getCurrentUserRating() != null;
	}

	// ===================
	// SETTERS
	// ===================

	public void setDoesHaveChildrenProperty(boolean doesHaveChildren) {
		this.doesHaveChildrenProperty.set(doesHaveChildren);
	}
	
	public void setShouldDisplayFavoredReviewerButton() {
	    boolean isReviewType = post.getPostType() == Type.Review;
	    boolean isCurrentUserAuthor = userSession.getCurrentUserId() == post.getAuthorId();

	    boolean shouldDisplay = isReviewType && !isCurrentUserAuthor;
	    shouldDisplayFavoredReviewerButtonProperty.set(shouldDisplay);
	}

	public void setFilterType(FilterType newFilterType) {
		this.filterProperty.set(newFilterType);
	}

	public void setDepthLevel(int level) {
		this.depthLevel = level;
	}

	public void setIsEditingProperty(boolean isEditing) {
		this.isEditingPostProperty.set(isEditing);
	}

	public void setIsReviewingProperty(boolean isReviewing) {
		resetNonConcurrentActions();
		this.isReviewingProperty.set(isReviewing);
	}

	public void setIsCommentingProperty(boolean isCommenting) {
		resetNonConcurrentActions();
		this.isCommentingProperty.set(isCommenting);
	}

	public void setIsDeletedProperty(boolean isDeleted) {
		this.isDeletedProperty.set(isDeleted);
	}

	// Displaying children elements
	public void setIsExpanded(boolean isExpanded) {
		this.isExpandedProperty.setValue(isExpanded);
	}

	public void setLabelTextProperty(String newLabelText) {
		labelTextProperty.set(newLabelText);
	}

	public void setBodyTextProperty(String newBodyText) {
		bodyTextProperty.set(newBodyText);
	}

	public BooleanProperty getExpandedProperty() {
		return isExpandedProperty;
	}
	
	// Building rating staight from the database-style rating 0.0-1.0
	public void setRatingProperty(BigDecimal newRating) {
	    ratingProperty.set(newRating);
	}
	
	// Building rating from the UI Style, IE: 1-5 for stars
	public void setRatingProperty(int starSelected) {
	    // Normalize new rating to 0–1 scale
	    BigDecimal newRating = BigDecimal.valueOf(starSelected - 1)
	            .divide(BigDecimal.valueOf(ratingStarCount - 1), 2, RoundingMode.HALF_UP);
	    
	    BigDecimal starRating = BigDecimal.valueOf(starSelected)
			    .divide(BigDecimal.valueOf(ratingStarCount), 2, RoundingMode.HALF_UP);

	    // Normalize old rating if it exists
	    BigDecimal oldRating = currentUserRatingProperty.get();
//	    System.out.println("New rating: " + newRating);
//	    System.out.println("Old rating: " + oldRating);

	    BigDecimal currentAverage = ((Answer) post).getAverageRating();
	    int currentCount = ((Answer) post).getUpvoteCount();

	    AnswerRatingInfo newAvgRatingAndUpvoteCount;
	    
	    Answer answer = ((Answer)post);

	    try {
	        if (oldRating != null && starRating.compareTo(oldRating) == 0) {
	            // User is removing their rating
	            newAvgRatingAndUpvoteCount = applyRatingChange(currentAverage, currentCount, oldRating, null);

	            updateRatingInBackground(null, oldRating, currentCount, RatingUpdateType.DELETE);
	            
	            answer.setUpvoteCount(currentCount - 1);
	            currentUserRatingProperty.set(getRatingProperty().get());

	        } else {
	            // User is adding or changing a rating
	            newAvgRatingAndUpvoteCount = applyRatingChange(currentAverage, currentCount, oldRating, newRating);

	            updateRatingInBackground(newRating, oldRating, currentCount, RatingUpdateType.UPDATE);
	            
	            answer.setUpvoteCount(currentCount + 1);
	            currentUserRatingProperty.set(starRating);
	        }

	        // Update UI immediately
	        setRatingProperty(newAvgRatingAndUpvoteCount.getAverageRating());
	        upvoteCountProperty.set(newAvgRatingAndUpvoteCount.getUpvoteCount());

	    } catch (Exception e) {
	        System.out.println(e.getLocalizedMessage());
	    }
	}

	
	public void setUpvoteCountProperty(int upvoteCount) {
		upvoteCountProperty.set(upvoteCount);
	}
	
	public void resetRating() {
		setRatingProperty(((Answer)post).getAverageRating());
	}
	
	public void setUserDefinedRating() {		
		int answerId = post.getId();
		BigDecimal cachedRating = AppCache.getInstance().getRatingForAnswer(answerId);
		
		if(cachedRating == null) {
			BigDecimal retrievedRating = answerService.getRating(answerId, userSession.getCurrentUserId());
			BigDecimal converted;
			
			System.out.println("Retrieved: " + retrievedRating);
			if(retrievedRating == null) {
				retrievedRating = BigDecimal.ZERO;
				converted = BigDecimal.ZERO;
			} else {
				converted = retrievedRating.multiply(BigDecimal.valueOf(0.8)).add(BigDecimal.valueOf(0.2));
			}
			System.out.println("Converted: " + converted);
			AppCache.getInstance().setRatingForAnswer(answerId, retrievedRating);
			currentUserRatingProperty.set(converted);
		} else {
			currentUserRatingProperty.set(cachedRating);
		}
	}

	// ===================
	// CRUD
	// ===================

	public void deletePost() {
		setIsDeletedProperty(!getIsDeletedProperty().get());
		deleteCallback.accept(post.getId());
	}

	public void toggleExpand() {
		setIsExpanded(!isExpandButtonPressed());
		this.toggleExpandCallback.run();
	}

	public void updatePost(String newTitle, String newBody) {
		if (isQuestion()) {
			updateCallBack.accept(post.getId(), newTitle, newBody);
		} else {
			// A bit hacky, but I didn't see a better option here
			updateCallBack.accept(post.getId(), newBody, ((Answer) post).isCorrect() ? "yes" : "no");
		}
	}

	public void setRating(int newRating) {
		
	}
	
	public void addAnswer(String newTitleText, String newBodyText, IPost.Type answerType) {
		IPost.Type currentPostType = post.getPostType();
		
		int questionId;
		Integer parentAnswerId;
		
		if(currentPostType == Type.Question) {
			questionId = post.getId();
			parentAnswerId = null;
		} else {
			questionId = ((Answer) post).getQuestionId();
			parentAnswerId = post.getId();
		}
		
		boolean isReview;
		if(answerType == Type.Answer) {
			isReview = false;
		} else {
			isReview = true;
		}
		
		Answer newAnswer = new Answer(
				questionId, 
				parentAnswerId, 
				userSession.getCurrentUserId(),
				userSession.getCurrentUserName(), 
				newBodyText,
				isReview
		);

		addCallBack.accept(newAnswer);
	}
	
	// ===================
	// HELPERS
	// ===================
	
	public void toggleFavoredReviewerStatus(boolean newStatus) {
		try {
			userService.toggleIsFavoredReviewer(userSession.getCurrentUserId(), post.getAuthorId());
		} catch(DataAccessException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		setIsFavoredReviewerProperty(newStatus);
	}
	
	// Properties that cannot be active at the same time. 
	// IE: Commenting and Reviewing windows cannot both be open at once
	private void resetNonConcurrentActions() {
		nonConcurrentProperties().forEach(property -> {
			property.set(false);
		});
	}
	
	private AnswerRatingInfo applyRatingChange(
	        BigDecimal avg,
	        int count,
	        BigDecimal oldRating,
	        BigDecimal newRating)
	{
		System.out.println("Average: " + avg);
		System.out.println("Count: " + count);
	    BigDecimal total = avg.multiply(BigDecimal.valueOf(count));

	    // Remove old rating
	    if (oldRating != null) {
	        total = total.subtract(oldRating);
	        count--;
	    }

	    // Add new rating
	    if (newRating != null) {
	        total = total.add(newRating);
	        count++;
	    }

	    if (count == 0) {
	        return new AnswerRatingInfo(BigDecimal.ZERO, 0);
	    }

	    BigDecimal newAverage =
	        total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

	    return new AnswerRatingInfo(newAverage, count);
	}

	
	private void updateRatingInBackground(BigDecimal newRating, BigDecimal oldRating, int oldUpvoteCount, RatingUpdateType updateType) {
		Task<Void> updateTask = new Task<>() {
			@Override
			protected Void call() throws Exception {
				if(updateType == RatingUpdateType.DELETE) {
					answerService.removeRating(post.getId(), userSession.getCurrentUserId());
					System.out.println("Removed");
				} else {
					System.out.println("New Rating: " + newRating);
					answerService.updateRating(post.getId(), userSession.getCurrentUserId(), newRating);
				}
				return null;
			}

			@Override
			protected void failed() {
				currentUserRatingProperty.set(oldRating);
				upvoteCountProperty.set(oldUpvoteCount);
			}
		};

		new Thread(updateTask).start();
	}
}



