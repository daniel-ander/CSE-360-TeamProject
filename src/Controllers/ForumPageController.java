package Controllers;

import Models.User;
import Sessions.UserSession;
import Models.QuestionRepository;
import Models.AnswerRepository;
import UI.Pages.ForumPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import UI.Elements.ForumListView;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * ForumPageController manages the Forum page layout and actions
 * such as embedding the forum list and handling logout/role changes.
 */
public class ForumPageController {

    private final DBConnectionStore dBConnectionStore;
    private final IUserDBManager userDBManager;
    private final Stage primaryStage;
    private final ForumPage view;
    private final User user;
    private final UserSession userSession;

    // Forum list components
    private ForumListView forumListView;
    private ForumListViewController forumListController;

    public ForumPageController(DBConnectionStore dBConnectionStore,
                               Stage primaryStage,
                               UserSession userSession,
                               ForumPage view) {
        this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        this.user = userSession.currentUser();
        this.view = view;

        setInitialLayout();
        setLogoutButtonAction();
        setChangeRolesButtonAction();
    }

    private void setLogoutButtonAction() {
        view.getLogoutButton().setOnAction(a -> {
            // Return to login page
            new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
        });
    }

    private void setChangeRolesButtonAction() {
        view.getChangeRolesButton().setOnAction(a -> {
            String role = userDBManager.getUserRole(user.getUserName());
            User newUser = new User(userSession.getCurrentUserName(), "", User.Role.student);
            UserSession userSession = new UserSession(newUser);
            new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
        });
    }

    private void setInitialLayout() {
        view.setInitialLayout(primaryStage);

        // === Embed forum list ===
        QuestionRepository qRepo = new QuestionRepository();   // questions repo
        AnswerRepository aRepo = new AnswerRepository();       // answers repo

        forumListView = new ForumListView();
        forumListController = new ForumListViewController(
            dBConnectionStore,forumListView, qRepo, aRepo, primaryStage, userSession.getCurrentUserName()
        );

        // Place the list view in the center of the page layout
        view.getRoot().setCenter(forumListView.getLayout());
    }
}
