package Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Models.Answer;
import Models.IPost;
import Sessions.UserSession;
import Utils.KeyPairHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QuestionListViewModel {

	private final ObservableList<PostDisplayViewModel> visiblePosts = FXCollections.observableArrayList();
	private final Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> postParentMap = new HashMap<>();

	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final UserSession userSession;

	public QuestionListViewModel(IQuestionService questionService, IAnswerService answerService,
			UserSession userSession) {
		this.questionService = questionService;
		this.answerService = answerService;
		this.userSession = userSession;
	}

	public void refreshQuestions(List<PostWithPermissions> questions) {
//        visiblePosts.clear();
//        for (PostWithPermissions q : questions) {
//            var vm = new PostDisplayViewModel(...);
//            vm.setDepthLevel(0);
//            visiblePosts.add(vm);
//        }
	}

	public void populateQuestions(List<PostWithPermissions> questionsWithPermissions) {
		for (PostWithPermissions questionWithPermissions : questionsWithPermissions) {
			// TODO: - Add button action to expand
			PostDisplayViewModel vm = buildPost(
					questionWithPermissions.getPost(),
					questionWithPermissions.getPermissions(),
					questionWithPermissions.getPost().getId(),
					null,
					IPost.Type.Question
			);

			vm.setDepthLevel(0);

			visiblePosts.add(vm);
		}
	}

	public void populateAnswers(List<PostWithPermissions> answersWithPermissions) {
		for (PostWithPermissions answerWithPermissions : answersWithPermissions) {
			Answer answer = (Answer) answerWithPermissions.getPost();

			int parentQuestionId = answer.getQuestionId();
			final Integer parentAnswerId = answer.getParentAnswerId();

			final KeyPairHolder<Integer, Integer> keys = new KeyPairHolder<Integer, Integer>(parentQuestionId,
					parentAnswerId);

			PostDisplayViewModel vm = buildPost(
					answerWithPermissions.getPost(),
					answerWithPermissions.getPermissions(),
					parentQuestionId,
					answerWithPermissions.getPost().getId(),
					IPost.Type.Answer
			);

			if (postParentMap.containsKey(keys)) {
				postParentMap.get(keys).add(vm);
			} else {
				List<PostDisplayViewModel> newList = new ArrayList<>();
				newList.add(vm);
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

	public void toggleExpand(int postIdToToggle, KeyPairHolder<Integer, Integer> questionAnswerIDs,
			IPost.Type postType) {

		PostDisplayViewModel currentPostVM = getVisiblePostByID(postIdToToggle, postType);

		if (currentPostVM == null)
			return;
	
		int currentPostIndex = getIndexOfVisiblePost(postIdToToggle, currentPostVM);
		int nextQuestionIndex = findNextQuestionIndex(currentPostIndex);
		
		if (currentPostVM.isExpanded()) {
	        expandChildren(currentPostVM, currentPostIndex, questionAnswerIDs);
	    } else {
	        collapseChildren(currentPostVM, currentPostIndex, nextQuestionIndex, questionAnswerIDs);
	    }
	}
	
	// ===================
    // 		HELPERS 
 	// ===================
	
	private PostDisplayViewModel getVisiblePostByID(int postIdToToggle, IPost.Type postType) {
		return visiblePosts.stream()
				.filter(v -> v.getPost().getId().equals(postIdToToggle) && v.getPost().getPostType() == postType)
				.findFirst().orElse(null);
	}
	
	// [Q1, -->A1<--, A2, Q2] -- currentPostIndex 1 in this case 
	private int getIndexOfVisiblePost(int postIdToToggle, PostDisplayViewModel selectedPostVM) {
		return IntStream.range(0, visiblePosts.size()).filter(i -> visiblePosts.get(i).getPost().getId()
				.equals(postIdToToggle)
				&& (selectedPostVM.isQuestion() ? visiblePosts.get(i).isQuestion() : !visiblePosts.get(i).isQuestion()))
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
	
	private void expandChildren(PostDisplayViewModel parent, int parentIndex, KeyPairHolder<Integer, Integer> keys) {
	    List<PostDisplayViewModel> children = postParentMap.get(keys);
	    if (children == null) return;

	    for (int i = children.size() - 1; i >= 0; i--) {
	        PostDisplayViewModel child = children.get(i);
	        child.setDepthLevel(parent.getDepthLevel() + 1);
	        visiblePosts.add(parentIndex + 1, child);

	        KeyPairHolder<Integer, Integer> childKeys = new KeyPairHolder<>(
	                ((Answer) child.getPost()).getQuestionId(),
	                child.getPost().getId()
	        );
	        
	        // If child answer doesn't have any sub-answers
	        if (postParentMap.get(childKeys) == null) {
	            child.setDoesHaveChildren(false);
	        }
	    }
	}
	
	

	private void collapseChildren(PostDisplayViewModel currentPostVM, int currentPostIndex, int nextQuestionIndex,
	                              KeyPairHolder<Integer, Integer> keys) {
		int offsetToNextQuestion = nextQuestionIndex - 1;

		if (!currentPostVM.isQuestion()) {
			// [Q1, -->A1<--, A2, -->Q2<--] <-- Distance between answer and next question
			offsetToNextQuestion = postParentMap.get(keys).size() + currentPostIndex;
		}

		List<PostDisplayViewModel> children = postParentMap.get(keys);
		if (children != null) {
			for (PostDisplayViewModel child : children) {
				child.setIsExpanded(false);

				Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> subMap = child.isQuestion()
						? postParentMap
						: postParentMap;

				List<PostDisplayViewModel> subChildren = subMap.get(keys);
				if (subChildren != null) {
					for (PostDisplayViewModel subChild : subChildren) {
						subChild.setIsExpanded(false);
					}
				}
			}
		}

		for (int i = offsetToNextQuestion; i > currentPostIndex; i--) {
			visiblePosts.remove(i);
		}
	}
	
	private PostDisplayViewModel buildPost(
			IPost post, 
			List<String> permissions, 
			int questionKeyID, 
			Integer answerKeyID, 
			IPost.Type postType
	) {
		return new PostDisplayViewModel(
				post,
				permissions, 
				userSession, 
				id -> {
					try {
						answerService.deleteAnswer(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, newPost -> {
					try {
						answerService.addAnswer((Answer) newPost);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, (id, newBody, isCorrect) -> {
					try {
						answerService.updateAnswer(id, newBody, isCorrect.equals("yes"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, () -> {
					KeyPairHolder<Integer, Integer> questionAnswerKeyPair = new KeyPairHolder<Integer, Integer>(questionKeyID,
							answerKeyID);
					
					this.toggleExpand(post.getId(), questionAnswerKeyPair, postType);
				});
	}
}
