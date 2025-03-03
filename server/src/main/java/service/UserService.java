package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.records.*;

public class UserService {

    private final MemoryUserDAO memoryUserDAO;
    private final MemoryAuthDAO memoryAuthDAO;

    public UserService() {
        this.memoryUserDAO = new MemoryUserDAO();
        this.memoryAuthDAO = new MemoryAuthDAO();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = this.memoryUserDAO.getUser(registerRequest.username());
        if (userData != null) {
            throw new DataAccessException("Error: username already taken");
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData authData = this.memoryUserDAO.createUser(userData);
        this.memoryAuthDAO.createAuth(authData);
        return new RegisterResult(userData.username(), authData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }

    public void logout(LogoutRequest logoutRequest) {
    }

    public void clear() {
    }
}
