package chess;

//import chess.move_calculators.BishopMoveCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private ChessPosition position;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> myMoves = new ArrayList<>();


        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP){
//            var BishopMoves = new BishopMoveCalculator();
//            return BishopMoves.CalculateMoves();
            // Lower Left Diagonal
            for (int x_cor = myPosition.getRow() - 1; x_cor > 0; x_cor--){
                for (int y_cor = 1; y_cor < myPosition.getColumn(); y_cor++){
                    if (myPosition.isDiagonal(x_cor, y_cor)){
                        continue;
                    }
                    if (board.isEmpty(x_cor, y_cor)){
                        ChessMove.addMoves(myMoves, myPosition, x_cor, y_cor);
                    }
                    else {
                        x_cor = 0;
                        break;
                    }
                }

            }
            // Upper Left Diagonal
            for (int x_cor = myPosition.getRow() - 1; x_cor > 0; x_cor--) {
                for (int y_cor = myPosition.getColumn(); y_cor < 9; y_cor++) {
                    if (Math.abs(y_cor - myPosition.getColumn()) != Math.abs(x_cor - myPosition.getRow())) {
                        continue;
                    }
                    if (board.isEmpty(x_cor, y_cor)) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    } else {
                        x_cor = 0;
                        break;
                    }
                }
            }
        }



        if (piece.getPieceType() == PieceType.ROOK) {
            for (int x_cor = 1; x_cor < 9; x_cor++ ) {
                for (int y_cor = 1; y_cor < 9; y_cor++ ) {
                    if (x_cor == myPosition.getRow() && y_cor == myPosition.getColumn()) {
                        continue;
                    }
                    if (x_cor == myPosition.getRow() || y_cor == myPosition.getColumn()) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                }
            }
        }
        if (piece.getPieceType() == PieceType.KING) {
            for (int x_cor = 1; x_cor < 9; x_cor++ ) {
                for (int y_cor = 1; y_cor < 9; y_cor++ ) {
                    if (x_cor == myPosition.getRow() && y_cor == myPosition.getColumn()) {
                        continue;
                    }
                    if (x_cor == myPosition.getRow()-1 && y_cor == myPosition.getColumn()) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow()-1 && y_cor == myPosition.getColumn()+1) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow() && y_cor == myPosition.getColumn()+1) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow()+1 && y_cor == myPosition.getColumn()+1) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow()+1 && y_cor == myPosition.getColumn()) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow()+1 && y_cor == myPosition.getColumn()-1) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow() && y_cor == myPosition.getColumn()-1) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                    if (x_cor == myPosition.getRow()-1 && y_cor == myPosition.getColumn()-1) {
                        myMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(x_cor, y_cor), null));
                    }
                }
            }
        }
        return myMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, position);
    }
}
