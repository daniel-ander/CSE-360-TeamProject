package Controllers;

import Models.User;
import Models.Questions;
import Models.Answers;
import UI.Pages.ForumPage;
import UI.Elements.ForumListView;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * ForumPageController manages the Forum page layout and actions
 * such as embedding the forum list and handling logout/role changes.
 */
public class ForumPageController {

    // References
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final ForumPage view = new ForumPage();
    private final String currentUser;

    // Forum list components
    private ForumListView forumListView;
    private ForumListViewController forumListController;

    public ForumPageController(DatabaseHelper databaseHelper, Stage primaryStage, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;

        setInitialLayout();
        setLogoutButtonAction();
        setChangeRolesButtonAction();
    }

    private void setLogoutButtonAction() {
        view.getLogoutButton().setOnAction(a -> {
            // Return to login page
            new UserLoginPageController(databaseHelper, primaryStage);
        });
    }

    private void setChangeRolesButtonAction() {
        view.getChangeRolesButton().setOnAction(a -> {
            // Return to welcome page
            String role = databaseHelper.getUserRole(currentUser);
            User user = new User(currentUser, "", role); // Password can be blank if not needed
            new WelcomeLoginPageController(databaseHelper, primaryStage, user);
        });
    }

    private void setInitialLayout() {
        view.setInitialLayout(primaryStage);

        // === Embed forum list ===
        Questions qRepo = new Questions();   // questions repo
        Answers aRepo = new Answers();       // answers repo

        forumListView = new ForumListView(); 
        forumListController = new ForumListViewController(
            forumListView, qRepo, aRepo, primaryStage, currentUser);

        // Place the list view in the center of the page layout
        view.getRoot().setCenter(forumListView.getLayout());
    }
}
