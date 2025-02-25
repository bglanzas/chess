package dataAccess;


import model.GameData;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class GameDAO {
    private  final Map<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }

    public void insertGame(GameData game){
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public  List<GameData> listGames(){
        return new ArrayList<>(games.values());
    }
}
