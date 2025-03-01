package dataAccess;


import model.GameData;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class GameDAO {
    private  final Map<Integer, GameData> games = new HashMap<>();

    public GameData insertGame(GameData game) {
        int gameID;
        do {
            gameID = Math.abs(UUID.randomUUID().hashCode());
        } while (games.containsKey(gameID));

        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, newGame);

        return newGame;
    }

    public void clear(){
        games.clear();
    }


    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public  List<GameData> listGames(){
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException{
        if(!games.containsKey(game.gameID())){
            throw new DataAccessException("Game does not exist");
        }

        games.put(game.gameID(), game);
    }
}
