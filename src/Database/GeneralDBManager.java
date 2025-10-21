package Database;

import java.io.File;
import java.sql.*;

import Config.DefaultRolePermissions;
import Models.Answer;
import Models.Invitation;
import Models.Permission;
import Models.Question;
import Models.User;

import Utils.StringUtils;


/**
 * The GeneralDBManager class is responsible for managing the connection to the
 * database, and general actions related to the meta functions of the database
 */

public class GeneralDBManager implements IGeneralDBManager {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:./database/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	
	// CRUD-related
	private final String userTableName = User.tableName;
	private final String questionTableName = Question.tableName;
	private final String answerTableName = Answer.tableName;
	private final String invitationTableName = Invitation.tableName;
	private final String permissionTableName = Permission.tableName;

	// TODO: - Not entirely sure what this is.
	private static int forumPostIdNumber = 0;

	// ===================
	// CREATE
	// ===================

	@Override
	public void initializeDB() throws SQLException {
		try {
			// TODO: - Should probably display an alert if an error arises.
			Class.forName(JDBC_DRIVER); // Necessary for database

			createTables();
			seedData();
		} catch (ClassNotFoundException e) { // feels like this catch belongs here rather than the implementer
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		// TODO: - Invitation codes should probably have a foreign key to the user that
		// created it and used it
		// - or an entirely new table that tracks which user created which invitation
		// code
		
		String userRolesSQL = StringUtils.listToSQLString(User.Role.getAllRoles()); // ["admin", "staff", "student"]
		String questionPermissionSQL = StringUtils.listToSQLString(DefaultRolePermissions.QuestionPermissions.getAllPermissions()); // ["APPROVE", "COMMENT"]
		String answerPermissionSQL = StringUtils.listToSQLString(DefaultRolePermissions.AnswerPermissions.getAllPermissions());

		String userTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "role VARCHAR(225))", userTableName);

		String invitationCodesTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "code VARCHAR(10) PRIMARY KEY, " + "isUsed BOOLEAN DEFAULT FALSE)", invitationTableName);

		String forumQuestionsTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (" + "id IDENTITY PRIMARY KEY, "
				+ "userId INT, " + "title VARCHAR(255), " + "body TEXT, "
				+ "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + "answered BOOLEAN DEFAULT FALSE, "
				+ "resolved BOOLEAN DEFAULT FALSE, " + "isDeleted BOOLEAN DEFAULT FALSE, "
				+ "upvoteCount INT DEFAULT 0, " + "FOREIGN KEY (userId) REFERENCES %s(id))", questionTableName, userTableName);

		String forumAnswersTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s (" + "id IDENTITY PRIMARY KEY, "
				+ "questionId INT, " + "parentAnswerId INT DEFAULT NULL, " + "userId INT, " + "authorName TEXT, " + "body TEXT, "
				+ "isCorrect BOOLEAN DEFAULT FALSE, " + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "isDeleted BOOLEAN DEFAULT FALSE, " + "upvoteCount INT DEFAULT 0, "
				+ "FOREIGN KEY (questionId) REFERENCES %s(id), "
				+ "FOREIGN KEY (parentAnswerId) REFERENCES %s(id), "
				+ "FOREIGN KEY (userId) REFERENCES %s(id))", answerTableName, questionTableName, answerTableName, userTableName);

		String permissionTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s ( " + "id SERIAL PRIMARY KEY, "
				+ "name VARCHAR(20) UNIQUE NOT NULL)", permissionTableName);

		String questionPermissionTableQuery = String.format("CREATE TABLE IF NOT EXISTS question_permissions ( "
				+ "role VARCHAR(20) NOT NULL, " + "permission VARCHAR(20) NOT NULL, "
				+ "PRIMARY KEY (role, permission), "
				+ "CHECK (role IN (%s)), "
				+ "CHECK (permission IN (%s)))", 
					userRolesSQL, questionPermissionSQL);

		String answerPermissionTableQuery = String.format("CREATE TABLE IF NOT EXISTS answer_permissions ( "
				+ "role VARCHAR(20) NOT NULL, " + "permission VARCHAR(20) NOT NULL, "
				+ "PRIMARY KEY (role, permission), "
				+ "CHECK (role IN (%s)), "
				+ "CHECK (permission IN (%s)))", 
					userRolesSQL, answerPermissionSQL);

		String userQuestionPermissionTableQuery = String.format("CREATE TABLE IF NOT EXISTS user_question_permissions ( "
				+ "user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, "
				+ "post_id INTEGER REFERENCES %s(id) ON DELETE CASCADE, "
				+ "permission VARCHAR(20) NOT NULL, " + "is_allowed BOOLEAN NOT NULL, "
				+ "PRIMARY KEY (user_id, post_id, permission), "
				+ "CHECK (permission IN (%s)))", 
					questionTableName, questionPermissionSQL);

		String userAnswerPermissionTableQuery = String.format("CREATE TABLE IF NOT EXISTS user_answer_permissions ( "
				+ "user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, "
				+ "post_id INTEGER REFERENCES %s(id) ON DELETE CASCADE, "
				+ "permission VARCHAR(20) NOT NULL, " + "is_allowed BOOLEAN NOT NULL, "
				+ "PRIMARY KEY (user_id, post_id, permission), "
				+ "CHECK (permission IN (%s)))", 
					answerTableName, answerPermissionSQL);

		try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {

			stmt.execute(userTableQuery);
			stmt.execute(invitationCodesTableQuery);
			stmt.execute(forumQuestionsTableQuery);
			stmt.execute(forumAnswersTableQuery);
			stmt.execute(permissionTableQuery);
			stmt.execute(answerPermissionTableQuery);
			stmt.execute(questionPermissionTableQuery);
			stmt.execute(userQuestionPermissionTableQuery);
			stmt.execute(userAnswerPermissionTableQuery);
		}

	}

	private void seedData() throws SQLException {
		String populateUserTable = String.format("INSERT INTO %s (userName, password, role) VALUES"
				+ "('student', 'Password1!', 'student')," + "('instructor', 'Password1!', 'instructor'),"
				+ "('reviewer', 'Password1!', 'reviewer')," + "('admin1', 'Password1!', 'admin'),"
				+ "('staff', 'Password1!', 'staff');", userTableName);

		String populateQuestionTable = String.format("INSERT INTO %s (userId, title, body, answered, resolved, isDeleted, upvoteCount) VALUES "
				+ "(1, 'What is polymorphism?', 'Can someone explain polymorphism in Java with examples?', FALSE, FALSE, FALSE, 5),"
				+ "(1, 'Difference between == and .equals()', 'I am confused about when to use == vs .equals in Java.', TRUE, TRUE, FALSE, 12),"
				+ "(1, 'Help with ArrayLists', 'How do I remove an element from an ArrayList while iterating?', FALSE, FALSE, FALSE, 3),"
				+ "(1, 'Understanding Threads', 'What is the best way to manage threads in a desktop Java app?', TRUE, FALSE, FALSE, 9),"

				+ "(2, 'Annotation usage in Java', 'When should I use annotations and how do custom annotations work?', FALSE, FALSE, FALSE, 6),"
				+ "(2, 'JavaFX Scene Switching', 'Best practices for switching scenes in JavaFX?', TRUE, TRUE, FALSE, 15),"
				+ "(2, 'Java Memory Model', 'How does the Java memory model affect multithreading?', FALSE, FALSE, FALSE, 4),"
				+ "(2, 'Garbage Collection Insight', 'Can I force garbage collection in Java, and should I?', FALSE, FALSE, FALSE, 2),"

				+ "(3, 'Design Patterns Advice', 'Which design patterns are best for MVC in JavaFX?', TRUE, TRUE, FALSE, 7),"
				+ "(3, 'DAO Pattern Implementation', 'Looking for a simple example of the DAO pattern in Java.', TRUE, FALSE, FALSE, 8),"
				+ "(3, 'What is a NullPointerException?', 'Getting a NPE and not sure how to fix it.', TRUE, TRUE, FALSE, 10),"
				+ "(3, 'Using ObservableList', 'What are the benefits of ObservableList in JavaFX?', FALSE, FALSE, FALSE, 3),"

				+ "(4, 'Admin tools in JavaFX', 'How to implement admin-level access controls in a JavaFX app?', TRUE, FALSE, FALSE, 11),"
				+ "(4, 'Security Best Practices', 'How do I secure login forms in desktop Java apps?', TRUE, TRUE, FALSE, 13),"
				+ "(4, 'SQLite vs H2', 'Which embedded DB is better for a JavaFX app?', FALSE, FALSE, FALSE, 4),"
				+ "(4, 'Crash handling', 'How to catch uncaught exceptions globally in JavaFX?', FALSE, FALSE, FALSE, 6),"

				+ "(5, 'User session persistence', 'How do I keep a user logged in between restarts?', TRUE, TRUE, FALSE, 14),"
				+ "(5, 'MVC vs MVVM', 'Should I use MVVM or MVC for JavaFX projects?', TRUE, FALSE, FALSE, 5),"
				+ "(5, 'Multilingual UI', 'How to implement i18n (internationalization) in JavaFX?', FALSE, FALSE, FALSE, 3),"
				+ "(5, 'Dark mode toggle', 'Best approach for a dark/light theme switch in JavaFX?', FALSE, FALSE, FALSE, 7);", questionTableName);

		String populateAnswersTable = String.format("INSERT INTO %s (questionId, parentAnswerId, userId, authorName, body, isCorrect, upvoteCount) VALUES "
				+ "(1, NULL, 1, 'student', 'Polymorphism allows methods to behave differently based on the object.', FALSE, 4),"
				+ "(1, NULL, 1, 'student', 'Polymorphism is very useful.', FALSE, 4),"
				+ "(1, 1, 2, 'instructor', 'Great explanation! Could you give a code example?', FALSE, 2),"
				+ "(1, 1, 2, 'instructor', 'Very nice!', FALSE, 2),"
				+ "(1, 1, 3, 'reviewer', 'Interesting.', FALSE, 2),"
				+ "(2, NULL, 3, 'reviewer', 'Use .equals() for content comparison, == for reference.', FALSE, 5),"
				+ "(2, 3, 4, 'admin1', 'Thanks for clearing that up!', FALSE, 1),"
				+ "(3, NULL, 2, 'instructor', 'You can use Iterator.remove() to avoid ConcurrentModificationException.', FALSE, 3),"
				+ "(4, NULL, 5, 'staff', 'ExecutorService is a good way to manage threads.', FALSE, 2),"
				+ "(4, 6, 1, 'student', 'Definitely agree, especially with fixed thread pool.', FALSE, 1),"
				+ "(5, NULL, 2, 'instructor', 'Annotations can reduce boilerplate and improve readability.', FALSE, 0),"
				+ "(6, NULL, 3, 'reviewer', 'Use a SceneManager class to manage switching.', FALSE, 6),"
				+ "(7, NULL, 1, 'student', 'The Java Memory Model explains visibility and ordering of threads.', FALSE, 3),"
				+ "(8, NULL, 5, 'staff', 'You can request GC, but you can’t force it.', FALSE, 2),"
				+ "(9, NULL, 4, 'admin1', 'MVC fits well with JavaFX: model handles data, view is FXML.', FALSE, 3),"
				+ "(10, NULL, 3, 'reviewer', 'DAO separates database logic. It keeps code clean.', FALSE, 4),"
				+ "(11, NULL, 2, 'instructor', 'NPE occurs when referencing an object that’s null.', FALSE, 5),"
				+ "(12, NULL, 1, 'student', 'ObservableList notifies listeners when data changes.', FALSE, 2),"
				+ "(13, NULL, 5, 'staff', 'Use roles and guards to control admin-level actions.', FALSE, 4),"
				+ "(14, NULL, 3, 'reviewer', 'Use hashing and HTTPS to secure login.', FALSE, 3),"
				+ "(15, NULL, 4, 'admin1', 'SQLite is lightweight, H2 has good tooling.', FALSE, 2),"
				+ "(16, NULL, 1, 'student', 'Use Thread.setDefaultUncaughtExceptionHandler.', FALSE, 1),"
				+ "(17, NULL, 2, 'instructor', 'Persist sessions using preferences API or local file.', FALSE, 3),"
				+ "(18, NULL, 5, 'staff', 'MVVM is better for testability, but more complex.', FALSE, 2),"
				+ "(19, NULL, 1, 'student', 'Use ResourceBundle for i18n.', FALSE, 4),"
				+ "(20, NULL, 3, 'reviewer', 'Toggle stylesheets on button click for dark mode.', FALSE, 5),"
				+ "(1, 2, 4, 'admin1', 'Example: Dog extends Animal and overrides speak().', FALSE, 1),"
				+ "(2, 4, 1, 'student', 'Code: if(a.equals(b)) instead of if(a == b)', FALSE, 0),"
				+ "(3, 5, 3, 'reviewer', 'Iterating backwards also helps avoid issues.', FALSE, 2),"
				+ "(4, 7, 5, 'staff', 'CachedThreadPool can scale well.', FALSE, 1),"
				+ "(5, 8, 2, 'instructor', '@Override and @FunctionalInterface are helpful ones.', FALSE, 2),"
				+ "(6, 9, 4, 'admin1', 'Use FXMLLoader.load and setController manually if needed.', FALSE, 1),"
				+ "(7, 10, 1, 'student', 'Volatile helps with visibility between threads.', FALSE, 0),"
				+ "(8, 11, 3, 'reviewer', 'GC runs on its own schedule. Avoid calling System.gc().', FALSE, 2),"
				+ "(9, 12, 5, 'staff', 'MVC: Controller acts as middle layer.', FALSE, 3),"
				+ "(10, 13, 2, 'instructor', 'Simple DAO example: interface + JDBC implementation.', FALSE, 2),"
				+ "(11, 14, 4, 'admin1', 'Always check for null before accessing objects.', FALSE, 2),"
				+ "(12, 15, 1, 'student', 'ObservableList makes TableView updates automatic.', FALSE, 1),"
				+ "(13, 16, 5, 'staff', 'Admin can be assigned via roles at login.', FALSE, 4),"
				+ "(14, 17, 2, 'instructor', 'Use BCrypt to hash passwords.', FALSE, 3),"
				+ "(15, 18, 3, 'reviewer', 'SQLite is good for fast prototyping.', FALSE, 2),"
				+ "(16, 19, 4, 'admin1', 'Use Platform.runLater inside uncaughtExceptionHandler.', FALSE, 2),"
				+ "(17, 20, 1, 'student', 'Save tokens or use Java Preferences API.', FALSE, 3);", answerTableName);

		try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {

			stmt.execute(populateUserTable);
			stmt.execute(populateQuestionTable);
			stmt.execute(populateAnswersTable);
		}

	}

	// ===================
	// READ
	// ===================

	@Override
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = String.format("SELECT COUNT(*) AS count FROM %s", userTableName);
		try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
			ResultSet resultSet = stmt.executeQuery(query);

			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}
		return true;
	}

	@Override
	public int getNextForumPostId() {
		forumPostIdNumber += 1;
		return forumPostIdNumber;
		// TODO: This method should be updated to fetch the next ID from the database
		// once the forum posts table is implemented.

	}

	public Connection getConnection() throws SQLException {
		System.out.println("Connecting to database...");
		Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
		connection.setAutoCommit(true); // Enable auto-commit mode
		return connection;
	}
	
	public boolean doesDatabaseExist(String dbPathWithoutExtension) {
        File dbFile = new File(dbPathWithoutExtension + ".mv.db");
        System.out.println("Checking file: " + dbFile.getAbsolutePath());
        return dbFile.exists();
    }
	

}
