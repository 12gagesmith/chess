package chess;

import java.util.ArrayList;

public class MoveCalculator {
    private static boolean canCapture(ChessPiece myPiece, ChessPiece otherPiece) {
        if (otherPiece == null) {
            return false;
        }
        return myPiece.getTeamColor() != otherPiece.getTeamColor();
    }

    private static boolean inBounds(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }

    private static boolean isSquareOpen(ChessBoard board, ChessPosition position, ChessPiece myPiece) {
        if (!inBounds(position)) {
            return false;
        }
        ChessPiece otherPiece = board.getPiece(position);
        if (myPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return otherPiece == null || canCapture(myPiece, otherPiece);
        }
        return otherPiece == null;
    }

    private static void checkPromotion(ChessPosition myPosition, ChessPosition nextPosition, ArrayList<ChessMove> moves, int row) {
        if (nextPosition.getRow() == row) {
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.ROOK));
        } else {
            moves.add(new ChessMove(myPosition, nextPosition, null));
        }
    }

    private static void goStraight(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves, String direction) {
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
    }

    private static void goDiagonal(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves, String direction) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        switch (direction) {
            case "up_left" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn() - 1);
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
            case "up_right" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn() + 1);
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
            case "down_left" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn() - 1);
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
            case "down_right" -> {
                do {
                    nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn() + 1);
                    if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                } while (inBounds(nextPosition) && board.getPiece(nextPosition) == null);
            }
        }
    }

    public static void calculateKing(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
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
    }

    public static void calculateQueen(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        goStraight(board, myPiece, myPosition, moves, "up");
        goStraight(board, myPiece, myPosition, moves, "down");
        goStraight(board, myPiece, myPosition, moves, "left");
        goStraight(board, myPiece, myPosition, moves, "right");
        goDiagonal(board, myPiece, myPosition, moves, "up_left");
        goDiagonal(board, myPiece, myPosition, moves, "up_right");
        goDiagonal(board, myPiece, myPosition, moves, "down_left");
        goDiagonal(board, myPiece, myPosition, moves, "down_right");
    }

    public static void calculateBishop(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        goDiagonal(board, myPiece, myPosition, moves, "up_left");
        goDiagonal(board, myPiece, myPosition, moves, "up_right");
        goDiagonal(board, myPiece, myPosition, moves, "down_left");
        goDiagonal(board, myPiece, myPosition, moves, "down_right");
    }

    public static void calculateKnight(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
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
    }

    public static void calculateRook(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        goStraight(board, myPiece, myPosition, moves, "up");
        goStraight(board, myPiece, myPosition, moves, "down");
        goStraight(board, myPiece, myPosition, moves, "left");
        goStraight(board, myPiece, myPosition, moves, "right");
    }

    public static void calculatePawn(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves, int up_down, int proRow, int startRow) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + up_down, myPosition.getColumn());
        if (isSquareOpen(board, nextPosition, myPiece)) {
            checkPromotion(myPosition, nextPosition, moves, proRow);
        }
        if (myPosition.getRow() == startRow && board.getPiece(nextPosition) == null) {
            nextPosition = new ChessPosition(myPosition.getRow() + (2 * up_down), myPosition.getColumn());
            if (isSquareOpen(board, nextPosition, myPiece)) {moves.add(new ChessMove(myPosition, nextPosition, null));}
        }
        nextPosition = new ChessPosition(myPosition.getRow() + up_down, myPosition.getColumn() - 1);
        if (inBounds(nextPosition) && canCapture(myPiece, board.getPiece(nextPosition))) {checkPromotion(myPosition, nextPosition, moves, proRow);}
        nextPosition = new ChessPosition(myPosition.getRow() + up_down, myPosition.getColumn() + 1);
        if (inBounds(nextPosition) && canCapture(myPiece, board.getPiece(nextPosition))) {checkPromotion(myPosition, nextPosition, moves, proRow);}
    }

    public static void checkPawnColor(ChessBoard board, ChessPiece myPiece, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        switch (myPiece.getTeamColor()) {
            case WHITE -> calculatePawn(board, myPiece, myPosition, moves, 1, 8, 2);
            case BLACK -> calculatePawn(board, myPiece, myPosition, moves, -1, 1, 7);
        }
    }
}
