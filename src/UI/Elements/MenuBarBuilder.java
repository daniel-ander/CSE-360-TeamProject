package UI.Elements;

import javafx.scene.control.*;


/**
 * MenuBarBuilder is a util class that makes the standard menu bar at the top of the application
 * 
 */

public class MenuBarBuilder {
	
	private final MenuBar menuBar;
	private final MenuItem logoutMenuItem;
	private final MenuItem exitMenuItem;
	private final MenuItem aboutMenuItem;
	
	public MenuBarBuilder() {
		menuBar = new MenuBar();
		
		// File Menu
		Menu fileMenu = new Menu("File");
		logoutMenuItem = new MenuItem("Logout");
		exitMenuItem = new MenuItem("Exit");
		fileMenu.getItems().addAll(logoutMenuItem, exitMenuItem);
		
		// Help Menu
		Menu helpMenu = new Menu("Help");
		aboutMenuItem = new MenuItem("About");
		helpMenu.getItems().add(aboutMenuItem);
		
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		
	}
		
	public MenuBar getMenuBar() {
		return menuBar;
	}
	public MenuItem getLogoutMenuItem() {
		return logoutMenuItem;
	}
	public MenuItem getExitMenuItem() {
		return exitMenuItem;
	}
	public MenuItem getAboutMenuItem() {
		return aboutMenuItem;
	}
	
}
