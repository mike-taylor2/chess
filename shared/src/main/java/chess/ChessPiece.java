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

        if (piece.getPieceType() == PieceType.BISHOP) {
            BishopMoveCalculator myBishopMoves = new BishopMoveCalculator(board, piece, myPosition);
            myMoves = myBishopMoves.CalculateMoves();
        }

        else if (piece.getPieceType() == PieceType.KING){
            KingMoveCalculator myKingMoves = new KingMoveCalculator(board, piece, myPosition);
            myMoves = myKingMoves.CalculateMoves();
        }

        else if (piece.getPieceType() == PieceType.KNIGHT){
        }

        else if (piece.getPieceType() == PieceType.QUEEN){
            QueenMoveCalculator myQueenMoves = new QueenMoveCalculator(board, piece, myPosition);
            myMoves = myQueenMoves.CalculateMoves();
        }

        else if (piece.getPieceType() == PieceType.ROOK){
            RookMoveCalculator myRookMoves = new RookMoveCalculator(board, piece, myPosition);
            myMoves = myRookMoves.CalculateMoves();
        }

        else if (piece.getPieceType() == PieceType.PAWN){

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
