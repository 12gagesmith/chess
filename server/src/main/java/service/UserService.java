package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.records.*;
import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = this.userDAO.getUser(registerRequest.username());
        if (userData != null) {
            throw new DataAccessException("Error: username already taken");
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData authData = this.userDAO.createUser(userData);
        this.authDAO.createAuth(authData);
        return new RegisterResult(userData.username(), authData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = this.userDAO.getUser(loginRequest.username());
        if (userData == null || !Objects.equals(loginRequest.password(), userData.password())) {
            throw new DataAccessException("Error: invalid username or password");
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        this.authDAO.createAuth(authData);
        return new LoginResult(userData.username(), authData.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = this.authDAO.getAuth(logoutRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        this.authDAO.deleteAuth(logoutRequest.authToken());
    }

    public void clear() {
        this.userDAO.clear();
        this.authDAO.clear();
    }
}
