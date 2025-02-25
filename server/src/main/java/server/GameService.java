package server;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;
import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData createGame(String authToken, String gameName)throws DataAccessException{
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new DataAccessException("Unauthorized");
        }
        if(gameName == null || gameName.isEmpty()){
            throw new DataAccessException("Bad request");
        }

        int gameId = UUID.randomUUID().hashCode();
        GameData game = new GameData(gameId, null, null, gameName, null);
        gameDAO.insertGame(game);
        return game;

    }

    public List<GameData> listGames(String authToken)throws DataAccessException{
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new DataAccessException("Unauthorized");
        }
        return gameDAO.listGames();
    }
}
