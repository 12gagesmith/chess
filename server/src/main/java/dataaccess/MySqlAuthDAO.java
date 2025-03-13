package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS auth (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (authToken)
        );
        """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(authData);
        DatabaseManager.executeUpdate(statement, authData.authToken(), authData.username(), json);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken=?";
        if (getAuth(authToken) == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        DatabaseManager.executeUpdate(statement, authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException {
        String statement = "TRUNCATE auth";
        DatabaseManager.executeUpdate(statement);
    }
}
