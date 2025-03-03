package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.Service;
import service.records.*;
import spark.*;

public class Server {

    private final Service service;

    public Server() {
        this.service = new Service();
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
        RegisterResult registerResult = this.service.register(registerRequest);
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = this.service.login(loginRequest);
        return new Gson().toJson(loginResult);
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        LogoutRequest logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        this.service.logout(logoutRequest);
        return new Gson().toJson("");
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        ListRequest listRequest = new Gson().fromJson(req.body(), ListRequest.class);
        ListResult listResult = this.service.list(listRequest);
        return new Gson().toJson(listResult);
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        CreateRequest createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateResult createResult = this.service.create(createRequest);
        return new Gson().toJson(createResult);
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        this.service.join(joinRequest);
        return new Gson().toJson("");
    }

    private Object clear(Request req, Response res) {
        this.service.clear();
        return new Gson().toJson("");
    }
}
