package Database.Repositories;

import Database.Repositories.Interfaces.IReviewerRequestRepository;
import Models.ReviewerRequest;
import Database.IGeneralDBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// managing reviewer requests in the database
public class ReviewerRequestRepository implements IReviewerRequestRepository {
    private final IGeneralDBManager generalDBManager;
    public ReviewerRequestRepository(IGeneralDBManager generalDBManager) {
        this.generalDBManager = generalDBManager;
    }

    //submit a new reviewer request to the database
    public void submitRequest(ReviewerRequest request) throws SQLException {
        String query = "INSERT INTO reviewer_requests (student_id, instructor_id, status) VALUES (?, ?, ?)";
        try (Connection connection = generalDBManager.getConnection(); PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getStudentId());
            stmt.setInt(2, request.getInstructorId());
            stmt.setString(3, request.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) { //get the generated request ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) { // set the generated request ID in the request object
                        request.setRequestId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    // get all pending requests for a specific instructor
    public List<ReviewerRequest> getPendingRequestsByInstructor(int instructorId) throws SQLException {
        List<ReviewerRequest> requests = new ArrayList<>(); // list to hold the pending requests
        String query = "SELECT * FROM reviewer_requests WHERE instructor_id = ? AND status = 'PENDING'";
        try (Connection connection = generalDBManager.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { // create ReviewerRequest objects from the result set + add them to the list
                    ReviewerRequest request = new ReviewerRequest(rs.getInt("student_id"), rs.getInt("instructor_id"));
                    request.setRequestId(rs.getInt("request_id"));
                    request.setStatus(rs.getString("status"));
                    requests.add(request);
                }
            }
        }
        return requests;
    }

    // update the status of a request
    public void updateRequestStatus(int requestId, String status) throws SQLException {
        String query = "UPDATE reviewer_requests SET status = ? WHERE request_id = ?";
        try (Connection connection = generalDBManager.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status); 
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }

    //method to get a request by its ID
    public ReviewerRequest getRequestById(int requestId) throws SQLException {
        String query = "SELECT * FROM reviewer_requests WHERE request_id = ?";
        try (Connection connection = generalDBManager.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ReviewerRequest request = new ReviewerRequest(rs.getInt("student_id"), rs.getInt("instructor_id"));
                    request.setRequestId(rs.getInt("request_id"));
                    request.setStatus(rs.getString("status"));
                    return request;
                }
            }
        }
        return null;
    }
}
