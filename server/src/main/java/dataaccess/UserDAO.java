package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    UserData getUserByAuth(String authToken);
    AuthData createUser(UserData userData);
    void clear();
}
