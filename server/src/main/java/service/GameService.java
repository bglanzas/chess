package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

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

    public GameData createGame(String authToken, String gameName) throws DataAccessException {

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }

        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Bad request");
        }
        GameData newGame = new GameData(0, null, null, gameName, null);
        newGame = gameDAO.insertGame(newGame);

        return newGame;
    }



    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        // Validate authentication
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }


        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new DataAccessException("Bad request");
        }


        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Bad request");
        }

        GameData updatedGame;
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Already taken");
            }
            updatedGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Already taken");
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }
        gameDAO.updateGame(updatedGame);
    }

}
