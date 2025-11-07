package Database.Services;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Exceptions.DBExceptions.DataAccessException;
import Database.Services.Interfaces.IUserService;

/**
 * Lightweight request service used by controllers to submit reviewer requests and
 * list/approve pending requests. This implementation uses raw SQL where a
 * repository/service isn't available to avoid large refactors.
 */
public class RequestService {
    private final DBConnectionStore store;
    private final IUserService userService;

    public RequestService(DBConnectionStore store) {
        this.store = store;
        this.userService = store.getUserService();
    }

    // ensure the reviewer_requests table exists
    private void ensureReviewerRequestsTable(Connection conn) throws SQLException {
        String createSql = "CREATE TABLE IF NOT EXISTS reviewer_requests ("
                + "request_id IDENTITY PRIMARY KEY, "
                + "student_id INT, "
                + "instructor_id INT, "
                + "status VARCHAR(20) DEFAULT 'PENDING', "
                + "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (student_id) REFERENCES users(id), "
                + "FOREIGN KEY (instructor_id) REFERENCES users(id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        }
    }

    // student req reviewer role and finds instructor to assign request to
    public boolean submitReviewerRequest(String studentUsername) {
        try (Connection conn = store.getGeneralDBManager().getConnection()) {
            // table exists before inserting
            ensureReviewerRequestsTable(conn);

            int studentId = userService.getUserIdByName(studentUsername);

            // find any instructor id
            String findInstructorSql = "SELECT id FROM users WHERE LOWER(role) LIKE '%instructor%' LIMIT 1";
            int instructorId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(findInstructorSql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    instructorId = rs.getInt("id");
                }
            }

            if (instructorId == -1) {
                // no instructor role/user found
                return false;
            }

            String insertSql = "INSERT INTO reviewer_requests (student_id, instructor_id, status) VALUES (?, ?, 'PENDING')";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, instructorId);
                int affected = stmt.executeUpdate();
                return affected > 0;
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Error submitting reviewer request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // returns list of users who have pending requests
    public List<String> getPendingRequests() throws DataAccessException {
        List<String> result = new ArrayList<>();
        String sql = "SELECT u.userName FROM reviewer_requests r JOIN users u ON r.student_id = u.id WHERE r.status = 'PENDING'";
        try (Connection conn = store.getGeneralDBManager().getConnection()) {
            // Ensure table exists before querying
            ensureReviewerRequestsTable(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    result.add(rs.getString("userName"));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Could not get pending requests: " + e.getMessage());
        }
    }

    // gives reviewer role to student user + mark request as approved
    public boolean grantReviewerRole(String studentUsername) {
        try (Connection conn = store.getGeneralDBManager().getConnection()) {
            // ensures table exists before update
            ensureReviewerRequestsTable(conn);
            // add reviewer role to existing roles instead of replacing
            try {
                userService.addRoleToUser(studentUsername, User.Role.reviewer);
            } catch (DataAccessException e) {
                System.err.println("Error adding reviewer role to user: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            //mark any pending requests for this student as APPROVED
            String updateSql = "UPDATE reviewer_requests SET status = 'APPROVED' WHERE student_id = (SELECT id FROM users WHERE userName = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, studentUsername);
                stmt.executeUpdate();
                // role added and request updated
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error granting reviewer role: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // deny a reviewer request for the given student user + set status = DENIED
    public boolean denyReviewerRequest(String studentUsername) {
        try (Connection conn = store.getGeneralDBManager().getConnection()) {
            ensureReviewerRequestsTable(conn);
            String updateSql = "UPDATE reviewer_requests SET status = 'DENIED' WHERE student_id = (SELECT id FROM users WHERE userName = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, studentUsername);
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error denying reviewer request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
