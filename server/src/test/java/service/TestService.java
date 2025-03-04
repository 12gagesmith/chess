package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.records.*;

import java.util.ArrayList;

public class TestService {

    private static Service service;
    private String authToken;

    public TestService() {
        this.authToken = "authToken";
    }

    @BeforeAll
    public static void init() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        service = new Service(userDAO, authDAO, gameDAO);
        service.register(new RegisterRequest("gage", "smith", "myEmail"));
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        LoginResult loginResult = service.login(new LoginRequest("gage", "smith"));
        this.authToken = loginResult.authToken();
    }

    @Test
    public void testRegister() throws DataAccessException {
        RegisterResult actual = service.register(new RegisterRequest("gage2", "smith2", "myEmail"));
        RegisterResult expected = new RegisterResult("gage2", "authToken");
        Assertions.assertEquals(actual.username(), expected.username());
        Assertions.assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("gage", "smith", "myEmail")));
    }

    @Test
    public void testLogin() throws DataAccessException {
        LoginResult actual = service.login(new LoginRequest("gage", "smith"));
        LoginResult expected = new LoginResult("gage", "authToken");
        Assertions.assertEquals(actual.username(), expected.username());
        Assertions.assertThrows(DataAccessException.class, () -> service.login(new LoginRequest("smith", "gage")));
    }

    @Test
    public void testLogout() {
        Assertions.assertDoesNotThrow(() -> service.logout(new LogoutRequest(this.authToken)));
        Assertions.assertThrows(DataAccessException.class, () -> service.logout(new LogoutRequest("invalidAuth")));
    }

    @Test
    public void testCreate() throws DataAccessException {
        CreateResult actual = service.create(new CreateRequest("gageGame"), this.authToken);
        Assertions.assertInstanceOf(Integer.class, actual.gameID());
        Assertions.assertThrows(DataAccessException.class, () -> service.create(new CreateRequest(null), this.authToken));
    }

    @Test
    public void testList() throws DataAccessException {
        ListResult actual = service.list(new ListRequest(this.authToken));
        Assertions.assertInstanceOf(ArrayList.class, actual.games());
        Assertions.assertThrows(DataAccessException.class, () -> service.list(new ListRequest("invalidAuth")));
    }

    @Test
    public void testJoin() throws DataAccessException {
        int gameID = service.create(new CreateRequest("gageGame"), this.authToken).gameID();
        Assertions.assertDoesNotThrow(() -> service.join(new JoinRequest(ChessGame.TeamColor.BLACK, gameID), this.authToken));
        Assertions.assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(ChessGame.TeamColor.BLACK, gameID), this.authToken));
    }

    @Test
    public void testClear() throws DataAccessException {
        service.create(new CreateRequest("gageGame"), this.authToken);
        Assertions.assertDoesNotThrow(() -> service.clear());
        Assertions.assertDoesNotThrow(() -> service.register(new RegisterRequest("gage", "smith", "myEmail")));
    }
}
