package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import service.records.*;
import java.util.ArrayList;
import java.util.UUID;

public class Service {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Service() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
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
        if (userData == null || !loginRequest.password().equals(userData.password())) {
            throw new DataAccessException("Error: invalid username or password");
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        this.authDAO.createAuth(authData);
        return new LoginResult(userData.username(), authData.authToken());
    }

    private void authenticate(String authToken) throws DataAccessException {
        AuthData authData = this.authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        authenticate(logoutRequest.authToken());
        this.authDAO.deleteAuth(logoutRequest.authToken());
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        authenticate(listRequest.authToken());
        ArrayList<GameData> gameList = this.gameDAO.listGames();
        return new ListResult(gameList);
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        authenticate(createRequest.authToken());
        GameData gameData = this.gameDAO.createGame(createRequest.gameName());
        return new CreateResult(gameData.gameID());
    }

    private GameData modifyGame(GameData gameData, String username, ChessGame.TeamColor playerColor) throws DataAccessException {
        //update the game with the new player and color
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        switch (playerColor) {
            case WHITE -> {
                if (whiteUsername == null) {
                    whiteUsername = username;
                } else {
                    throw new DataAccessException("Error: color already taken");
                }
            }
            case BLACK -> {
                if (blackUsername == null) {
                    blackUsername = username;
                } else {
                    throw new DataAccessException("Error: color already taken");
                }
            }
        }
        return new GameData(gameData.gameID(), whiteUsername, blackUsername, gameData.gameName(), gameData.game());
    }

    public void join(JoinRequest joinRequest) throws DataAccessException {
        authenticate(joinRequest.authToken());
        GameData gameData = this.gameDAO.getGame(joinRequest.gameID());
        UserData userData = this.userDAO.getUser(this.authDAO.getAuth(joinRequest.authToken()).username());
        GameData newGameData = modifyGame(gameData, userData.username(), joinRequest.playerColor());
        this.gameDAO.updateGame(newGameData, joinRequest.gameID());
    }

    public void clear() {
        this.userDAO.clear();
        this.authDAO.clear();
        this.gameDAO.clear();
    }
}
