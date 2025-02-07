package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    private void addPawns(ChessGame.TeamColor color, int row) {
        ChessPiece nextPiece;
        ChessPosition nextPosition;
        for (int i = 1; i <= 8; i++) {
            nextPiece = new ChessPiece(color, ChessPiece.PieceType.PAWN);
            nextPosition = new ChessPosition(row, i);
            addPiece(nextPosition, nextPiece);
        }
    }

    private void addRow(ChessGame.TeamColor color, int row) {
        ChessPiece nextPiece = null;
        ChessPosition nextPosition;
        for (int i = 1; i <= 8; i++) {
            switch (i) {
                case 1, 8 -> nextPiece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                case 2, 7 -> nextPiece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                case 3, 6 -> nextPiece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                case 4 -> nextPiece = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                case 5 -> nextPiece = new ChessPiece(color, ChessPiece.PieceType.KING);
            }
            nextPosition = new ChessPosition(row, i);
            addPiece(nextPosition, nextPiece);
        }
    }

    private ChessPiece[][] getBoard() {return squares;}

    private void setBoard(ChessPiece[][] board) {this.squares = board;}

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        addPawns(ChessGame.TeamColor.WHITE, 2);
        addPawns(ChessGame.TeamColor.BLACK, 7);
        addRow(ChessGame.TeamColor.WHITE, 1);
        addRow(ChessGame.TeamColor.BLACK, 8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            ChessPiece[][] clonedSquares = getBoard().clone();
            clone.setBoard(clonedSquares);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
