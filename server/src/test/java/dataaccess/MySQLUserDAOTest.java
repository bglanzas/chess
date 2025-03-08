package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySQLUserDAOTest {

    private MySQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();  // Clear data before every test for consistent results
    }

    // ===========================
    //         POSITIVE TESTS
    // ===========================

    @Test
    void insertUser_positive() throws DataAccessException {
        UserData user = new UserData("johnDoe", "securePassword", "john@example.com");
        assertDoesNotThrow(() -> userDAO.insertUser(user));

        UserData retrievedUser = userDAO.getUser("johnDoe");
        assertNotNull(retrievedUser);
        assertEquals("johnDoe", retrievedUser.username());
        assertEquals("securePassword", retrievedUser.password());
        assertEquals("john@example.com", retrievedUser.email());
    }

    @Test
    void getUser_positive() throws DataAccessException {
        UserData user = new UserData("janeDoe", "strongPassword", "jane@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("janeDoe");
        assertNotNull(retrievedUser);
        assertEquals("janeDoe", retrievedUser.username());
    }

    @Test
    void updateUser_positive() throws DataAccessException {
        UserData user = new UserData("johnDoe", "securePassword", "john@example.com");
        userDAO.insertUser(user);

        UserData updatedUser = new UserData("johnDoe", "newPassword", "john.new@example.com");
        assertDoesNotThrow(() -> userDAO.updateUser(updatedUser));

        UserData retrievedUser = userDAO.getUser("johnDoe");
        assertEquals("newPassword", retrievedUser.password());
        assertEquals("john.new@example.com", retrievedUser.email());
    }

    @Test
    void deleteUser_positive() throws DataAccessException {
        UserData user = new UserData("johnDoe", "securePassword", "john@example.com");
        userDAO.insertUser(user);

        assertDoesNotThrow(() -> userDAO.deleteUser("johnDoe"));
        assertNull(userDAO.getUser("johnDoe"));
    }

    @Test
    void listUsers_positive() throws DataAccessException {
        userDAO.insertUser(new UserData("user1", "pass1", "user1@example.com"));
        userDAO.insertUser(new UserData("user2", "pass2", "user2@example.com"));

        List<UserData> users = userDAO.getAllUsers();
        assertEquals(2, users.size());
    }

    // ===========================
    //         NEGATIVE TESTS
    // ===========================

    @Test
    void insertUser_negative_duplicateUser() throws DataAccessException {
        UserData user = new UserData("johnDoe", "securePassword", "john@example.com");
        userDAO.insertUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.insertUser(user));
    }

    @Test
    void getUser_negative_nonExistentUser() throws DataAccessException {
        assertNull(userDAO.getUser("nonExistentUser"));
    }

    @Test
    void updateUser_negative_nonExistentUser() throws DataAccessException {
        UserData nonExistentUser = new UserData("nonExistentUser", "randomPassword", "none@example.com");

        assertThrows(DataAccessException.class, () -> userDAO.updateUser(nonExistentUser));
    }

    @Test
    void deleteUser_negative_nonExistentUser() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userDAO.deleteUser("nonExistentUser"));
    }

    @Test
    void getAllUsers_negative_emptyDatabase() throws DataAccessException {
        List<UserData> users = userDAO.getAllUsers();
        assertTrue(users.isEmpty());
    }
}
