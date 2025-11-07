package Database.Repositories.Interfaces;

import Models.ReviewerRequest;
import java.sql.SQLException;
import java.util.List;

public interface IReviewerRequestRepository {

    // submit new request to become reviewer role
    void submitRequest(ReviewerRequest request) throws SQLException;

    // pending requests for specific instructor
    List<ReviewerRequest> getPendingRequestsByInstructor(int instructorId) throws SQLException;

    // update status of reviewer request (approved/denied)
    void updateRequestStatus(int requestId, String status) throws SQLException;

    // fetch specific request by its id
    ReviewerRequest getRequestById(int requestId) throws SQLException;
}
