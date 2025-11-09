package Tests;

import Database.Repositories.Interfaces.IReviewerRequestRepository;
import Database.Services.UserService;
import Database.Services.InstructorService;
import Exceptions.DBExceptions.DataAccessException;
import Models.ReviewerRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstructorServiceTest {

    /** A mock {@link IReviewerRequestRepository} repository for creating the instructor service */
    private IReviewerRequestRepository mockRequestRepo;
    
    /** A mock {@link UserService} for creating the instructor service */
    private UserService mockUserService;
    
    /** This is the service we are testing */
    private InstructorService instructorService;

    
    /**
     * Initializes mock dependencies and creates an instance of {@link InstructorService}
     * before each test case.
     */
    @BeforeEach
    public void setup() {
    	mockRequestRepo = Mockito.mock(IReviewerRequestRepository.class);
    	mockUserService = Mockito.mock(UserService.class);
        instructorService = new InstructorService(mockRequestRepo, mockUserService);
    }

    /**
     * Tests that {@link InstructorService#viewReviewerRequests(int)} returns a filled list
     * containing reviewer requests when valid requests is given.
     */
    @Test
    public void ViewReviewerRequests_WithValidId_ReurnsList() throws Exception {
        int instructorId = 42;
        ReviewerRequest req1 = new ReviewerRequest();
        ReviewerRequest req2 = new ReviewerRequest();
        when(mockRequestRepo.getPendingRequestsByInstructor(instructorId))
                .thenReturn(Arrays.asList(req1, req2));

        List<ReviewerRequest> result = instructorService.viewReviewerRequests(instructorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mockRequestRepo, times(1)).getPendingRequestsByInstructor(instructorId);
    }
    
    /**
     * Tests that {@link InstructorService#viewReviewerRequests(int)} returns an empty list
     * when no reviewer requests exist for the given instructor ID.
     */
    @Test
    public void ViewReviewerRequests_WhenNoRequests_ReturnsEmptyList() throws SQLException {
        int instructorId = 99;
        when(mockRequestRepo.getPendingRequestsByInstructor(instructorId))
                .thenReturn(List.of()); // empty list

        List<ReviewerRequest> result = instructorService.viewReviewerRequests(instructorId);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected an empty list for instructor with no requests");
        verify(mockRequestRepo, times(1)).getPendingRequestsByInstructor(instructorId);
    }
    
    /**
     * Tests that {@link InstructorService#viewReviewerRequests(int)} throws
     * a {@link SQLException} when the repository throws one.
     */
    @Test
    public void ViewReviewerRequests_ReceivesSQLException_ThrowsException() throws Exception {
        int instructorId = 7;
        when(mockRequestRepo.getPendingRequestsByInstructor(instructorId))
                .thenThrow(new SQLException("Database connection lost"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            instructorService.viewReviewerRequests(instructorId);
        });

        assertEquals("Database connection lost", thrown.getMessage());
        verify(mockRequestRepo, times(1)).getPendingRequestsByInstructor(instructorId);
    }

    /**
     * Tests that {@link InstructorService#processReviewerRequest(int, String)} correctly
     * calls {@link UserService}.
     */
    @Test
    public void ProcessReviewerRequest_CallsUserService() throws Exception {
        int requestId = 5;
        String decision = "approved";
        doNothing().when(mockUserService).processReviewerRequest(requestId, decision);

        instructorService.processReviewerRequest(requestId, decision);

        verify(mockUserService, times(1)).processReviewerRequest(requestId, decision);
    }
    
    /**
     * Tests that {@link InstructorService#processReviewerRequest(int, String)} throws
     * a {@link SQLException} when {@link UserService} throws a {@link DataAccessException}.
     */
    @Test
    public void ProcessReviewerRequest_WhenDataAccessException_ThrowsSQLException() throws Exception {
        int requestId = 10;
        String decision = "denied";
        doThrow(new DataAccessException("DB error"))
                .when(mockUserService)
                .processReviewerRequest(requestId, decision);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            instructorService.processReviewerRequest(requestId, decision);
        });

        assertTrue(thrown.getCause() instanceof DataAccessException);
        verify(mockUserService, times(1)).processReviewerRequest(requestId, decision);
    }
}
