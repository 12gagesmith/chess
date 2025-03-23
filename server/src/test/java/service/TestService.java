package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.*;
import server.records.*;

import java.util.ArrayList;

public class TestService {

    private static Service service;
    private String authToken;

    public TestService() {
        this.authToken = "authToken";
    }

    @BeforeAll
    public static void init() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        service = new Service(userDAO, authDAO, gameDAO);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        RegisterResult registerResult = service.register(new RegisterRequest("gage", "smith", "myEmail"));
        this.authToken = registerResult.authToken();
    }

    @AfterEach
    public void clearOut() throws DataAccessException {
        service.clear();
    }

    @Test
    public void testRegister1() throws DataAccessException {
        RegisterResult actual = service.register(new RegisterRequest("gage2", "smith2", "myEmail"));
        RegisterResult expected = new RegisterResult("gage2", "authToken");
        Assertions.assertEquals(actual.username(), expected.username());
    }

    @Test
    public void testRegister2() {
        Assertions.assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("gage", "smith", "myEmail")));
        Assertions.assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest(null, null, null)));
        Assertions.assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("gage", null, null)));
        Assertions.assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("gage", "smith", null)));
    }

    @Test
    public void testLogin1() throws DataAccessException {
        LoginResult actual = service.login(new LoginRequest("gage", "smith"));
        LoginResult expected = new LoginResult("gage", "authToken");
        Assertions.assertEquals(actual.username(), expected.username());
    }

    @Test
    public void testLogin2() {
        Assertions.assertThrows(DataAccessException.class, () -> service.login(new LoginRequest("gage", "gage")));
        Assertions.assertThrows(DataAccessException.class, () -> service.login(new LoginRequest("smith", "gage")));
    }

    @Test
    public void testLogout1() {
        Assertions.assertDoesNotThrow(() -> service.logout(new LogoutRequest(this.authToken)));
    }

    @Test
    public void testLogout2() {
        Assertions.assertThrows(DataAccessException.class, () -> service.logout(new LogoutRequest("invalidAuth")));
    }

    @Test
    public void testCreate1() throws DataAccessException {
        CreateResult actual = service.create(new CreateRequest("gageGame"), this.authToken);
        Assertions.assertInstanceOf(Integer.class, actual.gameID());
    }

    @Test
    public void testCreate2() {
        Assertions.assertThrows(DataAccessException.class, () -> service.create(new CreateRequest(null), this.authToken));
    }

    @Test
    public void testList1() throws DataAccessException {
        ListResult actual = service.list(new ListRequest(this.authToken));
        Assertions.assertInstanceOf(ArrayList.class, actual.games());
    }

    @Test
    public void testList2() {
        Assertions.assertThrows(DataAccessException.class, () -> service.list(new ListRequest("invalidAuth")));
    }

    @Test
    public void testJoin1() throws DataAccessException {
        int gameID = service.create(new CreateRequest("gageGame"), this.authToken).gameID();
        Assertions.assertDoesNotThrow(() -> service.join(new JoinRequest(ChessGame.TeamColor.BLACK, gameID), this.authToken));
    }

    @Test
    public void testJoin2() throws DataAccessException {
        int gameID = service.create(new CreateRequest("newGame"), this.authToken).gameID();
        service.join(new JoinRequest(ChessGame.TeamColor.BLACK, gameID), this.authToken);
        service.join(new JoinRequest(ChessGame.TeamColor.WHITE, gameID), this.authToken);
        Assertions.assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(ChessGame.TeamColor.BLACK, gameID), this.authToken));
        Assertions.assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(ChessGame.TeamColor.WHITE, gameID), this.authToken));
        Assertions.assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(null, null), this.authToken));
        Assertions.assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(ChessGame.TeamColor.BLACK, null), this.authToken));
        Assertions.assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(ChessGame.TeamColor.BLACK, 9999), this.authToken));
    }

    @Test
    public void testClear1() throws DataAccessException {
        service.create(new CreateRequest("gageGame"), this.authToken);
        Assertions.assertDoesNotThrow(() -> service.clear());
    }

    @Test
    public void testClear2() {
        Assertions.assertDoesNotThrow(() -> service.clear());
        Assertions.assertDoesNotThrow(() -> service.register(new RegisterRequest("gage", "smith", "myEmail")));
    }
}
