package ui;

import server.DataAccessException;
import server.ServerFacade;
import server.records.*;

import java.util.Arrays;

public class Client {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken = "";

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create();
                case "list" -> list();
                case "join" -> join();
                case "observe" -> observe();
                default -> help();
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws DataAccessException {
        assertState(State.SIGNEDOUT);
        if (params.length < 3) {
            throw new DataAccessException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }
        RegisterResult registerResult = server.register(params[0], params[1], params[2]);
        state = State.SIGNEDIN;
        this.authToken = registerResult.authToken();
        return String.format("You registered as %s", registerResult.username());
    }

    public String login(String... params) throws DataAccessException {
        assertState(State.SIGNEDOUT);
        if (params.length < 2) {
            throw new DataAccessException(400, "Expected: <USERNAME> <PASSWORD>");
        }
        LoginResult loginResult = server.login(params[0], params[1]);
        state = State.SIGNEDIN;
        this.authToken = loginResult.authToken();
        return String.format("You are logged in as %s", loginResult.username());
    }

    public String logout() throws DataAccessException {
        assertState(State.SIGNEDIN);
        return "";
    }

    public String create() throws DataAccessException {
        assertState(State.SIGNEDIN);
        return "";
    }

    public String list() throws DataAccessException {
        assertState(State.SIGNEDIN);
        return "";
    }

    public String join() throws DataAccessException {
        assertState(State.SIGNEDIN);
        return "";
    }

    public String observe() throws DataAccessException {
        assertState(State.SIGNEDIN);
        return "";
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

    private void assertState(State stateCheck) throws DataAccessException {
        if (!state.equals(stateCheck)) {
            switch (stateCheck) {
                case SIGNEDOUT -> {
                    throw new DataAccessException(400, "You must be signed out to use this command");
                }
                case SIGNEDIN -> {
                    throw new DataAccessException(400, "You must be signed in to use this command");
                }
                default -> {
                    throw new DataAccessException(400, "You must be playing a game to use this command");
                }
            }
        }
    }
}
