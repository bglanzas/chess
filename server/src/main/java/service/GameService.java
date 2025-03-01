package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.UUID;
import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<GameData> listGames(String authToken)throws DataAccessException{
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new DataAccessException("UnAuthorized");
        }
        return gameDAO.listGames();
    }


}
