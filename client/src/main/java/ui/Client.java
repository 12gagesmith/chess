package ui;

import chess.*;
import server.DataAccessException;
import server.ServerFacade;
import server.records.*;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken = "";

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> quit();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String quit() throws DataAccessException {
        if (state == State.SIGNEDIN) {
            logout();
        }
        return SET_TEXT_COLOR_BLUE + "Thanks for playing!" + RESET_TEXT_COLOR;
    }

    public String register(String... params) throws DataAccessException {
        assertState(State.SIGNEDOUT);
        if (params.length < 3) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR);
        }
        RegisterResult registerResult = server.register(params[0], params[1], params[2]);
        state = State.SIGNEDIN;
        this.authToken = registerResult.authToken();
        System.out.print(SET_TEXT_COLOR_GREEN);
        return String.format("You registered as %s%s", registerResult.username(), RESET_TEXT_COLOR);
    }

    public String login(String... params) throws DataAccessException {
        assertState(State.SIGNEDOUT);
        if (params.length < 2) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR);
        }
        LoginResult loginResult = server.login(params[0], params[1]);
        state = State.SIGNEDIN;
        this.authToken = loginResult.authToken();
        System.out.print(SET_TEXT_COLOR_GREEN);
        return String.format("You are logged in as %s%s", loginResult.username(), RESET_TEXT_COLOR);
    }

    public String logout() throws DataAccessException {
        assertState(State.SIGNEDIN);
        server.logout(this.authToken);
        state = State.SIGNEDOUT;
        System.out.print(SET_TEXT_COLOR_GREEN);
        return "Successfully logged out" + RESET_TEXT_COLOR;
    }

    public String create(String... params) throws DataAccessException {
        assertState(State.SIGNEDIN);
        if (params.length < 1) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <NAME>" + RESET_TEXT_COLOR);
        }
        CreateResult createResult = server.createGame(this.authToken, params[0]);
        System.out.print(SET_TEXT_COLOR_GREEN);
        return "Your new game's ID is: " + SET_TEXT_COLOR_YELLOW + createResult.gameID().toString() + RESET_TEXT_COLOR;
    }

    public String list() throws DataAccessException {
        assertState(State.SIGNEDIN);
        ListResult listResult = server.listGames(this.authToken);
        System.out.println(SET_TEXT_COLOR_YELLOW);
        for (GameList lst: listResult.games()) {
            System.out.printf("Game ID: %s\nGame Name: %s\nWhite Username: %s\nBlack Username: %s\n\n",
                    lst.gameID(), lst.gameName(), lst.whiteUsername(), lst.blackUsername());
        }
        return RESET_TEXT_COLOR;
    }

    public String join(String... params) throws DataAccessException {
        assertState(State.SIGNEDIN);
        if (params.length < 2) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <ID> <WHITE|BLACK>" + RESET_TEXT_COLOR);
        }
        server.joinGame(params[1], params[0], this.authToken);
        ChessGame game = new ChessGame();
        printBoard(game, params[1]);
        return RESET_TEXT_COLOR + RESET_BG_COLOR;
    }

    public String observe(String... params) throws DataAccessException {
        assertState(State.SIGNEDIN);
        return "";
    }

    public String help() {
        System.out.print(SET_TEXT_COLOR_BLUE);
        switch (state) {
            case State.PLAYING -> {
                return "TODO: UI during gameplay" + RESET_TEXT_COLOR;
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
                        """ + RESET_TEXT_COLOR;
            }
            default -> {
                return """
                        login <USERNAME> <PASSWORD> - log in to your account
                        register <USERNAME> <PASSWORD> <EMAIL> - create an account
                        help - display possible commands
                        quit - exit the program
                        """ + RESET_TEXT_COLOR;
            }
        }
    }

    private void assertState(State stateCheck) throws DataAccessException {
        if (!state.equals(stateCheck)) {
            System.out.print(SET_TEXT_COLOR_RED);
            switch (stateCheck) {
                case SIGNEDOUT -> {
                    throw new DataAccessException(400, "You must be signed out to use this command" + RESET_TEXT_COLOR);
                }
                case SIGNEDIN -> {
                    throw new DataAccessException(400, "You must be signed in to use this command" + RESET_TEXT_COLOR);
                }
                default -> {
                    throw new DataAccessException(400, "You must be playing a game to use this command" + RESET_TEXT_COLOR);
                }
            }
        }
    }

    private void printBoard(ChessGame game, String userColor) {
        int xStart;
        int yStart;
        boolean everyOther = true;
        ChessBoard board = game.getBoard();
        if (userColor.equals("BLACK")) {
            xStart = 9;
            yStart = 0;
        } else {
            xStart = 0;
            yStart = 9;
        }
        for (int y = yStart; 0 <= y; y--) {
            printRow(board, xStart, y, everyOther);
            everyOther = !everyOther;
        }
    }

    private void printRow(ChessBoard board, int xStart, int y, boolean everyOther) {
        for (int x = xStart; x <= 9; x++) {
            if (y == 0 || y == 9) {
                System.out.print(SET_BG_COLOR_DARK_GREEN);
                System.out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(getAlpha(x));
            } else if (x == 0 || x == 9) {
                System.out.print(SET_BG_COLOR_DARK_GREEN);
                System.out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(" " + y + " ");
            } else {
                if (everyOther) {
                    System.out.print(SET_BG_COLOR_WHITE);
                } else {
                    System.out.print(SET_BG_COLOR_BLUE);
                }
                System.out.print(SET_TEXT_COLOR_BLACK);
                ChessPosition position = new ChessPosition(y, x);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    System.out.print(EMPTY);
                } else {
                    printPiece(piece);
                }
                everyOther = !everyOther;
            }
        }
        System.out.print(RESET_BG_COLOR + "\n");
    }

    private void printPiece(ChessPiece piece) {
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            switch (piece.getPieceType()) {
                case KING -> System.out.print(WHITE_KING);
                case QUEEN -> System.out.print(WHITE_QUEEN);
                case ROOK -> System.out.print(WHITE_ROOK);
                case KNIGHT -> System.out.print(WHITE_KNIGHT);
                case BISHOP -> System.out.print(WHITE_BISHOP);
                default -> System.out.print(WHITE_PAWN);
            }
        } else {
            switch (piece.getPieceType()) {
                case KING -> System.out.print(BLACK_KING);
                case QUEEN -> System.out.print(BLACK_QUEEN);
                case ROOK -> System.out.print(BLACK_ROOK);
                case KNIGHT -> System.out.print(BLACK_KNIGHT);
                case BISHOP -> System.out.print(BLACK_BISHOP);
                default -> System.out.print(BLACK_PAWN);
            }
        }
    }

    private String getAlpha(int n) {
        return switch (n) {
            case 1 -> " a ";
            case 2 -> " b ";
            case 3 -> " c ";
            case 4 -> " d ";
            case 5 -> " e ";
            case 6 -> " f ";
            case 7 -> " g ";
            case 8 -> " h ";
            default -> "   ";
        };
    }
}
