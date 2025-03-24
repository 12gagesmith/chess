package ui;

import server.ServerFacade;

public class Client {
    private final ServerFacade server;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        return null;
    }
}
