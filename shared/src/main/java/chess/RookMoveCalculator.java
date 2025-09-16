package chess;

import java.util.ArrayList;
import java.util.List;

public class RookMoveCalculator extends PieceMoveCalculator{
    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;
    int x;
    int y;

    List<ChessMove> myMoves = new ArrayList<>();


    RookMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
        this.board = board;
        this.piece = piece;
        this.position = position;
        x = position.getRow();
        y = position.getColumn();

    }


    public void quadOne(){
        y = position.getColumn() + 1;

        while (y < 9){
            if (board.isEmpty(x, y)){
                ChessMove.addMoves(myMoves, position, x, y);
                y++;
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
        }
        y = position.getColumn();
    }

    public void quadTwo(){
        x = position.getRow() + 1;

        while (x < 9){
            if (board.isEmpty(x, y)){
                ChessMove.addMoves(myMoves, position, x, y);
                x++;
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
        }
        x = position.getRow();
    }

    public void quadThree(){
        y = position.getColumn() - 1;

        while (y > 0){
            if (board.isEmpty(x, y)){
                ChessMove.addMoves(myMoves, position, x, y);
                y--;
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
        }
        y = position.getColumn();
    }

    public void quadFour(){
        x = position.getRow() - 1;

        while (x > 0){
            if (board.isEmpty(x, y)){
                ChessMove.addMoves(myMoves, position, x, y);
                x--;
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
        }
        x = position.getRow();
    }

    public List<ChessMove> CalculateMoves(){
        quadOne();
        quadTwo();
        quadThree();
        quadFour();
        return myMoves;
    }
}
