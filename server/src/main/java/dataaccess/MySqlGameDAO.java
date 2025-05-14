package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import serverfacade.DataAccessException;
import serverfacade.records.GameList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS games (
          `gameID` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256) DEFAULT NULL,
          `blackUsername` varchar(256) DEFAULT NULL,
          `gameName` varchar(256) NOT NULL,
          `game` TEXT NOT NULL,
          PRIMARY KEY (gameID)
        );
        """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public ArrayList<GameList> listGames() throws DataAccessException {
        ArrayList<GameList> listOfGames = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData gameData = readGame(rs);
                        if (!gameData.game().gameOver) {
                            listOfGames.add(new GameList(gameData.gameID(), gameData.whiteUsername(),
                                    gameData.blackUsername(), gameData.gameName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return listOfGames;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        String json = new Gson().toJson(game);
        int gameID = DatabaseManager.executeUpdate(statement, null, null, gameName, json);
        return new GameData(gameID, null, null, gameName, game);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String json = rs.getString("game");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData, Integer gameID) throws DataAccessException {
        String json = new Gson().toJson(gameData.game());
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE games SET whiteUsername=?, blackUsername=?, " +
                    "gameName=?, game=? WHERE gameID=?")) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());
                ps.setString(3, gameData.gameName());
                ps.setString(4, json);
                ps.setInt(5, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        String statement = "TRUNCATE games";
        DatabaseManager.executeUpdate(statement);
    }
}
