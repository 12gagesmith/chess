package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.DataAccessException;
import server.records.GameList;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{

    private final ArrayList<GameData> gameDB;
    private int nextID = 1;

    public MemoryGameDAO() {
        this.gameDB = new ArrayList<>();
    }

    @Override
    public ArrayList<GameList> listGames() {
        ArrayList<GameList> listOfGames = new ArrayList<>();
        for (GameData gameData : gameDB) {
            listOfGames.add(new GameList(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName()));
        }
        return listOfGames;
    }

    @Override
    public GameData createGame(String gameName) {
        int gameID = nextID++;
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        this.gameDB.add(gameData);
        return gameData;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        for (GameData gameData : gameDB) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        throw new DataAccessException(403, "Error: cannot find game");
    }

    @Override
    public void updateGame(GameData gameData, Integer gameID) throws DataAccessException {
        GameData oldGameData = this.getGame(gameData.gameID());
        this.gameDB.remove(oldGameData);
        this.gameDB.add(gameData);
    }

    @Override
    public void clearGames() {
        int numGames = gameDB.size();
        if (numGames > 0) {
            gameDB.subList(0, numGames).clear();
        }
    }
}
