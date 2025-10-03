package Controllers;

import Models.User;
import Models.Questions;
import Models.Answers;
import UI.Pages.ForumPage;
import UI.Pages.UserLoginPage;
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
    private final String currentUserName;

    // Forum list components
    private ForumListView forumListView;
    private ForumListViewController forumListController;

    public ForumPageController(DBConnectionStore dBConnectionStore,
                               Stage primaryStage,
                               String currentUserName,
                               ForumPage view) {
        this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
        this.primaryStage = primaryStage;
        this.currentUserName = currentUserName;
        this.view = view;

        setInitialLayout();
        setLogoutButtonAction();
        setChangeRolesButtonAction();
    }

    private void setLogoutButtonAction() {
        view.getLogoutButton().setOnAction(a -> {
            // Return to login page
            new UserLoginPageController(dBConnectionStore, primaryStage, currentUserName, new UserLoginPage());
        });
    }

    private void setChangeRolesButtonAction() {
        view.getChangeRolesButton().setOnAction(a -> {
            String role = userDBManager.getUserRole(currentUserName);
            User user = new User(currentUserName, "", role); // password blank if not needed
            new WelcomeLoginPageController(dBConnectionStore, primaryStage, user);
        });
    }

    private void setInitialLayout() {
        view.setInitialLayout(primaryStage);

        // === Embed forum list ===
        Questions qRepo = new Questions();   // questions repo
        Answers aRepo = new Answers();       // answers repo

        forumListView = new ForumListView();
        forumListController = new ForumListViewController(
            forumListView, qRepo, aRepo, primaryStage, currentUserName
        );

        // Place the list view in the center of the page layout
        view.getRoot().setCenter(forumListView.getLayout());
    }
}
