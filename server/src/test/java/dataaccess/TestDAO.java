package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import serverFacade.DataAccessException;
import serverFacade.records.GameList;

import java.util.ArrayList;

public class TestDAO {

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static String authToken;

    @BeforeAll
    public static void init() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        AuthData authData = userDAO.createUser(new UserData("gage", "smith", "myEmail"));
        authDAO.createAuth(authData);
        authToken = authData.authToken();
        gameDAO.createGame("newGame");
    }

    @AfterEach
    public void clearOut() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuth();
        gameDAO.clearGames();
    }

    @Test
    public void testGetUserPositive() throws DataAccessException {
        Assertions.assertEquals(userDAO.getUser("gage"), new UserData("gage", "smith", "myEmail"));
    }

    @Test
    public void testGetUserNegative() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser("badUser"));
    }

    @Test
    public void testCreateUserPositive() throws DataAccessException {
        AuthData authData = userDAO.createUser(new UserData("gage2", "smith", "myEmail"));
        Assertions.assertInstanceOf(AuthData.class, authData);
    }

    @Test
    public void testCreateUserNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData(null, null, null)));
    }

    @Test
    public void testClearUsers() {
        Assertions.assertDoesNotThrow(() -> userDAO.clearUsers());
    }

    @Test
    public void testGetAuthPositive() throws DataAccessException {
        Assertions.assertEquals(authDAO.getAuth(authToken), new AuthData(authToken, "gage"));
    }

    @Test
    public void testGetAuthNegative() throws DataAccessException {
        Assertions.assertNull(authDAO.getAuth("badAuth"));
    }

    @Test
    public void testCreateAuthPositive() throws DataAccessException {
        AuthData originalData = new AuthData("newToken", "gage2");
        authDAO.createAuth(originalData);
        Assertions.assertEquals(authDAO.getAuth("newToken"), originalData);
    }

    @Test
    public void testCreateAuthNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData(null, null)));
    }

    @Test
    public void testDeleteAuthPositive() {
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(authToken));
    }

    @Test
    public void testDeleteAuthNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("badToken"));
    }

    @Test
    public void testClearAuth() {
        Assertions.assertDoesNotThrow(() -> authDAO.clearAuth());
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        ArrayList<GameList> testList = new ArrayList<>();
        testList.add(new GameList(1, null, null, "newGame"));
        Assertions.assertEquals(gameDAO.listGames(), testList);
    }

    @Test
    public void testListGamesNegative() throws DataAccessException {
        gameDAO.clearGames();
        Assertions.assertEquals(gameDAO.listGames(), new ArrayList<>());
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        GameData gameData = gameDAO.createGame("newGame");
        Assertions.assertInstanceOf(GameData.class, gameData);
    }

    @Test
    public void testCreateGameNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void testGetGamePositive() throws DataAccessException {
        Assertions.assertInstanceOf(GameData.class, gameDAO.getGame(1));
    }

    @Test
    public void testGetGameNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(null));
    }

    @Test
    public void testUpdateGamePositive() {
        GameData gameData = new GameData(1, "gage", null, "newGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(gameData, 1));
    }

    @Test
    public void testUpdateGameNegative() {
        GameData gameData = new GameData(1, "gage", null, "newGame", new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameData, null));
    }

    @Test
    public void testClearGames() {
        Assertions.assertDoesNotThrow(() -> gameDAO.clearGames());
    }
}
