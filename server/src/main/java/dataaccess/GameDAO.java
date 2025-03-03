package dataaccess;

import java.util.ArrayList;
import java.util.Map;

public interface GameDAO {
    ArrayList<Map> listGames();
    Map createGame(String gameName);
    Map getGame(String gameID);
    void updateGame(Map game, String gameID);
    void clear();
}
