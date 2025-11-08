package Controllers.ForumPage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Models.Answer;
import Models.IPost;
import Models.IPost.Type;
import Sessions.UserSession;
import Utils.RolePermissions;
import Utils.KeyPairHolder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PostListViewModel {

	// ==============================
	// DATA STRUCTURES
	// ==============================

	public enum FilterMode {
		NONE, REVIEW
	}

	public void setFilterMode(FilterMode mode) {
		currentFilterMode = mode;
	}

	private FilterMode currentFilterMode = FilterMode.NONE;

	// Observable list of currently visible posts (questions + answers)
	private ObservableList<PostDisplayViewModel> visiblePosts = FXCollections.observableArrayList();
	private final List<PostDisplayViewModel> allQuestionsStore = new ArrayList<>(visiblePosts); // Stores pre-filter
																								// posts for
	// resetting

	// Maps parent posts (questionId + parentAnswerId) to their child answers
	private final Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> postParentMap = new HashMap<>();
	private final Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> reviewPostParentMap = new HashMap<>();

	// External dependencies
	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final UserSession userSession;

	// ==============================
	// CONSTRUCTOR
	// ==============================

	public PostListViewModel(IQuestionService questionService, IAnswerService answerService, UserSession userSession) {
		this.questionService = questionService;
		this.answerService = answerService;
		this.userSession = userSession;
	}

	// ==============================
	// PUBLIC METHODS
	// ==============================
	
	/**
	 * Resets visible posts to initial state with all the questions
	 */
	public void resetVisiblePosts() {
	    visiblePosts.setAll(allQuestionsStore);
	}

	/**
	 * Filters visible posts by title and body text with the given search term
	 */
	public void filterVisibleQuestionsByText(String text) {
		String term = text.toLowerCase();
		List<PostDisplayViewModel> filtered = allQuestionsStore.stream()
				.filter(vm -> vm.getPost().getTitleText().toLowerCase().contains(term)
						|| vm.getPost().getBodyText().toLowerCase().contains(term))
				.collect(Collectors.toList());

		visiblePosts.setAll(filtered);
	}

	/**
	 * Filters visible posts by ones that contain a review by the current user who
	 * is a reviewer
	 */

	//
	public void filterVisibleQuestionsByReview() {
		reviewPostParentMap.clear();

		postParentMap.forEach((parentQuestionAnswerIDKey, postVMs) -> {
			for (PostDisplayViewModel answerVM : postVMs) {
				boolean postIsReviewType = ((Answer) answerVM.getPost()).isReview();
				boolean postBelongsToUser = ((Answer) answerVM.getPost()).getAuthorId() == userSession
						.getCurrentUserId();

				if (postIsReviewType && postBelongsToUser) {
					PostDisplayViewModel reviewOfCurrentReviewer = answerVM;
					populateReviewMap(parentQuestionAnswerIDKey, reviewOfCurrentReviewer);
				}
			}
		});

		List<Integer> idsOfQuestionsContainingReview = new ArrayList<>();

		reviewPostParentMap.forEach((questionAnswerIDKeys, _) -> {
			if (!idsOfQuestionsContainingReview.contains(questionAnswerIDKeys.getFirstKey())) {
				idsOfQuestionsContainingReview.add(questionAnswerIDKeys.getFirstKey());
			}
		});

		List<PostWithPermissions> questionsContainingReviews = new ArrayList<>();

		idsOfQuestionsContainingReview.forEach(questionID -> {
			PostDisplayViewModel questionContainingReviews = getVisiblePostByID(questionID, IPost.Type.Question);
			PostWithPermissions questionWithPermissions = new PostWithPermissions(questionContainingReviews.getPost(),
					questionContainingReviews.getPermissionList());
			questionsContainingReviews.add(questionWithPermissions);
		});

		populateQuestions(questionsContainingReviews);
		
		expandAllVisibleQuestions();
	}

	private void populateReviewMap(KeyPairHolder<Integer, Integer> parentQuestionAnswerIDKey,
			PostDisplayViewModel answerVM) {
		PostDisplayViewModel directParentAnswer = getDirectParentOfAnswer(answerVM.getPost().getId());
		System.out.println("The parent body is: " + directParentAnswer.getBodyTextProperty().get());

		if (reviewPostParentMap.containsKey(parentQuestionAnswerIDKey)) {
			List<PostDisplayViewModel> reviewList = reviewPostParentMap.get(parentQuestionAnswerIDKey);
			if(directParentAnswer.getPost().getPostType() == Type.Answer) {
				reviewList.add(directParentAnswer);
			}
			reviewList.add(answerVM);
			reviewPostParentMap.replace(parentQuestionAnswerIDKey, reviewList);
		} else {
			List<PostDisplayViewModel> newReviewList;
			
			if(directParentAnswer.getPost().getPostType() == Type.Answer) {
				newReviewList = new ArrayList<>(Arrays.asList(directParentAnswer, answerVM));
			} else {
				newReviewList = new ArrayList<>(Arrays.asList(answerVM));
			}
			reviewPostParentMap.put(parentQuestionAnswerIDKey, newReviewList);
		}
	}

	/**
	 * Populates the visible posts with questions
	 */
	public void populateQuestions(List<PostWithPermissions> questionsWithPermissions) {
		visiblePosts.clear();

		for (PostWithPermissions questionWithPermissions : questionsWithPermissions) {
			addQuestionToVisibleList(questionWithPermissions, false);
		}
	}

	public void addQuestionToVisibleList(PostWithPermissions questionWithPermissions, boolean toTopOfStack) {
		// Build a ViewModel for each question
		PostDisplayViewModel vm = buildPost(questionWithPermissions.getPost(), questionWithPermissions.getPermissions(),
				questionWithPermissions.getPost().getId(), null, IPost.Type.Question);

		vm.setDepthLevel(0); // Top-level (no indentation)

		if (toTopOfStack) {
			visiblePosts.add(0, vm); // Add to the visible post list
			for (PostDisplayViewModel question : allQuestionsStore) {
				if (question.getPost().getId() == vm.getPost().getId()) {
					return;
				}
			}
			allQuestionsStore.add(0, vm);
		} else {
			visiblePosts.add(vm); // Add to the visible post list
			for (PostDisplayViewModel question : allQuestionsStore) {
				if (question.getPost().getId() == vm.getPost().getId()) {
					return;
				}
			}
			allQuestionsStore.add(vm);
		}
	}

	/**
	 * Builds the parent-child relationship map for future answer expansion on
	 * toggle expand
	 */
	public void addToAnswerPool(List<PostWithPermissions> answersWithPermissions) {
		for (PostWithPermissions answerWithPermissions : answersWithPermissions) {
			Answer answer = (Answer) answerWithPermissions.getPost();

			int parentQuestionId = answer.getQuestionId();
			Integer parentAnswerId = answer.getParentAnswerId();

			// Each answer belongs under a unique (questionId, parentAnswerId) key
			final KeyPairHolder<Integer, Integer> keys = new KeyPairHolder<>(parentQuestionId, parentAnswerId);

			// Build a ViewModel for the answer
			PostDisplayViewModel newAnswerVM = buildPost(answerWithPermissions.getPost(),
					answerWithPermissions.getPermissions(), parentQuestionId, answerWithPermissions.getPost().getId(),
					IPost.Type.Answer);

			// Add to existing or new list under the parent key
			postParentMap.computeIfAbsent(keys, _ -> new ArrayList<>()).add(newAnswerVM);
		}
	}

	/**
	 * Returns currently visible posts
	 */
	public ObservableList<PostDisplayViewModel> getVisiblePosts() {
		return visiblePosts;
	}

	public PostDisplayViewModel getDirectParentOfAnswer(int answerId) {
		PostDisplayViewModel answerVM = postParentMap.values().stream().flatMap(List::stream)
				.filter(vm -> vm.getPost().getId() == answerId).findFirst().orElse(null);

		if (answerVM == null)
			return null; // not found

		Answer answer = (Answer) answerVM.getPost();
		Integer parentAnswerId = answer.getParentAnswerId();
		int questionId = answer.getQuestionId();

		if (parentAnswerId == null) {
			return getVisiblePostByID(questionId, IPost.Type.Question);
		}

		return postParentMap.values().stream().flatMap(List::stream)
				.filter(vm -> vm.getPost().getId().equals(parentAnswerId)).findFirst().orElse(null);
	}

	/**
	 * Expands or collapses the children of a given post (question or answer).
	 */
	public void toggleExpand(int postIdToToggle, KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair,
			IPost.Type postType) {

		PostDisplayViewModel selectedPostVM = getVisiblePostByID(postIdToToggle, postType);
		if (selectedPostVM == null)
			return;

		int selectedPostIndex = getIndexOfVisiblePost(postIdToToggle, selectedPostVM.isQuestion());
		int nextQuestionIndex = findNextQuestionIndex(selectedPostIndex);

		// Expand or collapse depending on current state
		if (selectedPostVM.isExpandButtonPressed()) {
			expandChildren(selectedPostVM, selectedPostIndex, questionAnswerIDKeyPair);
		} else {
			collapseChildren(selectedPostVM, selectedPostIndex, nextQuestionIndex, questionAnswerIDKeyPair);
		}
	}
	
	private void expandAllVisibleQuestions() {
		new ArrayList<>(visiblePosts).forEach(postVM -> {
		    int postIndex = getIndexOfVisiblePost(postVM.getPost().getId(), true);
		    KeyPairHolder<Integer, Integer> keyPairHolder = new KeyPairHolder<>(postVM.getPost().getId(), null);
		    expandChildren(postVM, postIndex, keyPairHolder);
		});
	}

	/**
	 * Adds a new answer post under a question or another answer.
	 */
		private void addAnswerToVisibleList(Answer answerToAdd) {
		int questionId = answerToAdd.getQuestionId();
		Integer parentAnswerId = answerToAdd.getParentAnswerId();
		IPost.Type parentType = (parentAnswerId == null) ? IPost.Type.Question : IPost.Type.Answer;

		// Default permissions for answers
		List<String> permissions = List.of(RolePermissions.AnswerPermissions.COMMENT.name(),
				RolePermissions.AnswerPermissions.DELETE.name(), RolePermissions.AnswerPermissions.EDIT.name());

		// Build a new post view model
		PostDisplayViewModel newPostVM = buildPost(answerToAdd, permissions, questionId, parentAnswerId,
				IPost.Type.Answer);

		int parentId = Objects.requireNonNullElse(parentAnswerId, questionId);
		PostDisplayViewModel parentVM = getVisiblePostByID(parentId, parentType);
		Integer parentIndex = (parentVM != null) ? getIndexOfVisiblePost(parentId, parentVM.isQuestion()) : null;

		// Set nesting depth and relationships
		newPostVM.setDepthLevel(parentVM.getDepthLevel() + 1);
		parentVM.setDoesHaveChildrenProperty(true);

		// Register new answer under parent
		final KeyPairHolder<Integer, Integer> relationKey = new KeyPairHolder<>(questionId, parentAnswerId);
		postParentMap.computeIfAbsent(relationKey, _ -> new ArrayList<>()).add(newPostVM);

		// Expand the parent to reveal the new answer
		if (parentVM != null) {
			final KeyPairHolder<Integer, Integer> parentKey = new KeyPairHolder<>(questionId, parentAnswerId);
			parentVM.setIsExpanded(true);
			expandChildren(parentVM, parentIndex, parentKey);
		}
	}

	// get questions that are visible to the user [-->Q1<--, A1, A2, -->Q2<--, A1,
	// -->Q3<--, A1, A2]
	public List<Integer> getVisibleQuestionIDs(int[] postsRange) {
		List<Integer> visibleQuestionIDs = new ArrayList<>();

		for (int i = postsRange[0]; i <= postsRange[1]; i++) {
			if (visiblePosts.get(i).isQuestion()) {
				visibleQuestionIDs.add(visiblePosts.get(i).getPost().getId());
			}
		}

		// if only answers are currently visible
		// [(Q1<-- Out of frame), (A1, A2, A3, A4, A5) <-- Visible, (Q2<-- Out of
		// frame)]
		if (visibleQuestionIDs.size() == 0)
			return null;

		return visibleQuestionIDs;
	}

	// ==============================
	// HELPER METHODS
	// ==============================

	// Find a visible post by its ID and type
	private PostDisplayViewModel getVisiblePostByID(int postIdToToggle, IPost.Type postType) {
		return visiblePosts.stream()
				.filter(v -> v.getPost().getId().equals(postIdToToggle) && v.getPost().getPostType() == postType)
				.findFirst().orElse(null);
	}

	// Return the index of a visible post
	private int getIndexOfVisiblePost(int postIdToToggle, boolean isQuestion) {
		return IntStream.range(0, visiblePosts.size())
				.filter(i -> visiblePosts.get(i).getPost().getId().equals(postIdToToggle)
						&& (isQuestion ? visiblePosts.get(i).isQuestion() : !visiblePosts.get(i).isQuestion()))
				.findFirst().orElse(-1);
	}

	// Finds the next question in the visible list (used to know where to stop
	// collapsing)
	private int findNextQuestionIndex(int currentIndex) {
		for (int i = currentIndex + 1; i < visiblePosts.size(); i++) {
			if (visiblePosts.get(i).isQuestion()) {
				return i;
			}
		}
		// If no more questions after current index
		return visiblePosts.size();
	}

	/**
	 * Expands and shows all direct children of a given parent post.
	 */
	private void expandChildren(PostDisplayViewModel parentPost, int visibleParentPostIndex,
			KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair) {

		List<PostDisplayViewModel> children = null;

		switch (currentFilterMode) {
		case NONE:
			children = postParentMap.get(questionAnswerIDKeyPair);
			break;
		case REVIEW:
			children = reviewPostParentMap.get(questionAnswerIDKeyPair);
			break;
		}

		if (children == null)
			return;

		// Optionally sort by creation time if filter is set to TIME
		switch (parentPost.getFilterType()) {
		case TIME:
			children.sort(Comparator.comparing(child -> child.getPost().getCreatedTimestamp(),
					Comparator.nullsFirst(Comparator.naturalOrder())));
		default:
			break;
		}
		// Ensure fresh state
		visiblePosts.removeAll(postParentMap.get(questionAnswerIDKeyPair));

		// Insert each child below the parent in reverse order (so order is preserved)
		for (int i = children.size() - 1; i >= 0; i--) {
			PostDisplayViewModel child = children.get(i);
			
			child.setDepthLevel(parentPost.getDepthLevel() + 1);
			visiblePosts.add(visibleParentPostIndex + 1, child);

			// Determine if child itself has children
			KeyPairHolder<Integer, Integer> childKeys = new KeyPairHolder<>(((Answer) child.getPost()).getQuestionId(),
					child.getPost().getId());

			if (postParentMap.get(childKeys) == null) {
				child.setDoesHaveChildrenProperty(false);
			}
		}
	}

	/**
	 * Collapses (hides) all children of the given post.
	 */
	private void collapseChildren(PostDisplayViewModel currentPostVM, int currentPostIndex, int nextQuestionIndex,
			KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair) {

		int offsetToNextQuestion = nextQuestionIndex - 1;

		// If collapsing an answer (not a question), adjust collapse range
		if (!currentPostVM.isQuestion()) {
			offsetToNextQuestion = postParentMap.get(questionAnswerIDKeyPair).size() + currentPostIndex;
		}

		// Reset expansion state for all children
		List<PostDisplayViewModel> children = postParentMap.get(questionAnswerIDKeyPair);
		if (children != null) {
			for (PostDisplayViewModel child : children) {
				child.setIsExpanded(false);
			}
		}

		// Remove all posts visually between current and next question
		for (int i = offsetToNextQuestion; i > currentPostIndex; i--) {
			visiblePosts.remove(i);
		}
	}

	/**
	 * Creates and configures a PostDisplayViewModel for display. Attaches delete,
	 * add, and update callbacks that interact with services.
	 */
	public PostDisplayViewModel buildPost(IPost post, List<String> permissions, int questionKeyID, Integer answerKeyID,
			IPost.Type postType) {

		PostDisplayViewModel vm = new PostDisplayViewModel(post, permissions, userSession,

				// DELETE callback
				postId -> {
					try {
						if (postType == IPost.Type.Question)
							questionService.deleteQuestion(postId);
						else
							answerService.deleteAnswer(postId);
					} catch (Exception e) {
						e.printStackTrace();
					}
				},

				// ADD callback
				newPost -> {
					try {
						Answer postToAdd = (Answer) newPost;
						Answer addedPost = answerService.addAnswer(postToAdd); // DB call
						addAnswerToVisibleList(addedPost); // Reflect in UI
					} catch (Exception e) {
						e.printStackTrace();
					}
				},

				// UPDATE callback
				(id, mainText, secondaryText) -> {
					try {
						if (postType == IPost.Type.Question) {
							questionService.updateQuestion(id, mainText, secondaryText);
						} else {
							answerService.updateAnswer(id, mainText, secondaryText.equals("yes"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				},

				// EXPAND / COLLAPSE callback
				() -> {
					KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair = new KeyPairHolder<>(questionKeyID,
							answerKeyID);
					this.toggleExpand(post.getId(), questionAnswerIDKeyPair, postType);
				});

		return vm;
	}
}
