package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import serverfacade.DataAccessException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebsocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
            case MAKE_MOVE -> make_move();
            case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case RESIGN -> resign();
        }
    }

    private void connect(String authToken, int gameID, Session session) throws DataAccessException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            GameData gameData = gameDAO.getGame(gameID);
            if (authData == null) {
                ServerMessage errorMessage = new ErrorMessage("Invalid authorization");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            }
            String user = authData.username();
            String color = "WHITE";
            connections.add(user, session);
            if (gameData == null) {
                ServerMessage errorMessage = new ErrorMessage("Invalid game ID");
                connections.sendOne(user, errorMessage);
            } else {
                String message;
                if (user.equals(gameData.whiteUsername())) {
                    message = String.format("%s has joined the game as WHITE", user);
                } else if (user.equals(gameData.blackUsername())) {
                    message = String.format("%s has joined the game as BLACK", user);
                    color = "BLACK";
                } else {
                    message = String.format("%s is observing the game", user);
                }
                ServerMessage notificationMessage = new NotificationMessage(message);
                ServerMessage gameMessage = new LoadGameMessage(gameData, color);
                connections.sendOne(user, gameMessage);
                connections.broadcast(user, notificationMessage);
            }
        } catch (IOException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private void make_move() {}

    private void leave(String authToken, int gameID) throws DataAccessException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            String user = authData.username();
            connections.remove(user);
            String message = String.format("%s has left the game", user);
            ServerMessage notificationMessage = new NotificationMessage(message);
            connections.broadcast(user, notificationMessage);
        } catch (IOException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private void resign() {}
}
