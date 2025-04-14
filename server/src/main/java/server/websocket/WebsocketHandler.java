package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import serverfacade.DataAccessException;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.*;

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
        UserGameCommand usc = new Gson().fromJson(message, UserGameCommand.class);
        if (usc.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
            MakeMoveCommand mmc = new Gson().fromJson(message, MakeMoveCommand.class);
            if (mmc.move.getStartPosition().equals(mmc.move.getEndPosition())) {
                highlight(mmc.getAuthToken(), mmc.getGameID(), mmc.move);
            } else {
                make_move(mmc.getAuthToken(), mmc.getGameID(), mmc.move, session);
            }
        }
        switch (usc.getCommandType()) {
            case CONNECT -> connect(usc.getAuthToken(), usc.getGameID(), session);
            case LEAVE -> leave(usc.getAuthToken(), usc.getGameID());
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
                return;
            }
            String user = authData.username();
            connections.add(user, session);
            if (gameData == null) {
                ServerMessage errorMessage = new ErrorMessage("Invalid game ID");
                connections.sendOne(user, errorMessage);
                return;
            }
            String message;
            if (user.equals(gameData.whiteUsername())) {
                message = String.format("%s has joined the game as WHITE", user);
            } else if (user.equals(gameData.blackUsername())) {
                message = String.format("%s has joined the game as BLACK", user);
            } else {
                message = String.format("%s is observing the game", user);
            }
            ServerMessage notificationMessage = new NotificationMessage(message);
            ServerMessage gameMessage = new LoadGameMessage(gameData, null);
            connections.sendOne(user, gameMessage);
            connections.broadcast(user, notificationMessage);
        } catch (IOException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private void make_move(String authToken, int gameID, ChessMove move, Session session) throws DataAccessException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                ServerMessage errorMessage = new ErrorMessage("Invalid authorization");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            String user = authData.username();
            ChessGame.TeamColor userColor;
            if (user.equals(gameData.whiteUsername())) {
                userColor = WHITE;
            } else if (user.equals(gameData.blackUsername())) {
                userColor = BLACK;
            } else {
                sendError(user, "You cannot move a piece when observing");
                return;
            }
            if (!userColor.equals(game.getTeamTurn())) {
                sendError(user, "It is not your turn");
                return;
            }
            ChessBoard board = game.getBoard();
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece == null) {
                sendError(user, "There is no piece at the starting position");
                return;
            } else if (!piece.getTeamColor().equals(userColor)) {
                sendError(user, "That is not your color piece");
                return;
            }
            // Handling promotion piece
            if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                if (piece.getTeamColor().equals(WHITE) && move.getEndPosition().getRow() == 8) {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN);
                } else if (piece.getTeamColor().equals(BLACK) && move.getEndPosition().getRow() == 1) {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN);
                }
            }
            Collection<ChessMove> moves = game.validMoves(move.getStartPosition());
            if (!moves.contains(move)) {
                sendError(user, "That move is illegal. To see valid moves, type 'highlight <POSITION>'");
                return;
            }
            game.makeMove(move);
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updatedGame, gameID);
            ServerMessage loadMessage = new LoadGameMessage(updatedGame, null);
            String startPosition = String.format("%s%s", getAlpha(move.getStartPosition().getColumn()), move.getStartPosition().getRow());
            String endPosition = String.format("%s%s", getAlpha(move.getEndPosition().getColumn()), move.getEndPosition().getRow());
            ServerMessage notificationMessage = new NotificationMessage(String.format("%s moved their %s from %s to %s",
                    user, piece.getPieceType(), startPosition, endPosition));
            connections.sendOne(user, loadMessage);
            connections.broadcast(user, loadMessage);
            connections.broadcast(user, notificationMessage);
        } catch (IOException | InvalidMoveException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private void sendError(String user, String message) throws IOException {
        ServerMessage errorMessage = new ErrorMessage(message);
        connections.sendOne(user, errorMessage);
    }

    private String getAlpha(int n) {
        return switch (n) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> null;
        };
    }

    private void highlight(String authToken, int gameID, ChessMove move) throws DataAccessException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            String user = authData.username();
            GameData gameData = gameDAO.getGame(gameID);
            ChessBoard board = gameData.game().getBoard();
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece == null) {
                sendError(user, "There is no piece at that position");
                return;
            }
            Collection<ChessMove> moves = gameData.game().validMoves(move.getStartPosition());
            Collection<ChessPosition> positions = new ArrayList<>();
            positions.add(move.getStartPosition());
            for (ChessMove eachMove : moves) {
                positions.add(eachMove.getEndPosition());
            }
            ServerMessage loadMessage = new LoadGameMessage(gameData, positions);
            connections.sendOne(user, loadMessage);
        } catch (IOException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private void leave(String authToken, int gameID) throws DataAccessException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            GameData gameData = gameDAO.getGame(gameID);
            String whiteUser = null;
            String blackUser = null;
            String user = authData.username();
            if (user.equals(gameData.whiteUsername())) {
                blackUser = gameData.blackUsername();
            } else {
                whiteUser = gameData.whiteUsername();
            }
            gameData = new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameData, gameID);
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
