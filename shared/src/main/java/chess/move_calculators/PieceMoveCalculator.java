//package chess.move_calculators;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import chess.ChessMove;
//import chess.ChessBoard;
//import chess.ChessPosition;
//
//public class PieceMoveCalculator {
//
//    private final int x_corStart;
//    private final int y_corStart;
//    private final ChessBoard Board;
//    List<ChessMove> myMoves;
//
//
//
//    public PieceMoveCalculator(int x_corStart, int y_corStart, ChessBoard Board, List<ChessMove> myMoves){
//        this.x_corStart = x_corStart;
//        this.y_corStart = y_corStart;
//        this.Board = Board;
//        this.myMoves = myMoves;
//    }
//
//
//
//    public void leftDiagonalsMoves(){
//        int x_cor = x_corStart - 1;
//
//
//        // Lower Diagonal
//        while (x_cor > 0){
//            for (int y_cor = 1; y_cor < y_corStart; y_cor++){
//                if (!Board.isEmpty(x_cor, y_cor)){
//                    break;
//                }
//                if (Math.abs(y_cor - y_corStart) == Math.abs(x_cor - x_corStart) && Board.isEmpty(x_cor, y_cor)){
//                    myMoves.add(new ChessMove(new ChessPosition(x_corStart, y_corStart), new ChessPosition(x_cor, y_cor), null));
//                }
//                x_cor--;
//            }
//            x_cor = x_corStart - 1;
//            // Upper Diagonal
//            for (int y_cor = y_corStart + 1; y_cor < 9; y_cor++){
//                if (!Board.isEmpty(x_cor, y_cor)){
//                    break;
//                }
//                if (Math.abs(y_cor - y_corStart) == Math.abs(x_cor - x_corStart) && Board.isEmpty(x_cor, y_cor)){
//                    myMoves.add(new ChessMove(new ChessPosition(x_corStart, y_corStart), new ChessPosition(x_cor, y_cor), null));
//                }
//                x_cor--;
//            }
//        }
//    }
//
//    public void rightDiagonalMoves(){
//
//    }
//
//    public void horizontalMoves(){
//
//    }
//
//    public void verticalMoves(){
//
//    }
//}
