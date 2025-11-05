package Controllers;

import java.util.ArrayList;
import java.util.List;

import Config.AppCache;
import Database.*;
import DisplayModels.PostWithPermissions;
import Sessions.UserSession;

import UI.Pages.ForumPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import Utils.AnswerPreloadPriority;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * ForumPageController manages the Forum page layout and actions
 * such as embedding the forum list and handling logout/role changes.
 */
public class ForumPageController {

    private final DBConnectionStore dBConnectionStore;
    private final Stage primaryStage;
    private final ForumPage view;
    private final UserSession userSession;
    private final PostListViewModel postListViewModel;

    // Forum list components

    public ForumPageController(
    		DBConnectionStore dBConnectionStore,
            Stage primaryStage,
            UserSession userSession,
            ForumPage view,
            PostListViewModel postListViewModel
        ) {
        this.dBConnectionStore = dBConnectionStore;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        this.view = view;
        this.postListViewModel = postListViewModel;

        setInitialLayout();
        setLogoutButtonAction();
        setChangeRolesButtonAction();
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================

    private void setLogoutButtonAction() {
        view.getLogoutButton().setOnAction(_ -> {
            // Return to login page
            new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
        });
    }

    private void setChangeRolesButtonAction() {
        view.getChangeRolesButton().setOnAction(_ -> {
        	
        	// TODO: - Deal with role setting and persistence in regard to it
//            String role = userRepository.getUserRole(userSession);
//            User user = new User(userSession, "", role); // password blank if not needed

            new WelcomeLoginPageController(
            		dBConnectionStore, 
            		primaryStage, 
            		userSession,
            		new WelcomeLoginPage()
            	);
        });
    }

    private void setInitialLayout() {
        view.setInitialLayout(primaryStage);

//        // Initialize the embedded list view + view model
//        questionListView = view.getForumListView();
//        postListViewModel = new PostListViewModel(
//                dBConnectionStore.getQuestionService(),
//                dBConnectionStore.getAnswerService(),
//                userSession
//        );

        initializeQuestionListListeners();
        setQuestionListActions();

        view.setGetVisibleQuestionIDsCallback(visiblePostsIndexRange -> {
            var visibleQuestionIDs = postListViewModel.getVisibleQuestionIDs(visiblePostsIndexRange);
            if (visibleQuestionIDs == null) return;

            AnswerPreloadPriority.resetPriorityList();
            visibleQuestionIDs.forEach(AnswerPreloadPriority::addPriorityQuestion);
        });

        // Attach to page layout
//        view.getRoot().setCenter(view.getLayout());
    }
    
    private void initializeQuestionListListeners() {
        List<PostWithPermissions> initialQuestions = new ArrayList<>();

        if (AppCache.getInstance().areInitialQuestionsLoaded()) {
            initialQuestions.addAll(AppCache.getInstance().getInitialQuestionData().get());
            postListViewModel.populateQuestions(initialQuestions);
            view.getForumListView().setItems(postListViewModel.getVisiblePosts());
        }

        if (AppCache.getInstance().areInitialAnswersLoaded()) {
            postListViewModel.addToAnswerPool(AppCache.getInstance().getAllInitialAnswersDataProperty().get());
        }

        AppCache.getInstance().getInitialQuestionData().addListener((_, _, questionsWithPermissions) -> {
            if (questionsWithPermissions == null) return;

            Platform.runLater(() -> {
                if (!initialQuestions.equals(questionsWithPermissions)) {
                    postListViewModel.populateQuestions(questionsWithPermissions);
                }
                view.getForumListView().setItems(postListViewModel.getVisiblePosts());
            });
        });

        AppCache.getInstance().getAnswersDataProperty().addListener((_, _, answersWithPermissions) -> {
            if (answersWithPermissions == null) return;

            Platform.runLater(() -> {
                List<PostWithPermissions> newAnswers = new ArrayList<>();
                for (PostWithPermissions answer : answersWithPermissions) {
                    boolean exists = AppCache.getInstance()
                            .getAllInitialAnswersDataProperty()
                            .get()
                            .stream()
                            .anyMatch(a -> a.getPost().getId() == answer.getPost().getId());
                    if (exists) newAnswers.add(answer);
                }
                AppCache.getInstance().setAnswersDataProperty(newAnswers);
                postListViewModel.addToAnswerPool(newAnswers);
            });
        });
    }
    
    private void setQuestionListActions() {
        view.getSearchButton().setOnAction(_ -> {
            String searchTerm = view.getSearchQuestionsTextField().getText();
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                initializeQuestionListListeners();
                return;
            }

            String term = searchTerm.toLowerCase();
            try {
                ObservableList<PostDisplayViewModel> currentItems = view.getForumListView().getItems();
                ObservableList<PostDisplayViewModel> filteredItems = currentItems.filtered(vm ->
                        vm.getPost().getTitleText().toLowerCase().contains(term)
                                || vm.getPost().getBodyText().toLowerCase().contains(term));
                view.getForumListView().setItems(filteredItems);
            } catch (Exception e) {
                System.err.println("Error during search: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }



}
