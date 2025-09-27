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
    List<ChessBoard> boardHistory = new ArrayList<>();

    ChessPosition positionOfKillPiece = null;

    public ChessGame() {
        myBoard.resetBoard();

        //initialize white and black position arrays here
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

        //Here, you must include the logic that if the King is in check, the only valid move is to move the king OR kill the offending piece


        return myValidMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)){throw new InvalidMoveException();}

        // Need to check for promotion
        // encapsulate addPiece() and removePiece()
        // Update black and white position arrays including accounting for when a piece is eliminated
        // You must also keep track of KillPiece to know who could kill the king.

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = locateKing(teamColor);
        for (int r = 1; r < 9; r++){
            for (int c = 1; c < 9; c++){
                if (!myBoard.isEmpty(r, c) && myBoard.isOppColor(r, c, teamColor)){
                    ChessPosition enemyPosition = new ChessPosition(r, c);
                    if (inCheckMove(kingPosition, enemyPosition)){return true;}}
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = locateKing(teamColor);
        //Create an anyValidMoves function that checks ValidMoves for ALL pieces on a team

        if (isInCheck(teamColor) && validMoves(kingPosition).isEmpty()){return true;}
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

    public boolean inCheckMove(ChessPosition kingPosition, ChessPosition enemyPosition){
        ChessPiece enemyPiece = myBoard.getPiece(enemyPosition);
        var enemyMoves = enemyPiece.pieceMoves(myBoard, enemyPosition);
        for (ChessMove move : enemyMoves){
            if (move.contains(kingPosition)){
                return true;
            }
        }
        return false;
    }

    private ChessPosition locateKing(TeamColor team){
            for (int r = 1; r < 9; r++){
                for (int c = 1; c < 9; c++){
                    if (!myBoard.isEmpty(r, c) && myBoard.isSameColor(r, c, team)){
                        var myPiece = (myBoard.getPiece(new ChessPosition(r,c)));
                        if (myPiece.getPieceType() == ChessPiece.PieceType.KING){
                            return new ChessPosition(r,c);
                        }
                    }
                }
            }
    return null;}

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
}

