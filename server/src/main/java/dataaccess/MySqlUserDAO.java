package dataaccess;

import model.AuthData;
import model.UserData;

public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS auth (
          username varchar(256) NOT NULL,
          password varchar(256) NOT NULL,
          email varchar(256) NOT NULL,
          PRIMARY KEY (username)
        );
        """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData createUser(UserData userData) {
        return null;
    }

    @Override
    public void clearUsers() {

    }
}
