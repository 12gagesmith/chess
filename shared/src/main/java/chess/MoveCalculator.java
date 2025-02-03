package chess;

import java.util.ArrayList;

public class MoveCalculator {

    private static boolean canCapture (ChessPiece myPiece, ChessPiece otherPiece) {
        if (otherPiece == null) {return false;}
        return (myPiece.getTeamColor() != otherPiece.getTeamColor());
    }

    private static boolean inBounds (ChessPosition nextPosition) {
        return nextPosition.getRow() >= 1 && nextPosition.getRow() <= 8 && nextPosition.getColumn() >= 1 && nextPosition.getColumn() <= 8;
    }

    private static boolean isSquareOpen(ChessBoard board, ChessPosition myPosition, ChessPosition nextPosition) {
        if (!inBounds(nextPosition) || myPosition.equals(nextPosition)) {return false;}
        ChessPiece otherPiece = board.getPiece(nextPosition);
        if (board.getPiece(myPosition).getPieceType() != ChessPiece.PieceType.PAWN) {
            return otherPiece == null || canCapture(board.getPiece(myPosition), otherPiece);
        }
        return otherPiece == null;
    }

    public static void kingCalculator(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        ChessPosition nextPosition;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                nextPosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
                if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
            }
        }
    }

    public static void knightCalculator(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        ChessPosition nextPosition;
        nextPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        nextPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
        if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
    }

    private static void goOneDirection(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves, int rowDir, int colDir) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        do {
            nextPosition = new ChessPosition(nextPosition.getRow() + rowDir, nextPosition.getColumn() + colDir);
            if (isSquareOpen(board, myPosition, nextPosition)) { moves.add(new ChessMove(myPosition, nextPosition, null)); }
        } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
    }

    public static void rookCalculator(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        goOneDirection(board, myPosition, moves, 1, 0);
        goOneDirection(board, myPosition, moves, -1, 0);
        goOneDirection(board, myPosition, moves, 0, 1);
        goOneDirection(board, myPosition, moves, 0, -1);
    }

    public static void bishopCalculator(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                goOneDirection(board, myPosition, moves, i, j);
            }
        }
    }

    public static void queenCalculator(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                goOneDirection(board, myPosition, moves, i, j);
            }
        }
    }

    private static void checkPromotion(ChessPosition myPosition, ChessPosition nextPosition, ArrayList<ChessMove> moves, int proRow) {
        if (nextPosition.getRow() == proRow) {
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myPosition, nextPosition, null));
        }
    }

    private static void pawnCalculator(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves, int proRow, int startRow, int direction) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (isSquareOpen(board, myPosition, nextPosition)) { checkPromotion(myPosition, nextPosition, moves, proRow); }
        if (myPosition.getRow() == startRow && board.getPiece(nextPosition) == null) {
            nextPosition = new ChessPosition(myPosition.getRow() + (2*direction), myPosition.getColumn());
            if (isSquareOpen(board, myPosition, nextPosition)) { checkPromotion(myPosition, nextPosition, moves, proRow); }
        }
        for (int i = -1; i <= 1; i+=2) {
            nextPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + i);
            if (inBounds(nextPosition) && canCapture(board.getPiece(myPosition), board.getPiece(nextPosition))) { checkPromotion(myPosition, nextPosition, moves, proRow); }
        }
    }

    public static void checkPawnColor(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        switch (board.getPiece(myPosition).getTeamColor()) {
            case WHITE -> pawnCalculator(board, myPosition, moves, 8, 2, 1);
            case BLACK -> pawnCalculator(board, myPosition, moves, 1, 7, -1);
        }
    }
}