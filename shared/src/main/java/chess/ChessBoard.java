package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    public boolean isEmpty(int x_cor, int y_cor) {return squares[x_cor - 1][y_cor -1] == null;}

    public boolean legalDiagonal(ChessPosition myPosition, int x, int y){
        int newXPosition = myPosition.getRow() + x;
        int newYPosition = myPosition.getColumn() + y;

        if (newXPosition < 1 || newYPosition < 1) {return false;}
        if (newXPosition > 8 || newYPosition > 8) {return false;}

        if (this.isEmpty(myPosition.getRow() + x, myPosition.getColumn() + y)){ return true;}
        if (this.isOppColor(myPosition.getRow()+x, myPosition.getColumn()+y, this.getPiece(new ChessPosition(newXPosition, newYPosition)).getTeamColor())){return true;}
        return false;
    }

    public boolean isOppColor(int x, int y, ChessGame.TeamColor color){
        return squares[x-1][y-1].getTeamColor() != color;
    }

    @Override
    public boolean equals(Object o) {
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
}
