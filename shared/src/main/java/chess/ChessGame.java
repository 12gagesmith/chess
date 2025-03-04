package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard myBoard;
    private TeamColor teamTurn;

    public ChessGame() {
        this.myBoard = new ChessBoard();
        myBoard.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return teamTurn; }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { teamTurn = team; }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = myBoard.getPiece(startPosition);
        if (myPiece == null) {return null;}
        Collection<ChessMove> moves = myPiece.pieceMoves(myBoard, startPosition);
        ArrayList<ChessMove> movesToRemove = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessBoard tempBoard = myBoard.clone();
            myBoard.addPiece(move.getEndPosition(), myPiece);
            myBoard.addPiece(move.getStartPosition(), null);
            if (isInCheck(myPiece.getTeamColor())) {
                movesToRemove.add(move);
            }
            setBoard(tempBoard);
        }
        for (ChessMove move : movesToRemove) {
            moves.remove(move);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor turn = getTeamTurn();
        ArrayList<ChessMove> moves = (ArrayList<ChessMove>) validMoves(move.getStartPosition());
        ChessPiece myPiece = myBoard.getPiece(move.getStartPosition());
        if (myPiece != null && turn == myPiece.getTeamColor() && moves.contains(move)) {
            if (move.getPromotionPiece() != null) {
                myPiece = new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece());
            }
            myBoard.addPiece(move.getEndPosition(), myPiece);
            myBoard.addPiece(move.getStartPosition(), null);
            switch (turn) {
                case BLACK -> setTeamTurn(TeamColor.WHITE);
                case WHITE -> setTeamTurn(TeamColor.BLACK);
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        ChessPosition currPosition;
        ChessPiece currPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currPosition = new ChessPosition(i, j);
                currPiece = myBoard.getPiece(currPosition);
                if (currPiece != null
                        && currPiece.getPieceType() == ChessPiece.PieceType.KING
                        && currPiece.getTeamColor() == teamColor) {
                    return currPosition;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        ChessPosition currPosition;
        ChessPiece currPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currPosition = new ChessPosition(i, j);
                currPiece = myBoard.getPiece(currPosition);
                if (currPiece == null || currPiece.getTeamColor() == teamColor) {
                    continue;
                }
                Collection<ChessMove> moves = currPiece.pieceMoves(myBoard, currPosition);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Collection<ChessMove> findAllValidMoves(TeamColor teamColor) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition currPosition;
        ChessPiece currPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currPosition = new ChessPosition(i, j);
                currPiece = myBoard.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    moves.addAll(validMoves(currPosition));
                }
            }
        }
        return moves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        boolean kingCheck = isInCheck(teamColor) && validMoves(kingPosition).isEmpty();
        if (!kingCheck) {return false;}
        ArrayList<ChessMove> allValidMoves = (ArrayList<ChessMove>) findAllValidMoves(teamColor);
        return allValidMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {return false;}
        ArrayList<ChessMove> allValidMoves = (ArrayList<ChessMove>) findAllValidMoves(teamColor);
        return allValidMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { myBoard = board; }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return myBoard; }
}
