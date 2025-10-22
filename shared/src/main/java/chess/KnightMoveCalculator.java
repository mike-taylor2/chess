package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnightMoveCalculator {
    ChessBoard board;
    ChessPiece piece;
    ChessPosition position;
    int x;
    int y;

    List<ChessMove> myMoves = new ArrayList<>();

    List<List<Integer>> movementCombos = Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(2, 1),
            Arrays.asList(2, -1),
            Arrays.asList(1, -2),
            Arrays.asList(-1, -2),
            Arrays.asList(-2, -1),
            Arrays.asList(-2, 1),
            Arrays.asList(-1, 2)
    );

    KnightMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position){
        this.board = board;
        this.piece = piece;
        this.position = position;
        x = position.getRow();
        y = position.getColumn();
    }

    public static boolean inBounds(int x, int y){
        return (0<x && x<9 && 0<y && y<9);
    }

    List<ChessMove> calculateMoves(){
        for (List<Integer> pair : movementCombos){
            x = position.getRow() + pair.get(0);
            y = position.getColumn() + pair.get(1);
            if (inBounds(x, y) && (board.isEmpty(x, y) || board.isOppColor(x, y, piece.getTeamColor()))){
                ChessMove.addMoves(myMoves, position, x, y);
            }
        }
        return myMoves;
    }
}
