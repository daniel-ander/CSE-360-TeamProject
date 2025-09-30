package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import Controllers.FirstPageController;
import Controllers.SetupLoginSelectionPageController;

import databasePart1.RepositoryProvider;


public class StartCSE360 extends Application {

	
	public static void main( String[] args )
	{
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        try {
        	RepositoryProvider sharedRepositoryProvider = RepositoryProvider.createInstance();
        	sharedRepositoryProvider.getDatabaseHelper().connectToDatabase(); // Connect to the database
        	
            if (sharedRepositoryProvider.getDatabaseHelper().isDatabaseEmpty()) {
            	new FirstPageController(sharedRepositoryProvider, primaryStage);
            } else {
            	new SetupLoginSelectionPageController(sharedRepositoryProvider, primaryStage);
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
    }
	

}
