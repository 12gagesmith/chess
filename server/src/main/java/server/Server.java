package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import service.records.*;
import spark.*;

public class Server {

    private final UserService userService;
    private final GameService gameService;

    public Server() {
        this.userService = new UserService();
        this.gameService = new GameService();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult registerResult = this.userService.register(registerRequest);
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = this.userService.login(loginRequest);
        return new Gson().toJson(loginResult);
    }

    private Object logout(Request req, Response res) {
        LogoutRequest logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        this.userService.logout(logoutRequest);
        return new Gson().toJson("");
    }

    private Object listGames(Request req, Response res) {
        ListRequest listRequest = new Gson().fromJson(req.body(), ListRequest.class);
        ListResult listResult = this.gameService.list(listRequest);
        return new Gson().toJson(listResult);
    }

    private Object createGame(Request req, Response res) {
        CreateRequest createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateResult createResult = this.gameService.create(createRequest);
        return new Gson().toJson(createResult);
    }

    private Object joinGame(Request req, Response res) {
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        this.gameService.join(joinRequest);
        return new Gson().toJson("");
    }

    private Object clear(Request req, Response res) {
        this.userService.clear();
        this.gameService.clear();
        return new Gson().toJson("");
    }
}
