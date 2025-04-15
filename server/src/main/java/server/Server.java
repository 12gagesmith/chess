package server;

import com.google.gson.Gson;
import dataaccess.*;
import server.websocket.WebsocketHandler;
import serverfacade.DataAccessException;
import serverfacade.records.*;
import service.Service;
import spark.*;

public class Server {

    private final Service service;
    private final WebsocketHandler websocketHandler;

    public Server() {
        try {
            UserDAO userDAO = new MySqlUserDAO();
            AuthDAO authDAO = new MySqlAuthDAO();
            GameDAO gameDAO = new MySqlGameDAO();
            this.service = new Service(userDAO, authDAO, gameDAO);
            this.websocketHandler = new WebsocketHandler(authDAO, gameDAO);
        } catch (DataAccessException ex) {
            throw new RuntimeException(String.format("Unable to start server: %s%n", ex.getMessage()));
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", websocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
        res.body(new Gson().toJson(errorMessage));
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
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization"));
        this.service.logout(logoutRequest);
        return "{}";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        ListRequest listRequest = new ListRequest(req.headers("authorization"));
        ListResult listResult = this.service.list(listRequest);
        return new Gson().toJson(listResult);
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        CreateRequest createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateResult createResult = this.service.create(createRequest, req.headers("authorization"));
        return new Gson().toJson(createResult);
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        this.service.join(joinRequest, req.headers("authorization"));
        return "{}";
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        this.service.clear();
        return "{}";
    }
}
