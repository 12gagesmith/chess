package dataaccess;

import model.AuthData;
import model.UserData;
import java.util.UUID;
import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{

    private final ArrayList<UserData> userDB;

    public MemoryUserDAO() {
        this.userDB = new ArrayList<>();
    }

    @Override
    public UserData getUser(String username) {
        for (UserData usersData : userDB) {
            if (usersData.username().equals(username)) {
                return usersData;
            }
        }
        return null;
    }

    @Override
    public AuthData createUser(UserData userData) {
        userDB.add(userData);
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, userData.username());
    }

    @Override
    public void clearUsers() {
        int numUsers = userDB.size();
        if (numUsers > 0) {
            userDB.subList(0, numUsers).clear();
        }
    }
}
