package ui;

import chess.*;
import serverfacade.DataAccessException;
import serverfacade.ServerFacade;
import serverfacade.records.*;
import ui.websocket.*;
import websocket.commands.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken = "";
    private ArrayList<GameNumMap> games = new ArrayList<>();
    private WebsocketFacade websocket;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;
    private String visitorName;
    private int curGameID;
    private String curColor;

    public Client(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
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
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
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
        if (params.length != 3) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR);
        }
        try {
            RegisterResult registerResult = server.register(params[0], params[1], params[2]);
            state = State.SIGNEDIN;
            this.authToken = registerResult.authToken();
            this.visitorName = registerResult.username();
            websocket = new WebsocketFacade(serverUrl, notificationHandler);
            System.out.print(SET_TEXT_COLOR_GREEN);
            return String.format("You registered as %s%s", visitorName, RESET_TEXT_COLOR);
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            return "Error: User already exists" + RESET_TEXT_COLOR;
        }
    }

    public String login(String... params) throws DataAccessException {
        assertState(State.SIGNEDOUT);
        if (params.length != 2) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR);
        }
        try {
            LoginResult loginResult = server.login(params[0], params[1]);
            state = State.SIGNEDIN;
            this.authToken = loginResult.authToken();
            this.visitorName = loginResult.username();
            websocket = new WebsocketFacade(serverUrl, notificationHandler);
            System.out.print(SET_TEXT_COLOR_GREEN);
            return String.format("You are logged in as %s%s", visitorName, RESET_TEXT_COLOR);
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            return "Error: Invalid username or password" + RESET_TEXT_COLOR;
        }
    }

    public String logout() throws DataAccessException {
        assertState(State.SIGNEDIN);
        server.logout(this.authToken);
        state = State.SIGNEDOUT;
        websocket = null;
        System.out.print(SET_TEXT_COLOR_GREEN);
        return "Successfully logged out" + RESET_TEXT_COLOR;
    }

    public String create(String... params) throws DataAccessException {
        assertState(State.SIGNEDIN);
        if (params.length != 1) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <NAME>" + RESET_TEXT_COLOR);
        }
        CreateResult createResult = server.createGame(this.authToken, params[0]);
        games.add(new GameNumMap(games.size(), createResult.gameID()));
        System.out.print(SET_TEXT_COLOR_GREEN);
        return "Your new game's # is: " + SET_TEXT_COLOR_YELLOW + createResult.gameID().toString() + RESET_TEXT_COLOR;
    }

    public String list() throws DataAccessException {
        assertState(State.SIGNEDIN);
        ListResult listResult = server.listGames(this.authToken);
        System.out.println(SET_TEXT_COLOR_YELLOW);
        int gameNum = 1;
        games = new ArrayList<>();
        for (GameList lst: listResult.games()) {
            games.add(new GameNumMap(gameNum, lst.gameID()));
            System.out.printf("Game #%s\nGame Name: %s\n", gameNum, lst.gameName());
            if (lst.whiteUsername() == null) { System.out.print("White Username: --available--\n");}
            else {System.out.print("White Username: " + lst.whiteUsername() + "\n");}
            if (lst.blackUsername() == null) { System.out.print("Black Username: --available--\n\n");}
            else {System.out.print("Black Username: " + lst.blackUsername() + "\n\n");}
            gameNum += 1;
        }
        return RESET_TEXT_COLOR;
    }

    public String join(String... params) throws DataAccessException {
        assertState(State.SIGNEDIN);
        if (params.length != 2) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <GAME#> <WHITE|BLACK>" + RESET_TEXT_COLOR);
        }
        int gameID;
        try {
            int gameNum = Integer.parseInt(params[0]);
            GameNumMap gameNumMap = games.get(gameNum - 1);
            gameID = gameNumMap.gameID();
            server.joinGame(params[1].toUpperCase(), gameID, this.authToken);
        } catch (DataAccessException e) {
            System.out.print(SET_TEXT_COLOR_RED + "Error: Invalid Color; not available");
            return RESET_TEXT_COLOR;
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.print("Error: Invalid game #. Type 'list' to view games");
            return RESET_TEXT_COLOR;
        }
        state = State.PLAYING;
        websocket.sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, null);
        curGameID = gameID;
        curColor = params[1].toUpperCase();
        return SET_TEXT_COLOR_YELLOW + String.format("You have joined game #%s", Integer.parseInt(params[0]))
                + RESET_TEXT_COLOR + RESET_BG_COLOR;
    }

    public String observe(String... params) throws DataAccessException {
        assertState(State.SIGNEDIN);
        if (params.length != 1) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <GAME#>" + RESET_TEXT_COLOR);
        }
        int gameID;
        try {
            int gameNum = Integer.parseInt(params[0]);
            GameNumMap gameNumMap = games.get(gameNum - 1);
            gameID = gameNumMap.gameID();
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.print("Error: Invalid game #. Type 'list' to view games");
            return RESET_TEXT_COLOR;
        }
        state = State.PLAYING;
        websocket.sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, null);
        curGameID = gameID;
        curColor = "WHITE";
        return SET_TEXT_COLOR_YELLOW + String.format("You are now observing game #%s", Integer.parseInt(params[0]))
                + RESET_TEXT_COLOR;
    }

    public String redraw() throws DataAccessException {
        assertState(State.PLAYING);
        websocket.sendCommand(UserGameCommand.CommandType.CONNECT, authToken, curGameID, null);
        return "";
    }

    public String leave() throws DataAccessException {
        assertState(State.PLAYING);
        state = State.SIGNEDIN;
        websocket.sendCommand(UserGameCommand.CommandType.LEAVE, authToken, curGameID, null);
        return SET_TEXT_COLOR_YELLOW + "You have left the game" + RESET_TEXT_COLOR;
    }

    public String move(String... params) throws DataAccessException {
        assertState(State.PLAYING);
        if (params.length != 1) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <STARTPOS/ENDPOS> (ex. e2e4)" + RESET_TEXT_COLOR);
        }
        String[] rawInput = params[0].split("");
        ChessPosition startPosition = new ChessPosition(Integer.parseInt(rawInput[1]), getNum(rawInput[0]));
        ChessPosition endPosition = new ChessPosition(Integer.parseInt(rawInput[3]), getNum(rawInput[2]));
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        websocket.sendCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, curGameID, move);
        return SET_TEXT_COLOR_YELLOW + "Move made. Please wait for other player to move" + RESET_TEXT_COLOR;
    }

    public String resign() throws DataAccessException {
        assertState(State.PLAYING);
        return "TODO: resign";
    }

    public String highlight(String... params) throws DataAccessException {
        assertState(State.PLAYING);
        if (params.length != 1) {
            System.out.print(SET_TEXT_COLOR_RED);
            throw new DataAccessException(400, "Expected: <POSITION>" + RESET_TEXT_COLOR);
        }
        String[] rawInput = params[0].split("");
        ChessPosition position = new ChessPosition(Integer.parseInt(rawInput[1]), getNum(rawInput[0]));
        ChessMove move = new ChessMove(position, position, null);
        websocket.sendCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, curGameID, move);
        return "";
    }

    public String help() {
        System.out.print(SET_TEXT_COLOR_BLUE);
        switch (state) {
            case State.PLAYING -> {
                return """
                        redraw - redraws the chess board
                        leave - leave the game
                        move <STARTPOS/ENDPOS> (ex. e2e4) - move a chess piece
                        resign - forfeit the game
                        highlight <POSITION> - highlights the legal moves for a piece
                        help - display possible commands
                        """ + RESET_TEXT_COLOR;
            }
            case State.SIGNEDIN -> {
                return """
                        logout - log out of your account
                        create <NAME> - create a chess game
                        list - list all chess games
                        join <GAME#> <WHITE|BLACK> - join a game as white or black
                        observe <GAME#> - observe a game
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
        if (state.equals(stateCheck)) {
            return;
        }
        System.out.print(SET_TEXT_COLOR_RED);
        switch (stateCheck) {
            case SIGNEDOUT -> throw new DataAccessException(400, "You must be signed out to use this command" + RESET_TEXT_COLOR);
            case SIGNEDIN -> {
                if (state.equals(State.SIGNEDOUT)) {
                    throw new DataAccessException(400, "You must be signed in to use this command" + RESET_TEXT_COLOR);
                } else {
                    throw new DataAccessException(400, "You must not be in a game to use this command" + RESET_TEXT_COLOR);
                }
            }
            default -> throw new DataAccessException(400, "You must be in a game to use this command" + RESET_TEXT_COLOR);
        }
    }

    public void printBoard(ChessGame game, Collection<ChessPosition> positions) {
        System.out.println("\n");
        boolean everyOther = true;
        ChessBoard board = game.getBoard();
        if (curColor.equals("BLACK")) {
            for (int y = 0; y <= 9; y++) {
                for (int x = 9; 0 <= x; x--) {
                    printRow(board, x, y, everyOther, positions);
                    everyOther = !everyOther;
                }
                System.out.print(RESET_BG_COLOR + "\n");
                everyOther = !everyOther;
            }
        } else {
            for (int y = 9; 0 <= y; y--) {
                for (int x = 0; x <= 9; x++) {
                    printRow(board, x, y, everyOther, positions);
                    everyOther = !everyOther;
                }
                System.out.print(RESET_BG_COLOR + "\n");
                everyOther = !everyOther;
            }
        }
    }

    private void printRow(ChessBoard board, int x, int y, boolean everyOther, Collection<ChessPosition> positions) {
        ChessPosition curPosition = new ChessPosition(y, x);
        if (y == 0 || y == 9) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(getAlpha(x));
        } else if (x == 0 || x == 9) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + y + " ");
        } else {
            if (everyOther) {
                if (positions != null && positions.contains(curPosition)) {
                    System.out.print(SET_BG_COLOR_GREEN);
                } else {
                    System.out.print(SET_BG_COLOR_WHITE);
                }
            } else {
                if (positions != null && positions.contains(curPosition)) {
                    System.out.print(SET_BG_COLOR_DARK_GREEN);
                } else {
                    System.out.print(SET_BG_COLOR_BLUE);
                }
            }
            System.out.print(SET_TEXT_COLOR_BLACK);
            ChessPosition position = new ChessPosition(y, x);
            ChessPiece piece = board.getPiece(position);
            if (piece == null) {
                System.out.print(EMPTY);
            } else {
                printPiece(piece);
            }
        }
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

    private int getNum(String alpha) throws DataAccessException {
        return switch (alpha) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new DataAccessException(403, "Invalid position");
        };
    }
}
