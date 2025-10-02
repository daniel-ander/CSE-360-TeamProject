package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import Controllers.FirstPageController;
import Controllers.SetupLoginSelectionPageController;

import UI.Pages.FirstPage;
import UI.Pages.SetupLoginSelectionPage;

import databasePart1.DBConnectionStore;


public class StartCSE360 extends Application {

	
	public static void main( String[] args )
	{
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        try {
        	DBConnectionStore sharedRepositoryProvider = DBConnectionStore.createInstance();
        	sharedRepositoryProvider.getDatabaseHelper().connectToDatabase(); // Connect to the database
        	
            if (sharedRepositoryProvider.getDatabaseHelper().isDatabaseEmpty()) {
            	new FirstPageController(sharedRepositoryProvider, primaryStage, new FirstPage());
            } else {
            	new SetupLoginSelectionPageController(sharedRepositoryProvider, primaryStage, new SetupLoginSelectionPage());
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
    }
	

}
