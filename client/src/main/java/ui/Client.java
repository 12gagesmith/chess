package ui;

import server.DataAccessException;
import server.ServerFacade;

import java.util.Arrays;

public class Client {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help() {
        switch (state) {
            case State.PLAYING -> {
                return "TODO: UI during gameplay";
            }
            case State.SIGNEDIN -> {
                return """
                        logout - log out of your account
                        create <NAME> - create a chess game
                        list - list all chess games
                        join <ID> <WHITE|BLACK> - join a game as white or black
                        observe <ID> - observe a game
                        help - display possible commands
                        quit - exit the program
                        """;
            }
            default -> {
                return """
                        login <USERNAME> <PASSWORD> - log in to your account
                        register <USERNAME> <PASSWORD> <EMAIL> - create an account
                        help - display possible commands
                        quit - exit the program
                        """;
            }
        }
    }
}
