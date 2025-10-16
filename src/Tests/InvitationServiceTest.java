package Tests;

import Database.Repositories.Interfaces.IInvitationRepository;
import Database.Services.InvitationService;
import Exceptions.DBExceptions.DataAccessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvitationServiceTest {

    private IInvitationRepository mockRepo;
    private InvitationService invitationService;

    @BeforeEach
    public void setup() throws SQLException {
        mockRepo = Mockito.mock(IInvitationRepository.class);
        invitationService = new InvitationService(mockRepo);
    }

    // ===================
    // CREATE TESTS
    // ===================

    @Test
    public void GenerateInvitationCode_WithValidRole_InsertsAndReturnsCode() throws Exception {
        // Arrange
        when(mockRepo.insertInvitationCode(anyString()))
        	.thenReturn(true);;

        // Act
        String code = invitationService.generateInvitationCode("10100");

        // Assert
        assertNotNull(code);
        assertTrue(code.startsWith("10100_"));
        verify(mockRepo, times(1)).insertInvitationCode(code);
    }

    @Test
    public void GenerateInvitationCode_WhenSQLException_ThrowsDataAccessException() throws Exception {
        // Arrange
        doThrow(new SQLException("DB fail"))
        	.when(mockRepo)
        	.insertInvitationCode(anyString());

        // Act + Assert
        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> invitationService.generateInvitationCode("11100"));
        assertTrue(ex.getMessage().contains("Failed to generate an invitation code"));
    }

    // ===================
    // READ TESTS
    // ===================

    @Test
    public void GetRoleFromInviteCode_WithValidCode_ReturnsExpectedRoles() throws Exception {
        // Act
        String roles = invitationService.getRoleFromInviteCode("10100_abcd");

        // Assert
        assertEquals("admin,instructor", roles);
    }

    @Test
    public void GetRoleFromInviteCode_WithNoRoles_ReturnsEmptyString() throws Exception {
        String roles = invitationService.getRoleFromInviteCode("00000_xyz");
        
        assertEquals("", roles);
    }

    @Test
    public void ValidateInvitationCode_ValidCall_ReturnsRepositoryResult() throws Exception {
        when(mockRepo.validateInvitationCode("testcode"))
        	.thenReturn(true);

        boolean result = invitationService.validateInvitationCode("testcode");

        assertTrue(result);
        verify(mockRepo, times(1)).validateInvitationCode("testcode");
    }

    @Test
    public void ValidateInvitationCode_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(mockRepo.validateInvitationCode(anyString()))
        	.thenThrow(new SQLException("DB error"));

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> invitationService.validateInvitationCode("badcode"));
        assertTrue(ex.getMessage().contains("Could not validate invitation code"));
    }

    @Test
    public void PrintInvitationCodesTable_WhenSQLException_ThrowsDataAccessException() throws Exception {
        doThrow(new SQLException("Print fail"))
        	.when(mockRepo)
    		.printInvitationCodesTable();

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> invitationService.printInvitationCodesTable());
        assertTrue(ex.getMessage().contains("Could not print invitation codes"));
    }

    // ===================
    // UPDATE TESTS
    // ===================

    @Test
    public void MarkInvitationCodeAsUsed_WithValidCode_CallsRepository() throws Exception {
        doNothing().when(mockRepo)
        	.markInvitationCodeAsUsed("testcode");

        invitationService.markInvitationCodeAsUsed("testcode");

        verify(mockRepo, times(1)).markInvitationCodeAsUsed("testcode");
    }

    @Test
    public void MarkInvitationCodeAsUsed_WhenSQLException_ThrowsDataAccessException() throws Exception {
        doThrow(new SQLException("Mark fail"))
        	.when(mockRepo)
        	.markInvitationCodeAsUsed(anyString());

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> invitationService.markInvitationCodeAsUsed("code123"));
        assertTrue(ex.getMessage().contains("Could not mark used invitation code"));
    }
}
