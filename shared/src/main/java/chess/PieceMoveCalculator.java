package chess;

import java.util.List;

public abstract class PieceMoveCalculator {

    public PieceMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position){
    }

    public abstract void quadOne();

    public abstract void quadTwo();

    public abstract void quadThree();

    public abstract void quadFour();
}
