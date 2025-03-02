package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {

    private final UserService userService;

    public Server() {
        this.userService = new UserService();
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
        return null;
    }

    private Object logout(Request req, Response res) {
        return null;
    }

    private Object listGames(Request req, Response res) {
        return null;
    }

    private Object createGame(Request req, Response res) {
        return null;
    }

    private Object joinGame(Request req, Response res) {
        return null;
    }

    private Object clear(Request req, Response res) {
        return null;
    }
}
