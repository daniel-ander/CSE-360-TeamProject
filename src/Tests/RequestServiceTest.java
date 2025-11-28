package Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Database.Services.RequestService;
import Database.Services.Interfaces.IUserService;
import Models.Interfaces.ISharedUser.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Request Service
 */
class RequestServiceTest {
	
    private DBConnectionStore mockStore;
    private IUserService mockUserService;
    private IGeneralDBManager mockDBManager;
    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRS;
    private RequestService service;
    
    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setup() throws SQLException {
        mockStore = mock(DBConnectionStore.class);
        mockUserService = mock(IUserService.class);
        mockDBManager = mock(IGeneralDBManager.class);
        mockConn = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockRS = mock(ResultSet.class);

        when(mockStore.getUserService()).thenReturn(mockUserService);
        when(mockStore.getGeneralDBManager()).thenReturn(mockDBManager);
        when(mockDBManager.getConnection()).thenReturn(mockConn);
        
        Statement stmt = mock(Statement.class);
        when(mockConn.createStatement()).thenReturn(stmt);
        when(stmt.execute(anyString())).thenReturn(true);

        service = new RequestService(mockStore);
    }

    /**
     * Tests that submitReviewerRequest(String) returns false connection fails
     */
    @Test
    void EnsureReviewerRequestsTable_WhenConnectionFails_ThrowsException() throws Exception {
        when(mockDBManager.getConnection()).thenThrow(new SQLException("Connection failed"));
        RequestService failingService = new RequestService(mockStore);

        boolean result = failingService.submitReviewerRequest("student");

        assertFalse(result);
    }
    
    /**
     * Verifies that a reviewer request submission succeeds
     */
    @Test
    void SubmitReviewerRequest_WhenInstructorExists_ShouldReturnTrue() throws Exception {
    	when(mockUserService.getUserIdByName("student")).thenReturn(1);
    	
        PreparedStatement checkStmt = mock(PreparedStatement.class);
        ResultSet checkRS = mock(ResultSet.class);
        when(mockConn.prepareStatement(contains("SELECT COUNT"))).thenReturn(checkStmt);
        when(checkStmt.executeQuery()).thenReturn(checkRS);
        when(checkRS.next()).thenReturn(true);
        when(checkRS.getInt("cnt")).thenReturn(0);

        PreparedStatement findStmt = mock(PreparedStatement.class);
        ResultSet findRS = mock(ResultSet.class);
        when(mockConn.prepareStatement(contains("SELECT id FROM users"))).thenReturn(findStmt);
        when(findStmt.executeQuery()).thenReturn(findRS);
        when(findRS.next()).thenReturn(true);
        when(findRS.getInt("id")).thenReturn(42);

        PreparedStatement insertStmt = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(startsWith("INSERT INTO reviewer_requests"))).thenReturn(insertStmt);
        when(insertStmt.executeUpdate()).thenReturn(1);

        boolean result = service.submitReviewerRequest("student");
        assertTrue(result, "Expected successful submission when instructor exists");
    }


    /**
     * Tests submit reviewer request fails without instructor
     */
    @Test
    void SubmitReviewerRequest_WhenNoInstructorExists_ReturnsFalse() throws Exception {
        when(mockUserService.getUserIdByName("student")).thenReturn(1);

        PreparedStatement checkStmt = mock(PreparedStatement.class);
        ResultSet checkRS = mock(ResultSet.class);
        when(mockConn.prepareStatement(contains("SELECT COUNT"))).thenReturn(checkStmt);
        when(checkStmt.executeQuery()).thenReturn(checkRS);
        when(checkRS.next()).thenReturn(true);
        when(checkRS.getInt("cnt")).thenReturn(0);

        PreparedStatement findStmt = mock(PreparedStatement.class);
        ResultSet findRS = mock(ResultSet.class);
        when(mockConn.prepareStatement(contains("SELECT id FROM users"))).thenReturn(findStmt);
        when(findStmt.executeQuery()).thenReturn(findRS);
        when(findRS.next()).thenReturn(false); // no instructor found

        boolean result = service.submitReviewerRequest("student");
        assertFalse(result, "Expected false when no instructor exists");
    }

    /**
     * Tests that getPendingRequests() returns a list of user names
     */
    @Test
    void GetPendingRequests_ReturnsListOfPendingUsernames() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRS);
        when(mockRS.next()).thenReturn(true, true, false);
        when(mockRS.getString("userName")).thenReturn("trevor", "jeka");

        List<String> result = service.getPendingRequests();

        assertEquals(2, result.size());
        assertTrue(result.contains("trevor"));
        assertTrue(result.contains("jeka"));
    }

    /**
     * Tests that grantReviewerRole(String) adds and approves request
     */
    @Test
    void GrantReviewerRole_AddsRoleAndApprovesRequest() throws Exception {
        when(mockConn.prepareStatement(startsWith("UPDATE reviewer_requests"))).thenReturn(mockStmt);

        boolean result = service.grantReviewerRole("student");

        verify(mockUserService).addRoleToUser("student", Role.reviewer);
        verify(mockStmt).executeUpdate();
        assertTrue(result);
    }

    /**
     * Tests that denyReviewerRequest(String) sets status to denied.
     */
    @Test
    void DenyReviewerRequest_SetsStatusToDenied() throws Exception {
        when(mockConn.prepareStatement(startsWith("UPDATE reviewer_requests"))).thenReturn(mockStmt);

        boolean result = service.denyReviewerRequest("student");

        verify(mockStmt).executeUpdate();
        assertTrue(result);
    }
}
