package client;

import org.junit.jupiter.api.*;
import serverfacade.*;
import server.Server;
import serverfacade.records.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void setup() throws DataAccessException {
        RegisterResult registerResult = serverFacade.register("user", "pass", "email");
        this.authToken = registerResult.authToken();
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTest1() {
        Assertions.assertDoesNotThrow(() -> serverFacade.register("name", "word", "mail"));
    }

    @Test
    public void registerTest2() {
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.register("user", "pass", "email"));
    }

    @Test
    public void loginTest1() {
        Assertions.assertDoesNotThrow(() -> serverFacade.login("user", "pass"));
    }

    @Test
    public void loginTest2() {
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.login("bad", "bad"));
    }

    @Test
    public void logoutTest1() {
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(this.authToken));
    }

    @Test
    public void logoutTest2() {
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.logout("BadAuth"));
    }

    @Test
    public void listTest1() {
        Assertions.assertDoesNotThrow(() -> serverFacade.listGames(this.authToken));
    }

    @Test
    public void listTest2() {
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.listGames("BadAuth"));
    }

    @Test
    public void createTest1() {
        Assertions.assertDoesNotThrow(() -> serverFacade.createGame(this.authToken, "NewGame"));
    }

    @Test
    public void createTest2() {
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.createGame(this.authToken, null));
    }

    @Test
    public void joinTest1() throws DataAccessException {
        serverFacade.createGame(this.authToken, "NewGame");
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame("WHITE", 1, this.authToken));
    }

    @Test
    public void joinTest2() throws DataAccessException {
        serverFacade.createGame(this.authToken, "NewGame");
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.joinGame("WHITE", 100, this.authToken));
        Assertions.assertThrows(DataAccessException.class, () -> serverFacade.joinGame("WHIT", 1, this.authToken));
    }

    @Test
    public void clearTest() throws DataAccessException {
        serverFacade.createGame(this.authToken, "NewGame");
        Assertions.assertDoesNotThrow(() -> serverFacade.clear());
    }
}
