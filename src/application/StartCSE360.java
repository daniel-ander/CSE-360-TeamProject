package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import Controllers.FirstPageController;
import Controllers.SetupLoginSelectionPageController;

import Database.DBConnectionStore;
import Database.GeneralDBManager;
import Database.Services.PermissionService;
import UI.Pages.FirstPage;
import UI.Pages.SetupLoginSelectionPage;


public class StartCSE360 extends Application {

	
	public static void main( String[] args )
	{
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		String databaseLocation = "database/FoundationDatabase";
		
        try {
        	DBConnectionStore sharedConnectionProvider = DBConnectionStore.createInstance();
        	
        	GeneralDBManager initializingConnectionManager = (GeneralDBManager)sharedConnectionProvider.getGeneralDBManager();
        	PermissionService permissionService = (PermissionService)sharedConnectionProvider.getPermissionService();
        	
        	if(!initializingConnectionManager.doesDatabaseExist(databaseLocation)) {  
            	initializingConnectionManager.initializeDB();
            	permissionService.seedRolePermissions();  													
        	
            	new FirstPageController(sharedConnectionProvider, primaryStage, new FirstPage());
            } else {
            	new SetupLoginSelectionPageController(sharedConnectionProvider, primaryStage, new SetupLoginSelectionPage());
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        } 
    }
	

}
