package Controllers;


import UI.Pages.InvitationPage;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPageController {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final InvitationPage view = new InvitationPage();
		
	public InvitationPageController(DatabaseHelper databaseHelper, Stage primaryStage, String userRole) {
		this.databaseHelper = databaseHelper;
	    this.primaryStage = primaryStage;
	        
	    setInitialLayout();
	        
	    setShowCodeButtonAction();
	}
    
    // ===================
 	// Setters
 	// ===================
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private void setShowCodeButtonAction() {
		view.getShowCodeButton().setOnAction(a -> {
			StringBuilder selectedRoles = new StringBuilder();
        	if (view.isAdminCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isStudentCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isInstructorCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isStaffCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isReviewerCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	
        	
			String invitationCode = databaseHelper.generateInvitationCode("");
            view.getInviteCodeLabel().setText(invitationCode);
		});
	}
    
    
}
