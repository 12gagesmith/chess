package dataaccess;

import model.AuthData;
import model.UserData;

public class MySqlUserDAO implements UserDAO{
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
