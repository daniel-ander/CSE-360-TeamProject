package Tests;

import Database.Repositories.Interfaces.IUserRepository;
import Database.Services.UserService;
import Exceptions.DBExceptions.DataAccessException;
import Exceptions.DBExceptions.DuplicateRecordIDException;
import Models.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for User Service.
 */
public class UserServiceTest {

    private IUserRepository mockRepo;
    private UserService userService;

    /**
     * Sets up a mocked repository and initializes the UserService before each test.
     */
    @BeforeEach
    public void setup() {
        mockRepo = Mockito.mock(IUserRepository.class);
        userService = new UserService(mockRepo);
    }

    /**
     * Tests registering a unique user calls insertNewUser in the repository.
     */
    @Test
    public void Register_WithUniqueUser_CallsInsertNewUser() throws Exception {
        User newUser = new User("testName", "password", User.Role.student);
        when(mockRepo.doesUserExistByName("testName")).thenReturn(false);
        when(mockRepo.insertNewUser(newUser)).thenReturn(1);

        userService.register(newUser);

        verify(mockRepo, times(1)).insertNewUser(newUser);
    }

    /**
     * Tests registering an existing user throws DuplicateRecordIDException.
     */
    @Test
    public void Register_WithExistingUser_ThrowsDuplicateRecordIDException() throws Exception {
        User existingUser = new User("testName", "password", User.Role.admin);
        when(mockRepo.doesUserExistByName("testName")).thenReturn(true);

        assertThrows(DuplicateRecordIDException.class,
    		() -> userService.register(existingUser));

        verify(mockRepo, never()).insertNewUser(any());
    }
    
    /**
     * Tests registering a user throws DataAccessException when a SQLException occurs.
     */
    @Test
    public void Register_WhenSQLException_ThrowsDataAccessException() throws Exception {
        User u = new User("testName", "password", User.Role.staff);
        when(mockRepo.doesUserExistByName("testName")).thenReturn(false);
        doThrow(new SQLException("Insert fail"))
        	.when(mockRepo)
        	.insertNewUser(u);

        DataAccessException ex = assertThrows(DataAccessException.class,
    		() -> userService.register(u));
        assertTrue(ex.getMessage().contains("Could not register user"));
    }

    /**
     * Tests performing login with valid credentials returns true.
     */
    @Test
    public void PerformLogin_WithValidCredentials_ReturnsTrue() throws Exception {
        User u = new User("testName", "password", User.Role.student);
        when(mockRepo.doesUserExist(u)).thenReturn(true);

        boolean result = userService.performLogin(u);
        
        assertTrue(result);
        verify(mockRepo).doesUserExist(u);
    }

    /**
     * Tests performing login throws DataAccessException when a SQLException occurs.
     */
    @Test
    public void PerformLogin_WhenSQLException_ThrowsDataAccessException() throws Exception {
        User u = new User("badname", "dumbPassword", User.Role.staff);
        when(mockRepo.doesUserExist(u)).thenThrow(new SQLException("DB down"));

        DataAccessException ex = assertThrows(DataAccessException.class,
    		() -> userService.performLogin(u));
        assertTrue(ex.getMessage().contains("Could not log in due to"));
    }

    /**
     * Tests retrieving a user by name and password returns the expected user.
     */
    @Test
    public void GetUserByNameAndPassword_ReturnsUser() throws Exception {
        User expected = new User("testName", "password", User.Role.instructor);
        when(mockRepo.getUserByNameAndPassword("testName", "password")).thenReturn(expected);

        User result = userService.getUserByNameAndPassword("testName", "password");

        assertEquals(expected, result);
        verify(mockRepo).getUserByNameAndPassword("testName", "password");
    }

    /**
     * Tests retrieving a user role returns the correct enum value.
     */
    @Test
    public void GetUserRole_ReturnsRoleEnum() throws Exception {
        when(mockRepo.getUserRole("testName")).thenReturn("admin");

        User.Role role = userService.getUserRole("testName");

        assertEquals(User.Role.admin, role);
    }

    /**
     * Tests retrieving a user role throws DataAccessException when a SQLException occurs.
     */
    @Test
    public void GetUserRole_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(mockRepo.getUserRole("testName")).thenThrow(new SQLException("Fail"));

        DataAccessException ex = assertThrows(DataAccessException.class,
    		() -> userService.getUserRole("testName"));
        assertTrue(ex.getMessage().contains("Could not get user"));
    }

    /**
     * Tests retrieving all usernames returns a list of usernames.
     */
    @Test
    public void GetAllUsernames_ReturnsList() throws Exception {
        when(mockRepo.getAllUsernames())
        	.thenReturn(List.of("name1", "name2", "name3"));

        List<String> names = userService.getAllUsernames();
        
        assertEquals(3, names.size());
        verify(mockRepo).getAllUsernames();
    }

    /**
     * Tests updating a password returns a new password string with the expected format.
     */
    @Test
    public void UpdatePasswordForUser_ReturnsPassword() throws Exception {
        when(mockRepo.updatePasswordForUser(eq("testName"), anyString()))
            .thenAnswer(callParam -> callParam.getArgument(1)); // this echos back password

        String result = userService.updatePasswordForUser("testName");

        assertTrue(result.startsWith("R_"));
        assertTrue(result.length() >= 8);
        verify(mockRepo, times(1)).updatePasswordForUser(eq("testName"), anyString());
    }

    /**
     * Tests updating a password throws DataAccessException when a SQLException occurs.
     */
    @Test
    public void UpdatePasswordForUser_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(mockRepo.updatePasswordForUser(anyString(), anyString()))
            .thenThrow(new SQLException("Update fail"));

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> userService.updatePasswordForUser("testName"));
        assertTrue(ex.getMessage().contains("Could not get user names"));
    }

    /**
     * Tests updating a user role calls the repository method once.
     */
    @Test
    public void UpdateUserRole_CallsRepository() throws Exception {
        doNothing().when(mockRepo).updateUserRole("testName", "student");

        userService.updateUserRole("testName", User.Role.student);

        verify(mockRepo).updateUserRole("testName", "student");
    }

    /**
     * Tests that an admin cannot delete themselves.
     */
    @Test
    public void DeleteUser_AdminTriesToDeleteSelf_ReturnsFalse() throws Exception {
        when(mockRepo.getUserRole("admin")).thenReturn("admin");

        boolean result = userService.deleteUser("admin", "admin");

        assertFalse(result);
        verify(mockRepo, never()).deleteUser("admin");
    }
    
    /**
     * Tests deleting another user returns true.
     */
    @Test
    public void DeleteUser_OtherUser_ReturnsTrue() throws Exception {
        when(mockRepo.getUserRole("admin")).thenReturn("admin");
        when(mockRepo.deleteUser("target")).thenReturn(true);

        boolean result = userService.deleteUser("admin", "target");

        assertTrue(result);
        verify(mockRepo, times(1)).deleteUser("target");
    }

    /**
     * Tests deleting a user throws DataAccessException when a SQLException occurs.
     */
    @Test
    public void DeleteUser_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(mockRepo.deleteUser("testName")).thenThrow(new SQLException("Delete fail"));

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> userService.deleteUser("me", "testName"));
        assertTrue(ex.getMessage().contains("Could not delete user"));
    }

    /**
     * Tests printing the users table throws DataAccessException when a SQLException occurs.
     */
    @Test
    public void PrintUsersTable_WhenSQLException_ThrowsDataAccessException() throws Exception {
        doThrow(new SQLException("Print fail")).when(mockRepo).printAllRows();

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> userService.printUsersTable());
        assertTrue(ex.getMessage().contains("Could not print user table"));
    }
}
