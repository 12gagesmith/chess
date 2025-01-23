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

    private static boolean isSquareOpen(ChessBoard board, ChessPosition position, ChessPiece myPiece) {
        if (position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8) {
            return false;
        }
        ChessPiece otherPiece = board.getPiece(position);
        return otherPiece == null || canCapture(myPiece, otherPiece);
    }

    public static ArrayList<ChessMove> calculateKing(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
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
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
        if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
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
        return moves;
    }

    public static ArrayList<ChessMove> calculatePawn(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        return moves;
    }
}
