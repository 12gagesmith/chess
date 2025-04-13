package ui.websocket;

import com.google.gson.Gson;
import serverfacade.DataAccessException;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()){
                        case NOTIFICATION -> serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                        case LOAD_GAME -> serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        case ERROR -> serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                    }
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (URISyntaxException | IOException | DeploymentException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws DataAccessException {
        try {
            UserGameCommand usc = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(usc));
        } catch (IOException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }
}
