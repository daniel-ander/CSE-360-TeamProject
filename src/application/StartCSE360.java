package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import Controllers.FirstPageController;
import Controllers.SetupLoginSelectionPageController;

import Database.DBConnectionStore;

import UI.Pages.FirstPage;
import UI.Pages.SetupLoginSelectionPage;


public class StartCSE360 extends Application {

	
	public static void main( String[] args )
	{
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        try {
        	DBConnectionStore sharedServiceProvider = DBConnectionStore.createInstance();
        	sharedServiceProvider.getGeneralDBManager().initializeDBIfNeeded();
        	
            if (sharedServiceProvider.getGeneralDBManager().isDatabaseEmpty()) {
            	new FirstPageController(sharedServiceProvider, primaryStage, new FirstPage());
            } else {
            	new SetupLoginSelectionPageController(sharedServiceProvider, primaryStage, new SetupLoginSelectionPage());
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        } 
    }
	

}
