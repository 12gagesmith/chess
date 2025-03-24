package dataaccess;

import model.AuthData;
import model.UserData;
import server.DataAccessException;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    AuthData createUser(UserData userData) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
