package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, int gameID, Session session) {
        Connection connection = new Connection(visitorName, gameID, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, int gameID, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<>();
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName) && c.gameID == gameID) {
                    c.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void sendOne(String visitorName, ServerMessage message) throws IOException {
        Connection c = connections.get(visitorName);
        if (c.session.isOpen()) {
            c.send(new Gson().toJson(message));
        } else {
            connections.remove(visitorName);
        }
    }
}
