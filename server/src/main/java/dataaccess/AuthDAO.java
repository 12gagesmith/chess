package dataaccess;

import model.AuthData;
import serverfacade.DataAccessException;

public interface AuthDAO {

    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clearAuth() throws DataAccessException;
}
