package dataaccess;

import java.util.ArrayList;
import service.Game;

public interface GameDAO {
    ArrayList<Game> listGames();
    Game createGame(String gameName);
    Game getGame(String gameID);
    void updateGame(Game game, String gameID);
    void clear();
}
