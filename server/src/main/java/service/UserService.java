package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;

public class UserService {

    private final MemoryUserDAO memoryUserDao;

    public UserService() {
        this.memoryUserDao = new MemoryUserDAO();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = this.memoryUserDao.getUser(registerRequest.username());
        return null;
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {
    }
}
