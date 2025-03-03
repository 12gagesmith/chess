package dataaccess;

import java.util.ArrayList;
import model.GameData;
import service.records.GameList;

public interface GameDAO {
    ArrayList<GameList> listGames();
    GameData createGame(String gameName);
    GameData getGame(Integer gameID) throws DataAccessException;
    void updateGame(GameData gameData, Integer gameID) throws DataAccessException;
    void clear();
}
