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
        for (AuthData authData : authDB) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        authDB.removeIf(authData -> authData.authToken().equals(authToken));
    }

    @Override
    public void clearAuth() {
        int numAuths = authDB.size();
        if (numAuths > 0) {
            authDB.subList(0, numAuths).clear();
        }
    }
}
