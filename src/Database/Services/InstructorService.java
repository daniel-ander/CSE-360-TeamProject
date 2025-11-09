package Database.Services;

import java.sql.SQLException;
import java.util.List;
import Models.ReviewerRequest;
import Database.Repositories.Interfaces.IReviewerRequestRepository;

public class InstructorService {
    private final IReviewerRequestRepository requestRepo;
    private final UserService userService;

    public InstructorService(IReviewerRequestRepository requestRepo, UserService userService) {
        this.requestRepo = requestRepo;
        this.userService = userService;
    }

    // view all pending reviewer access requests sent
    public List<ReviewerRequest> viewReviewerRequests(int instructorId) throws SQLException {
        return requestRepo.getPendingRequestsByInstructor(instructorId);
    }

    // approve or deny a reviewer request + grant reviewer role if approved
    public void processReviewerRequest(int requestId, String decision) throws SQLException {
        // transfer to the userService to update request status + granting role
        try { // handle DataAccessException
            userService.processReviewerRequest(requestId, decision);
        } catch (Exceptions.DBExceptions.DataAccessException e) {
            throw new SQLException(e);
        }
    }
}