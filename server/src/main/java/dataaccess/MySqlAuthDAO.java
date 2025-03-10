package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{
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
