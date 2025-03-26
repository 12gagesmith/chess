package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import serverfacade.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS users (
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (username)
        );
        """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, json FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public AuthData createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(userData);
        DatabaseManager.executeUpdate(statement, userData.username(), userData.password(), userData.email(), json);
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, userData.username());
    }

    @Override
    public void clearUsers() throws DataAccessException {
        String statement = "TRUNCATE users";
        DatabaseManager.executeUpdate(statement);
    }
}
