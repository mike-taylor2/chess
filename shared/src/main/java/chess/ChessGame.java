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

    public ChessGame() {
        myBoard.resetBoard();
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
        return myValidMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
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
                    if (!safeMove(kingPosition, enemyPosition)){return true;}}
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
        throw new RuntimeException("Not implemented");
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

    public boolean safeMove(ChessPosition kingPosition, ChessPosition enemyPosition){
        ChessPiece enemyPiece = myBoard.getPiece(enemyPosition);
        var enemyMoves = enemyPiece.pieceMoves(myBoard, enemyPosition);
        for (ChessMove move : enemyMoves){
            if (move.contains(kingPosition)){
                return false;
            }
        }
        return true;
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

