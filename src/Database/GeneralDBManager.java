package Database;

import java.io.File;
import java.sql.*;

import Models.Answer;
import Models.Invitation;
import Models.Message;
import Models.Question;
import Models.User;
import Models.DBModels.FavoredReviewer;
import Models.DBModels.Permission;
import Models.DBModels.Rating;
import Utils.RolePermissions;
import Utils.StringUtils;


/**
 * The GeneralDBManager class is responsible for managing the connection to the
 * database, and general actions related to the meta functions of the database
 */
public class GeneralDBManager implements IGeneralDBManager {

	/** JDBC driver name and database URL*/
	static final String JDBC_DRIVER = "org.h2.Driver";
	/** JDBC driver name and database URL*/
	static final String DB_URL = "jdbc:h2:./database/FoundationDatabase";

	/** Database credentials */
	static final String USER = "sa";
	/** Database credentials */
	static final String PASS = "";
	
	// CRUD-related
	private final String userTableName = User.tableName;
	private final String interactionTableName = Message.tableName;
	private final String questionTableName = Question.tableName;
	private final String answerTableName = Answer.tableName;
	private final String invitationTableName = Invitation.tableName;
	private final String permissionTableName = Permission.tableName;
	private final String favoredReviewerTableName = FavoredReviewer.tableName;
	private final String ratingTableName = Rating.tableName;

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
		String questionPermissionSQL = StringUtils.listToSQLString(RolePermissions.QuestionPermissions.getAllPermissions()); // ["APPROVE", "COMMENT"]
		String answerPermissionSQL = StringUtils.listToSQLString(RolePermissions.AnswerPermissions.getAllPermissions());

		String userTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS %s (" 
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " 
				+ "password VARCHAR(255), " 
				+ "role VARCHAR(225))", 
				userTableName
		);

		String invitationCodesTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS %s ("
				+ "code VARCHAR(10) PRIMARY KEY, " 
				+ "isUsed BOOLEAN DEFAULT FALSE)", 
				invitationTableName
		);

		String forumQuestionsTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS %s (" + "id IDENTITY PRIMARY KEY, "
				+ "userId INT, " 
				+ "title VARCHAR(255), " 
				+ "body TEXT, "
				+ "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " 
				+ "answered BOOLEAN DEFAULT FALSE, "
				+ "resolved BOOLEAN DEFAULT FALSE, " 
				+ "isDeleted BOOLEAN DEFAULT FALSE, "
				+ "upvoteCount INT DEFAULT 0, " 
				+ "FOREIGN KEY (userId) REFERENCES %s(id))", 
				questionTableName, userTableName
		);

		String forumAnswersTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS %s (" 
				+ "id IDENTITY PRIMARY KEY, "
				+ "questionId INT, " 
				+ "parentAnswerId INT DEFAULT NULL, " 
				+ "userId INT, " 
				+ "body TEXT, "
				+ "isCorrect BOOLEAN DEFAULT FALSE, " 
				+ "isReview BOOLEAN DEFAULT FALSE, " 
				+ "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "isDeleted BOOLEAN DEFAULT FALSE, " 
				+ "averageRating DECIMAL(3,2) NOT NULL DEFAULT 0, " 
				+ "upvoteCount INT DEFAULT 0, "
				+ "FOREIGN KEY (questionId) REFERENCES %s(id), "
				+ "FOREIGN KEY (parentAnswerId) REFERENCES %s(id), "
				+ "FOREIGN KEY (userId) REFERENCES %s(id))", 
				answerTableName, questionTableName, answerTableName, userTableName
		);

		String permissionTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS %s ( " 
				+ "id SERIAL PRIMARY KEY, "
				+ "name VARCHAR(20) UNIQUE NOT NULL)", 
				permissionTableName
		);

		String questionPermissionTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS question_permissions ( "
				+ "role VARCHAR(20) NOT NULL, " + "permission VARCHAR(20) NOT NULL, "
				+ "PRIMARY KEY (role, permission), "
				+ "CHECK (role IN (%s)), "
				+ "CHECK (permission IN (%s)))", 
					userRolesSQL, questionPermissionSQL
		);

		String answerPermissionTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS answer_permissions ( "
				+ "role VARCHAR(20) NOT NULL, " + "permission VARCHAR(20) NOT NULL, "
				+ "PRIMARY KEY (role, permission), "
				+ "CHECK (role IN (%s)), "
				+ "CHECK (permission IN (%s)))", 
					userRolesSQL, answerPermissionSQL
		);

		String userQuestionPermissionTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS user_question_permissions ( "
				+ "user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, "
				+ "post_id INTEGER REFERENCES %s(id) ON DELETE CASCADE, "
				+ "permission VARCHAR(20) NOT NULL, " + "is_allowed BOOLEAN NOT NULL, "
				+ "PRIMARY KEY (user_id, post_id, permission), "
				+ "CHECK (permission IN (%s)))", 
					questionTableName, questionPermissionSQL
		);

		String userAnswerPermissionTableQuery = String.format(
				  "CREATE TABLE IF NOT EXISTS user_answer_permissions ( "
				+ "user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, "
				+ "post_id INTEGER REFERENCES %s(id) ON DELETE CASCADE, "
				+ "permission VARCHAR(20) NOT NULL, " + "is_allowed BOOLEAN NOT NULL, "
				+ "PRIMARY KEY (user_id, post_id, permission), "
				+ "CHECK (permission IN (%s)))", 
				answerTableName, answerPermissionSQL
		);
		
		String messageTableQuery = String.format(
			    "CREATE TABLE IF NOT EXISTS %s ("
			  + "message_id IDENTITY PRIMARY KEY, "
			  + "review_id INT, "
			  + "sender_id INT, "
			  + "receiver_id INT, "
			  + "parent_message_id INT, "
			  + "message_text TEXT, "
			  + "sent_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			  + "FOREIGN KEY (review_id) REFERENCES %s(id), "
			  + "FOREIGN KEY (sender_id) REFERENCES %s(id), "
			  + "FOREIGN KEY (receiver_id) REFERENCES %s(id), "
			  + "FOREIGN KEY (parent_message_id) REFERENCES %s(message_id))",
			  interactionTableName, answerTableName, userTableName, userTableName, interactionTableName
			);
		
		// reviewer requests table for students requesting reviewer role
		String reviewerRequestsTableQuery = 
			  "CREATE TABLE IF NOT EXISTS reviewer_requests ("
			+ "request_id IDENTITY PRIMARY KEY, "
			+ "student_id INT, "
			+ "instructor_id INT, "
			+ "status VARCHAR(20) DEFAULT 'PENDING', "
			+ "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			+ "FOREIGN KEY (student_id) REFERENCES " + userTableName + "(id), "
			+ "FOREIGN KEY (instructor_id) REFERENCES " + userTableName + "(id))";
		
		String favoredReviewerTableQuery = String.format(
			    "CREATE TABLE IF NOT EXISTS %s ("
			  + "id INT AUTO_INCREMENT PRIMARY KEY, "
			  + "userId INT NOT NULL, "
			  + "favoredReviewerId INT NOT NULL, "
			  + "FOREIGN KEY (userId) REFERENCES %s(id) ON DELETE CASCADE, "
			  + "FOREIGN KEY (favoredReviewerId) REFERENCES %s(id) ON DELETE CASCADE, "
			  + "UNIQUE (userId, favoredReviewerId))",
			  favoredReviewerTableName, userTableName, userTableName
		);
		
		String answerRatingsTableQuery = String.format(
			    "CREATE TABLE IF NOT EXISTS %s ("
			  + "id IDENTITY PRIMARY KEY, "
			  + "userId INT NOT NULL, "
			  + "answerId INT NOT NULL, "
			  + "rating DECIMAL(3,2) NOT NULL, "
			  + "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			  + "CHECK (rating BETWEEN 0 AND 1), " 
			  + "FOREIGN KEY (userId) REFERENCES %s(id) ON DELETE CASCADE, "
			  + "FOREIGN KEY (answerId) REFERENCES %s(id) ON DELETE CASCADE, "
			  + "UNIQUE (userId, answerId))",
			    ratingTableName, userTableName, answerTableName
		);

		
			);
		
		String adminRequestsTableQuery =
			    "CREATE TABLE IF NOT EXISTS admin_requests ("
			  + "id IDENTITY PRIMARY KEY, "
			  + "description VARCHAR(255) NOT NULL, "
			  + "is_closed BOOLEAN DEFAULT FALSE, "
			  + "original_request_id INT"
			  + ")";




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
			stmt.execute(messageTableQuery);
			stmt.execute(reviewerRequestsTableQuery);
			stmt.execute(favoredReviewerTableQuery);
			stmt.execute(answerRatingsTableQuery);
			stmt.execute(adminRequestsTableQuery);
		}

	}

	private void seedData() throws SQLException {
		String populateUserTable = String.format("INSERT INTO %s (userName, password, role) VALUES"
				+ "('student', 'Password1!', 'student')," + "('student2', 'Password1!', 'student')," 
				+ "('student3', 'Password1!', 'student')," + "('reviewer', 'Password1!', 'reviewer')," 
				+ "('instructor', 'Password1!', 'instructor'),"
				+ "('reviewer2', 'Password1!', 'reviewer'),"
				+ "('reviewer3', 'Password1!', 'reviewer')," + "('admin1', 'Password1!', 'admin'),"
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

		String populateAnswersTable = String.format("""
			    INSERT INTO %s (questionId, parentAnswerId, userId, body, isCorrect, isReview) VALUES
			    (1, NULL, 1, 'Polymorphism allows methods to behave differently based on the object.', FALSE, FALSE),
			    (1, NULL, 3, 'Review.', FALSE, TRUE),
			    (1, NULL, 1, 'Polymorphism is very useful.', FALSE, FALSE),
			    (1, 1, 2, 'Great explanation! Could you give a code example?', FALSE, FALSE),
			    (1, 1, 2, 'Very nice!', FALSE, FALSE),
			    (1, 1, 3, 'Interesting.', FALSE, FALSE),
			    (2, NULL, 3, 'Use .equals() for content comparison, == for reference.', FALSE, FALSE),
			    (2, NULL, 3, 'Review.', FALSE, TRUE),
			    (2, 3, 4, 'Thanks for clearing that up!', FALSE, FALSE),
			    (2, 6, 4, 'Second one!', FALSE, FALSE),
			    (3, NULL, 2, 'You can use Iterator.remove() to avoid ConcurrentModificationException.', FALSE, FALSE),
			    (4, NULL, 5, 'ExecutorService is a good way to manage threads.', FALSE, FALSE),
			    (4, 6, 1, 'Definitely agree, especially with fixed thread pool.', FALSE, FALSE),
			    (5, NULL, 2, 'Annotations can reduce boilerplate and improve readability.', FALSE, FALSE),
			    (6, NULL, 3, 'Use a SceneManager class to manage switching.', FALSE, FALSE),
			    (7, NULL, 1, 'The Java Memory Model explains visibility and ordering of threads.', FALSE, FALSE),
			    (8, NULL, 5, 'You can request GC, but you can’t force it.', FALSE, FALSE),
			    (9, NULL, 4, 'MVC fits well with JavaFX: model handles data, view is FXML.', FALSE, FALSE),
			    (10, NULL, 3, 'Review.', FALSE, TRUE),
			    (11, NULL, 2, 'NPE occurs when referencing an object that’s null.', FALSE, FALSE),
			    (12, NULL, 1, 'ObservableList notifies listeners when data changes.', FALSE, FALSE),
			    (13, NULL, 5, 'Use roles and guards to control admin-level actions.', FALSE, FALSE),
			    (14, NULL, 3, 'Review.', FALSE, TRUE),
			    (15, NULL, 4, 'SQLite is lightweight, H2 has good tooling.', FALSE, FALSE),
			    (16, NULL, 1, 'Use Thread.setDefaultUncaughtExceptionHandler.', FALSE, FALSE),
			    (17, NULL, 2, 'Persist sessions using preferences API or local file.', FALSE, FALSE),
			    (18, NULL, 5, 'MVVM is better for testability, but more complex.', FALSE, FALSE),
			    (19, NULL, 1, 'Use ResourceBundle for i18n.', FALSE, FALSE),
			    (20, NULL, 3, 'Review.', FALSE, TRUE),
			    (1, 2, 4, 'Example: Dog extends Animal and overrides speak().', FALSE, FALSE),
			    (2, 4, 1, 'Code: if(a.equals(b)) instead of if(a == b)', FALSE, FALSE),
			    (3, 5, 3, 'Iterating backwards also helps avoid issues.', FALSE, FALSE),
			    (4, 7, 5, 'CachedThreadPool can scale well.', FALSE, FALSE),
			    (5, 8, 2, '@Override and @FunctionalInterface are helpful ones.', FALSE, FALSE),
			    (6, 9, 4, 'Use FXMLLoader.load and setController manually if needed.', FALSE, FALSE),
			    (7, 10, 1, 'Volatile helps with visibility between threads.', FALSE, FALSE),
			    (8, 11, 3, 'GC runs on its own schedule. Avoid calling System.gc().', FALSE, FALSE),
			    (9, 12, 5, 'MVC: Controller acts as middle layer.', FALSE, FALSE),
			    (10, 13, 2, 'Simple DAO example: interface + JDBC implementation.', FALSE, FALSE),
			    (11, 14, 4, 'Always check for null before accessing objects.', FALSE, FALSE),
			    (12, 15, 1, 'ObservableList makes TableView updates automatic.', FALSE, FALSE),
			    (13, 16, 5, 'Admin can be assigned via roles at login.', FALSE, FALSE),
			    (14, 17, 2, 'Use BCrypt to hash passwords.', FALSE, FALSE),
			    (15, 18, 3, 'SQLite is good for fast prototyping.', FALSE, FALSE),
			    (16, 19, 4, 'Use Platform.runLater inside uncaughtExceptionHandler.', FALSE, FALSE),
			    (17, 20, 1, 'Save tokens or use Java Preferences API.', FALSE, FALSE),

			    (2, NULL, 4, 'Additional reviewer insight for question 2.', FALSE, TRUE),
			    (2, 3, 4, 'Second review attached to answer 3 of question 2.', FALSE, TRUE),
			    (3, NULL, 4, 'First review for question 3.', FALSE, TRUE),
			    (3, NULL, 4, 'Second review for question 3.', FALSE, TRUE);
			    """, answerTableName);
		
		String populateMessagesTable = String.format("""
			    INSERT INTO %s (review_id, sender_id, receiver_id, parent_message_id, message_text, sent_timestamp) VALUES
			    (1, 3, 2, NULL, 'Review posted on your answer — check edge cases.', '2023-11-02 12:00:00'),
			    (1, 2, 3, 1, 'Got it, I’ll refine my code accordingly.', '2023-11-02 12:15:00'),
			    (2, 3, 4, NULL, 'Review added: watch your variable naming.', '2023-11-03 09:30:00'),
			    (2, 4, 3, 3, 'Thanks, I’ll make the updates.', '2023-11-03 09:45:00'),
			    (3, 3, 2, NULL, 'I left a comment on your loop structure.', '2023-11-03 14:10:00'),
			    (3, 2, 3, 5, 'Fixed it, using enhanced for-loop now.', '2023-11-03 14:25:00'),
			    (4, 5, 1, NULL, 'Feedback: your review of ExecutorService was clear.', '2023-11-04 08:40:00'),
			    (4, 1, 5, 7, 'Appreciate it! Glad it helped.', '2023-11-04 08:45:00');
			    """, interactionTableName);
		
		String populateAnswerRatingsTable = String.format("""
			    INSERT INTO %s (userId, answerId, rating) VALUES
			    (1, 1, 0.8),
			    (2, 1, 0.9),
			    (3, 1, 0.7),
			    (1, 2, 0.6),
			    (2, 2, 0.95),
			    (3, 2, 0.85),
			    (4, 3, 0.9),
			    (5, 4, 0.7),
			    (1, 5, 0.5),
			    (2, 6, 0.8),
			    (3, 7, 0.6),
			    (4, 8, 0.75),
			    (5, 9, 0.85),
			    (1, 10, 0.9),
			    (2, 11, 0.7),
			    (3, 12, 0.65),
			    (4, 13, 0.8),
			    (5, 14, 0.95),
			    (1, 15, 0.9),
			    (2, 16, 0.85),
			    (3, 17, 0.7),
			    (4, 18, 0.6),
			    (5, 19, 0.75),
			    (1, 20, 0.8);
			""", ratingTableName);
		
		String updateAverageRating = String.format("""
			    UPDATE %s a
			    SET averageRating = (
			        SELECT COALESCE(ROUND(AVG(r.rating), 2), 0)
			        FROM %s r
			        WHERE r.answerId = a.id
			    );
			""", answerTableName, ratingTableName);

			String updateUpvoteCount = String.format("""
			    UPDATE %s a
			    SET upvoteCount = (
			        SELECT COALESCE(COUNT(*), 0)
			        FROM %s r
			        WHERE r.answerId = a.id
			    );
			""", answerTableName, ratingTableName);

	

		try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {

			stmt.execute(populateUserTable);
			stmt.execute(populateQuestionTable);
			stmt.execute(populateAnswersTable);
			stmt.execute(populateMessagesTable);
			stmt.execute(populateAnswerRatingsTable);
			stmt.execute(updateAverageRating);
			stmt.execute(updateUpvoteCount);
		}

	}

	// ===================
	// READ
	// ===================

	@Override
	/**
	 *  Check if the database is empty
	 */
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
