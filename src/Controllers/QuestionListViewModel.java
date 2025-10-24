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
			PostDisplayViewModel vm = new PostDisplayViewModel(questionWithPermissions.getPost(),
					questionWithPermissions.getPermissions(), userSession, id -> {
						try {
							answerService.deleteAnswer(id);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}, post -> {
						try {
							answerService.addAnswer((Answer) post);
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
						this.toggleExpand(questionWithPermissions.getPost().getId(),
								new KeyPairHolder<Integer, Integer>(questionWithPermissions.getPost().getId(), null),
								IPost.Type.Question);
					});
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

			PostDisplayViewModel vm = new PostDisplayViewModel(answerWithPermissions.getPost(),
					answerWithPermissions.getPermissions(), userSession, id -> {
						try {
							answerService.deleteAnswer(id);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}, post -> {
						try {
							answerService.addAnswer((Answer) post);
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
						KeyPairHolder<Integer, Integer> holder = new KeyPairHolder<Integer, Integer>(parentQuestionId,
								answerWithPermissions.getPost().getId());
						
						this.toggleExpand(answerWithPermissions.getPost().getId(), holder, IPost.Type.Answer);
					});

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

		PostDisplayViewModel currentPostVM = visiblePosts.stream()
				.filter(v -> v.getPost().getId().equals(postIdToToggle) && v.getPost().getPostType() == postType)
				.findFirst().orElse(null);

		if (currentPostVM == null)
			return;

		int currentPostIndex = IntStream.range(0, visiblePosts.size()).filter(i -> visiblePosts.get(i).getPost().getId()
				.equals(postIdToToggle)
				&& (currentPostVM.isQuestion() ? visiblePosts.get(i).isQuestion() : !visiblePosts.get(i).isQuestion()))
				.findFirst().orElse(-1);

		boolean lookingForNextQuestion = true;
		int indexPointer = currentPostIndex + 1;
		int nextQuestionIndex = indexPointer;

		while (lookingForNextQuestion) {
			// [Q1, A1, A2, --> END/NULL <--] OR [Current Question --> Q1, A1, A2, --> Q2
			// <--, A1]
			if (visiblePosts.get(indexPointer) == null || visiblePosts.get(indexPointer).isQuestion()) {
				nextQuestionIndex = indexPointer;
				break;
			}

			indexPointer++;
		}

		if (currentPostVM.isExpanded()) {
			for (int i = postParentMap.get(questionAnswerIDs).size() - 1; i >= 0; i--) {
				PostDisplayViewModel child = postParentMap.get(questionAnswerIDs).get(i);
				
				child.setDepthLevel(currentPostVM.getDepthLevel() + 1);

				visiblePosts.add(currentPostIndex + 1, child);
				
				int questionId = ((Answer)child.getPost()).getQuestionId();
				int answerId = child.getPost().getId();
				
				KeyPairHolder<Integer, Integer> keys = new KeyPairHolder<Integer, Integer>(questionId, answerId);
				
				// If the leaf comment doesn't have any sub-comments
				if (postParentMap.get(keys) == null) {
					System.out.println("Keys are: " + keys);
					child.setDoesHaveChildren(false);
				}

			}
		} else {
			// [-->Q1<--, A1, A2, -->Q2<--] <-- Distance between the two questions
			int offsetToNextQuestion = nextQuestionIndex - 1;

			if (!currentPostVM.isQuestion()) {
				// [Q1, -->A1<--, A2, -->Q2<--] <-- Distance between answer and next question
				offsetToNextQuestion = postParentMap.get(questionAnswerIDs).size() + currentPostIndex;
			}

			List<PostDisplayViewModel> children = postParentMap.get(questionAnswerIDs);
			if (children != null) {
				for (PostDisplayViewModel child : children) {
					child.setIsExpanded(false);

					Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> subMap = child.isQuestion()
							? postParentMap
							: postParentMap;

					List<PostDisplayViewModel> subChildren = subMap.get(questionAnswerIDs);
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

	}
}
