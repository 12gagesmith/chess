package dataaccess;

import java.util.ArrayList;
import model.GameData;
import serverfacade.DataAccessException;
import serverfacade.records.GameList;

public interface GameDAO {
    ArrayList<GameList> listGames() throws DataAccessException;
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    void updateGame(GameData gameData, Integer gameID) throws DataAccessException;
    void clearGames() throws DataAccessException;
}
