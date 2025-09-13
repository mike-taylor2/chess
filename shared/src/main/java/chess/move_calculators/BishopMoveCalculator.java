package chess.move_calculators;

import chess.ChessMove;

import java.util.List;

public class BishopMoveCalculator extends PieceMoveCalculator{

    public BishopMoveCalculator(){
    }

    public List<ChessMove> CalculateMoves(){
        this.leftDiagonalsMoves();
        return myMoves;
    }
}
