package Controllers;

import UI.Elements.MenuBarBuilder;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

/**
 * The MenuBarBuilderController class manages the actions for a menu bar.
 * It provides functionality for exit, about, and logout menu items.
 * It is currently unimplemented
 */
public class MenuBarBuilderController {
	
	//TODO: Had this idea for a menu bar at the top, we'll have to update all of the pages/controllers
	// unless we want to add it globally, i dont know the best way to handle this if we want it
	
	public MenuBarBuilderController(MenuBarBuilder menuBarBuilder, Stage primaryStage) {
		
		menuBarBuilder.getExitMenuItem().setOnAction(e -> primaryStage.close());
		menuBarBuilder.getAboutMenuItem().setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("About This Application");
			alert.setContentText("This application was developed as part of the CSE 360 Team Project.\n\n"
					+ "Team Members:\n"
					+ "- Daniel 1\n"
					+ "- Trev 2\n"
					+ "- Jeka 3\n"
					+ "- Tracy 4\n\n"
					+ "Version: 1.0.0\n"
					+ "© 2025 CSE 360 Team Project");
			alert.showAndWait();
		});
		
		menuBarBuilder.getLogoutMenuItem().setOnAction(e -> {
			// Implement logout functionality here
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Logout");
			alert.setHeaderText(null);
			alert.setContentText("Logout functionality is not yet implemented.");
			alert.showAndWait();
		});
	}
	
}
