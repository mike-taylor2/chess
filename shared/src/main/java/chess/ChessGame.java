package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard myBoard = new ChessBoard();
    Boolean whiteTurn = true;
    List<ChessPosition> whitePositions = new ArrayList<>();
    List<ChessPosition> blackPositions = new ArrayList<>();
    ChessPosition whiteKing;
    ChessPosition blackKing;


    public ChessGame() {
        myBoard.resetBoard();
        whiteKing = new ChessPosition(1,5);
        blackKing = new ChessPosition(8,5);
        for (int c = 1; c<9; c++){
            whitePositions.add(new ChessPosition(1, c));
            whitePositions.add(new ChessPosition(2, c));
        }
        for (int c = 1; c<9; c++){
            blackPositions.add(new ChessPosition(7, c));
            blackPositions.add(new ChessPosition(8, c));
        }
//        boardHistory.add(myBoard.copy());
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (whiteTurn){return TeamColor.WHITE;}
        else {return TeamColor.BLACK;}
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whiteTurn = team == TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        List<ChessMove> myValidMoves = new ArrayList<>();

        ChessPiece myPiece = myBoard.getPiece(startPosition);
        var allMoves = myPiece.pieceMoves(myBoard, startPosition);

        for (ChessMove move : allMoves){
            if (legalMove(move)){
                myValidMoves.add(move);
            }
        }

        return myValidMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var startPosition = move.getStartPosition();
        var endPosition = move.getEndPosition();
        var piece = myBoard.getPiece(startPosition);

        if (myBoard.getPiece(startPosition) == null){throw new InvalidMoveException();}
        var color = piece.getTeamColor();
        var validMoves = validMoves(startPosition);
        if (color == TeamColor.WHITE && !whiteTurn){throw new InvalidMoveException();}
        if (color == TeamColor.BLACK && whiteTurn){throw new InvalidMoveException();}


        if (!validMoves.contains(move)){
            throw new InvalidMoveException();
        }


        if (move.getPromotionPiece() != null) {piece = new ChessPiece(color, move.getPromotionPiece());}

        if (myBoard.isEmpty(endPosition.getRow(), endPosition.getColumn())){
            updatePositions(color, startPosition, endPosition);
            myBoard.removePiece(startPosition);
            myBoard.addPiece(endPosition, piece);
        }
        else {
            var enemyPosition = endPosition;

            updatePositions(color, startPosition, endPosition);
            if (color == TeamColor.WHITE){blackPositions.remove(enemyPosition);}
            else{whitePositions.remove(enemyPosition);}
            myBoard.removePiece(enemyPosition);
            myBoard.removePiece(startPosition);
            myBoard.addPiece(endPosition, piece);
        }

        whiteTurn = !whiteTurn;

    }


    private boolean legalMove(ChessMove move){
        var startPosition = move.getStartPosition();
        var endPosition = move.getEndPosition();
        var piece = myBoard.getPiece(startPosition);
        var color = piece.getTeamColor();
        boolean answer;

        if (myBoard.isEmpty(endPosition.getRow(), endPosition.getColumn())){
            updatePositions(color, startPosition, endPosition);
            myBoard.removePiece(startPosition);
            myBoard.addPiece(endPosition, piece);
            answer = isInCheck(color);
            updatePositions(color, endPosition, startPosition);
            myBoard.removePiece(endPosition);
            myBoard.addPiece(startPosition, piece);
        }
        else if (myBoard.isOppColor(endPosition.getRow(), endPosition.getColumn(), color)){
            var enemyPosition = endPosition;
            var enemyPiece = myBoard.getPiece(enemyPosition);
            updatePositions(color, startPosition, endPosition);
            if (color == TeamColor.WHITE){blackPositions.remove(enemyPosition);}
            else{whitePositions.remove(enemyPosition);}
            myBoard.removePiece(enemyPosition);
            myBoard.removePiece(startPosition);
            myBoard.addPiece(endPosition, piece);
            answer = isInCheck(color);
            updatePositions(color, endPosition, startPosition);
            if (color == TeamColor.WHITE){blackPositions.add(enemyPosition);}
            else {whitePositions.add(enemyPosition);}
            myBoard.removePiece(endPosition);
            myBoard.addPiece(startPosition, piece);
            myBoard.addPiece(enemyPosition, enemyPiece);
        }
        else{
            answer = true;
        }
        return !answer;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE){
            for (ChessPosition enemyPosition : blackPositions){
                var enemyPiece = myBoard.getPiece(enemyPosition);
                var enemyMoves = enemyPiece.pieceMoves(myBoard, enemyPosition);
                for (ChessMove move : enemyMoves){
                    if (move.contains(whiteKing)){
                        return true;
                    }
                }
            }
            return false;
        }
        else{
            for (ChessPosition enemyPosition : whitePositions){
                var enemyPiece = myBoard.getPiece(enemyPosition);
                var enemyMoves = enemyPiece.pieceMoves(myBoard, enemyPosition);
                for (ChessMove move : enemyMoves){
                    if (move.contains(blackKing)){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void updatePositions(TeamColor color, ChessPosition removedPosition, ChessPosition addedPosition){
        var myPiece = myBoard.getPiece(removedPosition);
        if (color == TeamColor.WHITE){
            whitePositions.remove(removedPosition);
            whitePositions.add(addedPosition);
            if (myPiece.getPieceType() == ChessPiece.PieceType.KING){
                whiteKing = addedPosition;
            }
        }
        else{
            blackPositions.remove(removedPosition);
            blackPositions.add(addedPosition);
            if (myPiece.getPieceType() == ChessPiece.PieceType.KING){
                blackKing = addedPosition;
            }
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return scanBoardForMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }
        return scanBoardForMoves(teamColor);
    }

    private boolean scanBoardForMoves(TeamColor teamColor) {
        for (int r=1; r<9; r++){
            for (int c=1; c<9; c++){
                if (!myBoard.isEmpty(r,c) && myBoard.isSameColor(r,c,teamColor)){
                    var position = new ChessPosition(r,c);
                    if (!validMoves(position).isEmpty()) {return false;}
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.myBoard = board;
        whitePositions.clear();
        blackPositions.clear();

        for (int r=1; r<9; r++){
            for (int c=1; c<9; c++){
                if (myBoard.isEmpty(r,c)){
                    continue;}
                var piecePosition = new ChessPosition(r, c);
                var piece = myBoard.getPiece(piecePosition);
                if (piece.getTeamColor() == TeamColor.WHITE){
                    whitePositions.add(piecePosition);
                    if (piece.getPieceType() == ChessPiece.PieceType.KING){whiteKing = piecePosition;}
                }
                else{
                    blackPositions.add(piecePosition);
                    if (piece.getPieceType() == ChessPiece.PieceType.KING){blackKing = piecePosition;}
                }
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

    public ChessPiece getPiece(ChessPosition position) {return myBoard.getPiece(position);}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(myBoard, chessGame.myBoard) && Objects.equals(whiteTurn, chessGame.whiteTurn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myBoard, whiteTurn);
    }

    public boolean getWhiteTurn() {
        return whiteTurn;
    }


    public Collection<ChessPosition> getValidEndPositions(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = validMoves(startPosition);
        Collection<ChessPosition> validEndPositions = new ArrayList<>();
        for (ChessMove m : validMoves) {
            validEndPositions.add(m.getEndPosition());
        }
        return validEndPositions;
    }
}

