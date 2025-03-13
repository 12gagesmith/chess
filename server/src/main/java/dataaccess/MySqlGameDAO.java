package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.records.GameList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS games (
          `gameID` int NOT NULL,
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
    public ArrayList<GameList> listGames() {
        return null;
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        String json = rs.getString("game");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, json FROM games WHERE gameID=?";
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

    }

    @Override
    public void clearGames() throws DataAccessException {
        String statement = "TRUNCATE games";
        DatabaseManager.executeUpdate(statement);
    }
}
