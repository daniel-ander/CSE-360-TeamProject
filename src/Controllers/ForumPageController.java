package Controllers;

import Database.*;

import Sessions.UserSession;

import UI.Pages.ForumPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import UI.Elements.QuestionListView;

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

    // Forum list components

    public ForumPageController(DBConnectionStore dBConnectionStore,
                               Stage primaryStage,
                               UserSession userSession,
                               ForumPage view) {
        this.dBConnectionStore = dBConnectionStore;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        this.view = view;

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
        
        QuestionListView questionListView = view.getForumListView();
        new QuestionListViewController(
            dBConnectionStore, 
            primaryStage, 
            userSession, 
            new QuestionListViewModel(dBConnectionStore.getQuestionService(), dBConnectionStore.getAnswerService(), userSession), 
            questionListView
        );

        // Place the list view in the center of the page layout
        view.getRoot().setCenter(questionListView.getLayout());
    }
}
