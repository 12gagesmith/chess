package dataaccess;

import model.GameData;
import service.records.GameList;

import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO{
    @Override
    public ArrayList<GameList> listGames() {
        return null;
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameData, Integer gameID) throws DataAccessException {

    }

    @Override
    public void clearGames() {

    }
}
