package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS auth (
          authToken varchar(256) NOT NULL,
          username varchar(256) NOT NULL,
          PRIMARY KEY (authToken)
        );
        """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clearAuth() {

    }
}
