package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;


public class DrawBoard {
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;


    // ChessBoard Data
    private ChessBoard board;
    private String leftMostSquare = "WHITE";
    private String boardColor;

    public DrawBoard(ChessGame game, String boardColor) {
        this.board = game.getBoard();
        this.boardColor = boardColor;
    }


    public void draw() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if (boardColor.equals("WHITE")) { drawWhiteHeader(out);}
        else {drawBlackHeader(out);}

        drawChessBoard(out);

        if (boardColor.equals("WHITE")) { drawWhiteHeader(out);}
        else {drawBlackHeader(out);}
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print('\n');
    }

    private void drawWhiteHeader(PrintStream out) {

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print("   ");

        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(" ");

        out.println();
    }

    private void drawBlackHeader(PrintStream out) {

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print("   ");

        String[] headers = { "h", "g", "f", "e", "d", "c", "b", "a" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(" ");

        out.println();
    }

    private void drawHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(" ");
        printCoordinateText(out, headerText);
        out.print(" ");
    }

    private void printCoordinateText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);
    }

    private void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            printColumnNumbers(out, boardRow);
            out.print(" ");
            drawRowOfSquares(out, 8 - boardRow);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            printColumnNumbers(out, boardRow);
            out.print("  ");
            out.println();
        }
    }

    private void printColumnNumbers(PrintStream out, int boardRow){
        if (boardColor.equals("WHITE")){
            printCoordinateText(out, String.valueOf(8 - boardRow));
        }
        else {printCoordinateText(out, String.valueOf(boardRow + 1));}
    }

    private void drawRowOfSquares(PrintStream out, int boardRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if (leftMostSquare.equals("WHITE")) {
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(" ");
                    // print chess piece?
                    printChessPiece(out, boardRow, 8 - boardCol);
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(" ");
                    leftMostSquare = "BLACK";
                }
                else if (leftMostSquare.equals("BLACK")) {
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(" ");
                    // print chess piece?
                    printChessPiece(out, boardRow, 8 - boardCol);
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(" ");
                    leftMostSquare = "WHITE";
                }
            }
            if (leftMostSquare.equals("WHITE")) {leftMostSquare = "BLACK";}
            else {leftMostSquare = "WHITE";}
    }

    private void printChessPiece(PrintStream out, int row, int col) {
        if (boardColor.equals("BLACK")) {
            row = Math.abs(9-row);
            col = Math.abs(9-col);
        }
        var piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            out.print(" ");
            return;
        }
        var color = piece.getTeamColor();
        var type = piece.getPieceType();

        if (color == ChessGame.TeamColor.WHITE) {
            typeChecker(out, type, SET_TEXT_COLOR_RED);
        }
        else {
            typeChecker(out, type, SET_TEXT_COLOR_BLUE);
        }

        }

    private void typeChecker(PrintStream out, ChessPiece.PieceType type, String setTextColorBlue) {
        out.print(setTextColorBlue);
        if (type == ChessPiece.PieceType.KING){
            out.print("Q");}
        else if (type == ChessPiece.PieceType.QUEEN) {
            out.print("K");}
        else if (type == ChessPiece.PieceType.ROOK) {
            out.print("R");}
        else if (type == ChessPiece.PieceType.KNIGHT) {
            out.print("N");}
        else if (type == ChessPiece.PieceType.BISHOP) {
            out.print("B");}
        else {
            out.print("P");}
    }

}
