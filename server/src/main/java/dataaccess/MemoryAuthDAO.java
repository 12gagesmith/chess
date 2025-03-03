package dataaccess;

import model.AuthData;
import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{

    private final ArrayList<AuthData> authDB;

    public MemoryAuthDAO() {
        this.authDB = new ArrayList<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        authDB.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}
