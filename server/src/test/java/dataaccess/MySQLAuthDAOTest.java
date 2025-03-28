package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {
    private MySQLAuthDAO authDAO;
    private MySQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        userDAO = new MySQLUserDAO();

        authDAO.clear();
        userDAO.clear();


        userDAO.insertUser(new UserData("john", "password123", "john@example.com"));
        userDAO.insertUser(new UserData("benson", "securePass", "benson@example.com"));
    }

    @Test
    void clearPositive() throws DataAccessException {
        AuthData auth1 = new AuthData("token123", "john");
        AuthData auth2 = new AuthData("deltafox", "benson");

        authDAO.insertAuth(auth1);
        authDAO.insertAuth(auth2);

        assertNotNull(authDAO.getAuth("token123"));
        assertNotNull(authDAO.getAuth("deltafox"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token123"));
        assertNull(authDAO.getAuth("deltafox"));
    }

    @Test
    void insertAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("validToken", "john");

        authDAO.insertAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("validToken");
        assertNotNull(retrievedAuth);
        assertEquals("john", retrievedAuth.username());
    }

    @Test
    void insertAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("invalidToken", "nonExistentUser");

        assertThrows(DataAccessException.class, () -> authDAO.insertAuth(auth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("auth999", "john");

        authDAO.insertAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("auth999");
        assertNotNull(retrievedAuth);
        assertEquals("john", retrievedAuth.username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        assertNull(authDAO.getAuth("nonExistentToken"));
    }


    @Test
    void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("deleteMe", "john");

        authDAO.insertAuth(auth);
        assertNotNull(authDAO.getAuth("deleteMe"));

        authDAO.deleteAuth("deleteMe");
        assertNull(authDAO.getAuth("deleteMe"));
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("missingToken"));
    }
}

