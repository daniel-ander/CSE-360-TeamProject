package Tests;

import Database.Repositories.Interfaces.IPermissionRepository;
import Database.Services.PermissionService;
import Exceptions.DBExceptions.DataAccessException;
import Models.IPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Permission Service 
 */
public class PermissionServiceTest {

    private IPermissionRepository mockRepo;
    private PermissionService permissionService;

    /** sets up testing environment */
    @BeforeEach
    public void setup() {
        mockRepo = mock(IPermissionRepository.class);
        permissionService = new PermissionService(mockRepo);
    }

    /** ensures getEffectivePermissions returns list successfully */
    @Test
    public void GetEffectivePermissions_ReturnsList() throws Exception {
        int userId = 1;
        int postId = 1;
        List<String> expected = Arrays.asList("permission1", "permission2");
        when(mockRepo.getEffectivePermissions(userId, postId, true)).thenReturn(expected);

        List<String> result = permissionService.getEffectivePermissions(userId, postId, IPost.Type.Question);

        assertEquals(expected, result);
        verify(mockRepo, times(1)).getEffectivePermissions(userId, postId, true);
    }

    /** ensures getEffectivePermissions handles SQLException and throws DataAccessException */
    @Test
    public void testGetEffectivePermissions_ThrowsDataAccessException_OnSQLException() throws Exception {
        int userId = 1;
        int postId = 1;
        when(mockRepo.getEffectivePermissions(userId, postId, false))
                .thenThrow(new SQLException("DB read failed"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () ->
                permissionService.getEffectivePermissions(userId, postId, IPost.Type.Answer)
        );

        assertTrue(thrown.getMessage().contains("Failed to get effective permissions"));
        verify(mockRepo, times(1)).getEffectivePermissions(userId, postId, false);
    }

    /** ensures togglePermissionException successfully touches repository */
    @Test
    public void testTogglePermissionException_CallsRepository() throws Exception {
        int userId = 3;
        int postId = 1;
        String permission = "permission";
        doNothing().when(mockRepo).togglePermissionException(userId, postId, permission, true);

        permissionService.togglePermissionException(userId, postId, permission, true);

        verify(mockRepo, times(1)).togglePermissionException(userId, postId, permission, true);
    }

    /** ensures togglePermissionException handles SQLException and throws DataAccessException */
    @Test
    public void testTogglePermissionException_ThrowsDataAccessException_OnSQLException() throws Exception {
        int userId = 1;
        int postId = 1;
        String permission = "permission";
        doThrow(new SQLException("Toggle failed"))
                .when(mockRepo)
                .togglePermissionException(userId, postId, permission, false);

        DataAccessException thrownException = assertThrows(DataAccessException.class, () ->
                permissionService.togglePermissionException(userId, postId, permission, false)
        );

        assertTrue(thrownException.getMessage().contains("Failed to generate an invitation code"));
    }

    /** ensures seedRolePermissions handles SQLException */
    @Test
    public void testSeedRolePermissions_CatchesSQLException() throws Exception {
        doThrow(new SQLException("Seed failed")).when(mockRepo).seedRolePermissions();

        assertDoesNotThrow(() -> permissionService.seedRolePermissions());
        
        verify(mockRepo, times(1)).seedRolePermissions();
    }
}