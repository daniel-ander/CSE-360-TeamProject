package application;

import java.util.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	
	private final DatabaseHelper databaseHelper;
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	public AdminHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    ComboBox<String> userDropDown = new ComboBox<>();
	    
	    
	    List<String> users = databaseHelper.getAllUsernames();
	    
	    userDropDown.setPromptText("Select user");
	    userDropDown.getItems().addAll(users);
	    

	    layout.getChildren().addAll(adminLabel, userDropDown);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}