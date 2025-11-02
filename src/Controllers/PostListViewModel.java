package Controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import Config.DefaultRolePermissions;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Models.Answer;
import Models.IPost;

import Sessions.UserSession;
import Utils.KeyPairHolder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PostListViewModel {

	private final ObservableList<PostDisplayViewModel> visiblePosts = FXCollections.observableArrayList();
	private final Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> postParentMap = new HashMap<>();

	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final UserSession userSession;

	public PostListViewModel(IQuestionService questionService, IAnswerService answerService,
			UserSession userSession) {
		this.questionService = questionService;
		this.answerService = answerService;
		this.userSession = userSession;
	}

	public void refreshQuestions(List<PostWithPermissions> questions) {

	}

	public void populateQuestions(List<PostWithPermissions> questionsWithPermissions) {
		for (PostWithPermissions questionWithPermissions : questionsWithPermissions) {
			// TODO: - Add button action to expand
			PostDisplayViewModel vm = buildPost(questionWithPermissions.getPost(),
					questionWithPermissions.getPermissions(), questionWithPermissions.getPost().getId(), null,
					IPost.Type.Question);

			vm.setDepthLevel(0);

			visiblePosts.add(vm);
		}
	}

	public void populateAnswers(List<PostWithPermissions> answersWithPermissions) {
		for (PostWithPermissions answerWithPermissions : answersWithPermissions) {
			Answer answer = (Answer) answerWithPermissions.getPost();

			int parentQuestionId = answer.getQuestionId();
			Integer parentAnswerId = answer.getParentAnswerId();

			final KeyPairHolder<Integer, Integer> keys = new KeyPairHolder<Integer, Integer>(parentQuestionId,
					parentAnswerId);

			PostDisplayViewModel newAnswerVM = buildPost(answerWithPermissions.getPost(),
					answerWithPermissions.getPermissions(), parentQuestionId, answerWithPermissions.getPost().getId(),
					IPost.Type.Answer);

			if (postParentMap.containsKey(keys)) {
				postParentMap.get(keys).add(newAnswerVM);
			} else {
				// If no record under the keys exists, we initialize a new list and insert it
				List<PostDisplayViewModel> newList = new ArrayList<>();
				newList.add(newAnswerVM);
				postParentMap.put(keys, newList);
			}

		}
	}

	public ObservableList<PostDisplayViewModel> getVisiblePosts() {
		return visiblePosts;
	}

	public void refreshAnswers(List<PostWithPermissions> answers) {
		// build postParentMap and relationships
	}

	public void toggleExpand(int postIdToToggle, KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair,
			IPost.Type postType) {

		PostDisplayViewModel selectedPostVM = getVisiblePostByID(postIdToToggle, postType);

		if (selectedPostVM == null)
			return;

		int selectedPostIndex = getIndexOfVisiblePost(postIdToToggle, selectedPostVM.isQuestion());
		int nextQuestionIndex = findNextQuestionIndex(selectedPostIndex);

		if (selectedPostVM.isExpandButtonPressed()) {
			expandChildren(selectedPostVM, selectedPostIndex, questionAnswerIDKeyPair);
		} else {
			collapseChildren(selectedPostVM, selectedPostIndex, nextQuestionIndex, questionAnswerIDKeyPair);
		}
	}

	public void addPostToVisibleList(Answer answerToAdd, IPost.Type postType) {
		int questionId = answerToAdd.getQuestionId();
		Integer parentAnswerId = answerToAdd.getParentAnswerId();
		IPost.Type parentType = (parentAnswerId == null) ? IPost.Type.Question : IPost.Type.Answer;

		List<String> permissions = List.of(
				DefaultRolePermissions.AnswerPermissions.COMMENT.name(), 
				DefaultRolePermissions.AnswerPermissions.DELETE.name(), 
				DefaultRolePermissions.AnswerPermissions.EDIT.name()
		);

		PostDisplayViewModel newPostVM = buildPost(answerToAdd, permissions, questionId, parentAnswerId,
				IPost.Type.Answer);

		int parentId = Objects.requireNonNullElse(parentAnswerId, questionId);
		PostDisplayViewModel parentVM = getVisiblePostByID(parentId, parentType);
		Integer parentIndex = (parentVM != null) ? getIndexOfVisiblePost(parentId, parentVM.isQuestion()) : null;

		newPostVM.setDepthLevel(parentVM.getDepthLevel() + 1);
		parentVM.setDoesHaveChildren(true);

		final KeyPairHolder<Integer, Integer> relationKey = new KeyPairHolder<>(questionId, parentAnswerId);
		postParentMap.computeIfAbsent(relationKey, _ -> new ArrayList<>()).add(newPostVM);

		if (parentVM != null) {
			final KeyPairHolder<Integer, Integer> parentKey = new KeyPairHolder<>(questionId, parentAnswerId);
			parentVM.setIsExpanded(true);
			expandChildren(parentVM, parentIndex, parentKey);
		} 
	}

	public void updateQuestion(int questionIdToUpdate, String labelText, String contentText) {
		PostDisplayViewModel selectedPostVM = getVisiblePostByID(questionIdToUpdate, IPost.Type.Question);
		selectedPostVM.setLabelTextProperty(labelText);
	}
	
	public void updateAnswer(int answerIdToUpdate, String contentText, boolean isCorrect) {
		PostDisplayViewModel selectedPostVM = getVisiblePostByID(answerIdToUpdate, IPost.Type.Answer);
	}

	// ===================
	// HELPERS
	// ===================

	private PostDisplayViewModel getVisiblePostByID(int postIdToToggle, IPost.Type postType) {
		return visiblePosts.stream()
				.filter(v -> v.getPost().getId().equals(postIdToToggle) && v.getPost().getPostType() == postType)
				.findFirst().orElse(null);
	}

	// [Q1, -->A1<--, A2, Q2] -- currentPostIndex 1 in this case
	private int getIndexOfVisiblePost(int postIdToToggle, boolean isQuestion) {
		return IntStream.range(0, visiblePosts.size()).filter(i -> visiblePosts.get(i).getPost().getId()
				.equals(postIdToToggle)
				&& (isQuestion ? visiblePosts.get(i).isQuestion() : !visiblePosts.get(i).isQuestion()))
				.findFirst().orElse(-1);
	}

	private int findNextQuestionIndex(int currentIndex) {
		for (int i = currentIndex + 1; i < visiblePosts.size(); i++) {
			if (visiblePosts.get(i).isQuestion()) {
				return i;
			}
		}

		return visiblePosts.size();
	}

	private void expandChildren(
			PostDisplayViewModel parentPost, 
			int visibleParentPostIndex,
			KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair
	) {
		List<PostDisplayViewModel> children = postParentMap.get(questionAnswerIDKeyPair);
		
		if (children == null)
			return;
		
		switch(parentPost.getFilterType()) {
		case TIME:
			children.sort(Comparator.comparing(child -> child.getPost().getCreatedTimestamp()));
			// To have the newest ones at the top
			children = children.reversed();
		default:
			break;
		}

		for (int i = children.size() - 1; i >= 0; i--) {
			PostDisplayViewModel child = children.get(i);
			child.setDepthLevel(parentPost.getDepthLevel() + 1);
			visiblePosts.add(visibleParentPostIndex + 1, child);

			KeyPairHolder<Integer, Integer> childKeys = new KeyPairHolder<>(((Answer) child.getPost()).getQuestionId(),
					child.getPost().getId());

			// If child answer doesn't have any sub-answers
			if (postParentMap.get(childKeys) == null) {
				child.setDoesHaveChildren(false);
			}
		}
	}

	private void collapseChildren(PostDisplayViewModel currentPostVM, int currentPostIndex, int nextQuestionIndex,
			KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair) {
		int offsetToNextQuestion = nextQuestionIndex - 1;

		if (!currentPostVM.isQuestion()) {
			// [Q1, -->A1<--, A2, -->Q2<--] <-- Distance between answer and next question
			offsetToNextQuestion = postParentMap.get(questionAnswerIDKeyPair).size() + currentPostIndex;
		}

		List<PostDisplayViewModel> children = postParentMap.get(questionAnswerIDKeyPair);
		if (children != null) {
			for (PostDisplayViewModel child : children) {
				child.setIsExpanded(false);
			}
		}

		for (int i = offsetToNextQuestion; i > currentPostIndex; i--) {
			visiblePosts.remove(i);
		}
	}

	private PostDisplayViewModel buildPost(IPost post, List<String> permissions, int questionKeyID, Integer answerKeyID,
			IPost.Type postType) {
		return new PostDisplayViewModel(
				post, 
				permissions, 
				userSession, 
				postId -> {
					try {
						if (postType == IPost.Type.Question) {
							questionService.deleteQuestion(postId);
						} else {
							answerService.deleteAnswer(postId);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, newPost -> {
					try {
						Answer postToAdd = (Answer) newPost;
						
						// The DB return includes fields like time stamps
						Answer addedAnswer = answerService.addAnswer(postToAdd);
						System.out.println("timestamp: " + addedAnswer.getCreatedTimestamp());
						addPostToVisibleList(addedAnswer, postType);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, (id, mainText, secondaryText) -> {
					try {
						if (postType == IPost.Type.Question) {
							questionService.updateQuestion(id, mainText, secondaryText);
							updateQuestion(id, mainText, secondaryText);
						} else {
							answerService.updateAnswer(id, mainText, secondaryText.equals("yes"));
							updateAnswer(id, mainText, secondaryText.equals("yes"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, () -> {
					KeyPairHolder<Integer, Integer> questionAnswerIDKeyPair = new KeyPairHolder<Integer, Integer>(questionKeyID,
							answerKeyID);

					this.toggleExpand(post.getId(), questionAnswerIDKeyPair, postType);
				});
		}
}
