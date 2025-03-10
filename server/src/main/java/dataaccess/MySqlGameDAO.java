package dataaccess;

import model.GameData;
import service.records.GameList;

import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS auth (
          gameID int NOT NULL,
          whiteUsername varchar(256) NOT NULL,
          blackUsername varchar(256) NOT NULL,
          gameName varchar(256) NOT NULL,
          gameJSON TEXT DEFAULT NULL,
          PRIMARY KEY (gameID)
        );
        """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

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
