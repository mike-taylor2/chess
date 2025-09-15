package chess;

import java.util.ArrayList;
import java.util.List;

public class BishopMoveCalculator extends PieceMoveCalculator{

    private int x;
    private int y;
    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;

    BishopMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
        x = position.getRow();
        y = position.getColumn();

        this.board = board;
        this.piece = piece;
        this.position = position;

    }

    List <ChessMove> myMoves = new ArrayList<>();

    public void quadOne(){
        x = position.getRow() + 1;
        y = position.getColumn() + 1;

        while (x<9 && y<9){
            if (board.isEmpty(x,y)){
                ChessMove.addMoves(myMoves, position, x, y);
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
            x++;
            y++;
        }
    }

    public void quadTwo(){
        x = position.getRow() + 1;
        y = position.getColumn() - 1;

        while (x<9 && y>0){
            if (board.isEmpty(x,y)){
                ChessMove.addMoves(myMoves, position, x, y);
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
            x++;
            y--;
        }
    }

    public void quadThree(){
        x = position.getRow() - 1;
        y = position.getColumn() - 1;

        while (x>0 && y>0){
            if (board.isEmpty(x,y)){
                ChessMove.addMoves(myMoves, position, x, y);
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
            x--;
            y--;
        }
    }

    public void quadFour(){
        x = position.getRow() - 1;
        y = position.getColumn() + 1;

        while (x>0 && y<9){
            if (board.isEmpty(x,y)){
                ChessMove.addMoves(myMoves, position, x, y);
            }
            else if (board.isOppColor(x, y, piece.getTeamColor())){
                ChessMove.addMoves(myMoves, position, x, y);
                break;
            }
            else{
                break;
            }
            x--;
            y++;
        }
    }

    public List<ChessMove> CalculateMoves(){
        quadOne();
        quadTwo();
        quadThree();
        quadFour();
        return myMoves;
    }
}
