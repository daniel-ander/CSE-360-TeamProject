package Controllers;

import Database.*;
import Database.Services.Interfaces.IInvitationService;

import Exceptions.DBExceptions.DataAccessException;

import UI.Pages.InvitationPage;

import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPageController {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param generalDBManager An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	// Reference to the DatabaseHelper for database interactions
	private final IInvitationService invitationService;
	private final Stage primaryStage;
	private final InvitationPage view;
		
	public InvitationPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, InvitationPage view) {
		this.invitationService = dBConnectionStore.getInvitationService();
	    this.primaryStage = primaryStage;
	    this.view = view;    
	    
	    setInitialLayout();
	        
	    setShowCodeButtonAction();
	}
    
	// ===================
    // 		 SETTERS 
 	// ===================
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private void setShowCodeButtonAction() {
		view.getShowCodeButton().setOnAction(_ -> {
			StringBuilder selectedRoles = new StringBuilder();
        	if (view.isAdminCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isStudentCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isInstructorCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isStaffCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (view.isReviewerCheckBoxSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	
        	try {
        		String invitationCode = invitationService.generateInvitationCode(selectedRoles.toString());
        		view.getInviteCodeLabel().setText(invitationCode);
        	} catch (DataAccessException e) {
        		// TODO: - Handle invitation code generation failure
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
		});
	}
    
    
}