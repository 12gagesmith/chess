package chess;

import java.util.ArrayList;

public class MoveCalculator {
    private static boolean canCapture(ChessPiece myPiece, ChessPiece otherPiece) {
        boolean isDifferent = myPiece.getTeamColor() != otherPiece.getTeamColor();
        if (myPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return isDifferent;
        }
        return false;
    }

    private static boolean inBounds(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }

    private static boolean isSquareOpen(ChessBoard board, ChessPosition position, ChessPiece myPiece) {
        if (!inBounds(position)) {
            return false;
        }
        ChessPiece otherPiece = board.getPiece(position);
        return otherPiece == null || canCapture(myPiece, otherPiece);
    }

    private static ArrayList<ChessMove> goStraight(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves, String direction) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        switch (direction) {
            case "up" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn());
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
            case "down" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn());
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
            case "left" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow(), nextPosition.getColumn() - 1);
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
            case "right" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow(), nextPosition.getColumn() + 1);
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
        }
        return moves;
    }

    public static ArrayList<ChessMove> calculateKing(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        // These two lines get the next position, check if that square's open, then adds it to moves if it is.
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        // Repeat 7 more times
        nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        return moves;
    }

    public static ArrayList<ChessMove> calculateQueen(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        return moves;
    }

    public static ArrayList<ChessMove> calculateBishop(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        return moves;
    }

    public static ArrayList<ChessMove> calculateKnight(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        // These two lines get the next position, check if that square's open, then adds it to moves if it is.
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        // Repeat 7 more times
        nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        nextPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        return moves;
    }

    public static ArrayList<ChessMove> calculateRook(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        moves = goStraight(board, myPiece, myPosition, moves, "up");
        moves = goStraight(board, myPiece, myPosition, moves, "down");
        moves = goStraight(board, myPiece, myPosition, moves, "left");
        moves = goStraight(board, myPiece, myPosition, moves, "right");
        return moves;
    }

    public static ArrayList<ChessMove> calculatePawn(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        return moves;
    }
}
