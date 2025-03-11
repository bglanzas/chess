package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {
    private MySQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    void insertUser_Positive() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("john_doe");
        assertNotNull(retrievedUser);
        assertEquals("john_doe", retrievedUser.username());
    }

    @Test
    void insertUser_Negative_DuplicateUsername() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");
        userDAO.insertUser(user);

        // Attempt to insert duplicate
        assertThrows(DataAccessException.class, () ->
                userDAO.insertUser(user), "Inserting duplicate username should fail.");
    }

    @Test
    void getUser_Positive() throws DataAccessException {
        UserData user = new UserData("jane_doe", "securepassword", "jane@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("jane_doe");
        assertNotNull(retrievedUser);
        assertEquals("jane_doe", retrievedUser.username());
    }

    @Test
    void getUser_Negative_NotFound() throws DataAccessException {
        UserData retrievedUser = userDAO.getUser("non_existent_user");
        assertNull(retrievedUser, "Non-existent user should return null.");
    }


    @Test
    void clear_Positive() throws DataAccessException {
        userDAO.insertUser(new UserData("testUser", "password", "email@example.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("testUser"), "Database should be empty after clear.");
    }
}

