package server;


import dataAccess.DataAccessException;
import dataAccess.AuthDAO;


public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void validate(String authToken)throws DataAccessException{
        if(authDAO.getAuth(authToken) == null){
            throw new DataAccessException("Unauthorized");
        }
    }
}
