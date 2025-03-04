package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    AuthData createUser(UserData userData);
    void clear();
}
