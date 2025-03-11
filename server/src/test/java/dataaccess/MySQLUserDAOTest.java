package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures tests run in order
public class MySQLUserDAOTest {
    private static MySQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    @Order(1)
    public void testInsertUser_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "email@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("testUser");

        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertEquals("email@example.com", retrievedUser.email());
    }

    @Test
    @Order(2)
    public void testInsertUser_Negative_Duplicate() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "email@example.com");
        userDAO.insertUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.insertUser(user),
                "Inserting a duplicate user should throw an error.");
    }


    @Test
    @Order(3)
    public void testGetUser_Positive() throws DataAccessException {
        UserData user = new UserData("existingUser", "securePass", "user@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("existingUser");

        assertNotNull(retrievedUser);
        assertEquals("existingUser", retrievedUser.username());
        assertEquals("user@example.com", retrievedUser.email());
    }

    @Test
    @Order(4)
    public void testGetUser_Negative_NonExistent() throws DataAccessException {
        assertNull(userDAO.getUser("nonExistentUser"),
                "Fetching a non-existent user should return null.");
    }

    @Test
    @Order(5)
    public void testDeleteUser_Positive() throws DataAccessException {
        UserData user = new UserData("deleteUser", "deletePass", "delete@example.com");
        userDAO.insertUser(user);

        userDAO.deleteUser("deleteUser");

        assertNull(userDAO.getUser("deleteUser"),
                "Deleted user should no longer exist.");
    }

    @Test
    @Order(6)
    public void testDeleteUser_Negative_NonExistent() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userDAO.deleteUser("ghostUser"),
                "Deleting a non-existent user should fail.");
    }
}
