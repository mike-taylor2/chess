package chess;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveCalculator {

    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;
    int x;
    int y;

    List<ChessMove> myMoves = new ArrayList<>();

    PawnMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position){
        this.board = board;
        this.piece = piece;
        this.position = position;

        x = position.getRow();
        y = position.getColumn();
    }

    private void  promotion(int x, int y){
        ChessMove.addMovesPromotion(myMoves, position, x, y, ChessPiece.PieceType.ROOK);
        ChessMove.addMovesPromotion(myMoves, position, x, y, ChessPiece.PieceType.KNIGHT);
        ChessMove.addMovesPromotion(myMoves, position, x, y, ChessPiece.PieceType.BISHOP);
        ChessMove.addMovesPromotion(myMoves, position, x, y, ChessPiece.PieceType.QUEEN);
    }

    private boolean isWhite(){
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    private void checkWhiteJump(){
        if (board.isEmpty(x+1, y)){
            if (x == 7){promotion(x+1, y);}
            else {ChessMove.addMoves(myMoves, position, x+1, y);}
        }
    }

    private void checkBlackJump(){
        if (board.isEmpty(x-1, y)){
            if (x == 2){promotion(x-1, y);}
            else {ChessMove.addMoves(myMoves, position, x-1, y);}
        }
    }

    private void checkWhiteLeap(){
        if (position.getRow() == 2){
            if (board.isEmpty(x+1, y) && board.isEmpty(x+2, y)){
                ChessMove.addMoves(myMoves, position, x+2, y);
            }
        }
    }

    private void checkBlackLeap(){
        if (position.getRow() == 7){
            if (board.isEmpty(x-1, y) && board.isEmpty(x-2, y)){
                ChessMove.addMoves(myMoves, position, x-2, y);
            }
        }
    }

    private void checkLeftWhiteEnemyJump() {
        if (y > 2 && y < 9 && x < 8) {
            if (!board.isEmpty(x + 1, y - 1)) {
                if (board.isOppColor(x + 1, y - 1, ChessGame.TeamColor.WHITE)) {
                    if (x == 7){promotion(x+1, y-1);}
                    else {ChessMove.addMoves(myMoves, position, x + 1, y - 1);}
                }
            }
        }
    }

    private void checkLeftBLackEnemyJump(){
        if (y > 1 && y < 9 && x > 1) {
            if (!board.isEmpty(x - 1, y - 1)) {
                if (board.isOppColor(x - 1, y - 1, ChessGame.TeamColor.BLACK)) {
                    if (x == 2){promotion(x-1, y-1);}
                    else {ChessMove.addMoves(myMoves, position, x - 1, y - 1);}
                }
            }
        }
    }

    private void checkRightWhiteEnemyJump() {
        if (y > 0 && y < 8 && x < 8) {
            if (!board.isEmpty(x + 1, y + 1)) {
                if (board.isOppColor(x + 1, y + 1, ChessGame.TeamColor.WHITE)) {
                    if (x==7){promotion(x+1, y+1);}
                    else {ChessMove.addMoves(myMoves, position, x + 1, y + 1);}
                }
            }
        }
    }

    private void checkRightBLackEnemyJump(){
        if (y > 0 && y < 8 && x > 1) {
            if (!board.isEmpty(x - 1, y + 1)) {
                if (board.isOppColor(x - 1, y + 1, ChessGame.TeamColor.BLACK)) {
                    if (x == 2){promotion(x-1, y+1);}
                    else {ChessMove.addMoves(myMoves, position, x - 1, y + 1);}
                }
            }
        }
    }



    public List<ChessMove> calculateMoves(){
        if (isWhite()){
            checkWhiteJump();
            checkWhiteLeap();
            checkLeftWhiteEnemyJump();
            checkRightWhiteEnemyJump();
        }
        else{
            checkBlackJump();
            checkBlackLeap();
            checkLeftBLackEnemyJump();
            checkRightBLackEnemyJump();
        }
        return myMoves;
    }
}
