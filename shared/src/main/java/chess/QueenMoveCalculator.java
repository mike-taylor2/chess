package chess;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveCalculator {
    ChessBoard board;
    ChessPiece piece;
    ChessPosition position;

    List<ChessMove> myMoves = new ArrayList<>();

    QueenMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position){
        this.board = board;
        this.piece = piece;
        this.position = position;
    }

    List<ChessMove> CalculateMoves(){
        List<ChessMove> axisMoves = new RookMoveCalculator(board, piece, position).CalculateMoves();
        List<ChessMove> diagonalMoves = new BishopMoveCalculator(board, piece, position).CalculateMoves();

        myMoves.addAll(axisMoves);
        myMoves.addAll(diagonalMoves);

        return myMoves;
    }
}
