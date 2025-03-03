package service.records;

import chess.ChessGame;

public record JoinRequest(ChessGame.TeamColor playerColor, String gameID) {
}
