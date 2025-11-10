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

	
	/** Reference to the DatabaseHelper for database interactions */
	private final IInvitationService invitationService;
	private final Stage primaryStage;
	private final InvitationPage view;
		
    /**
     * Creates a new InvitationPageController.
     * Initializes the layout and sets up the button action for generating codes.
     */
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
    
    /**
     * Sets up the initial layout of the invitation page.
     */
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    /**
     * Configures the action for the "Show Code" button.
     * Builds a role string based on selected checkboxes, generates an invitation code,
     * and displays it in the view.
     */
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