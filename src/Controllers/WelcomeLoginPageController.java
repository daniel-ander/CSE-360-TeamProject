package Controllers;

import Models.User;
import UI.Pages.WelcomeLoginPage;
import databasePart1.DatabaseHelper;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WelcomeLoginPageController {
	
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final WelcomeLoginPage view = new WelcomeLoginPage();
    private final User user;

    public WelcomeLoginPageController(DatabaseHelper databaseHelper, Stage primaryStage, User user) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.user = user;
        
        setInitialLayout();
        
        setContinueButtonAction();
        setQuitButtonAction();
        
        checkForAdminPrivilige();
    }
    
    private void setContinueButtonAction() {
    	view.getContinueButton().setOnAction(a -> {
	    	String role =user.getRole();
	    	System.out.println(role);
	    	
	    	if(role.equals("admin")) {
	    		new AdminHomePageController(databaseHelper, primaryStage);
	    		// AdminHomePage(databaseHelper).show(primaryStage)
	    	}
	    	else if(role.equals("user")) {
	    		new UserHomePageController(primaryStage);
	    	}
	    });
	}
    
    private void setQuitButtonAction() {
    	view.getQuitButton().setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private void checkForAdminPrivilige() {
    	if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPageController(databaseHelper, primaryStage);
            });
            view.getLayout().getChildren().add(inviteButton);
        }
    }
}
    