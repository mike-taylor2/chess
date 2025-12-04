package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import facade.ServerFacade;
import model.ClientGameplayData;
import model.DataCoordinates;
import model.JoinGameRequest;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class GameplayUI implements ServerMessageHandler {
    private final ServerFacade server;
    private final String username;
    private final ClientGameplayData.Role role;
    private final int gameID;
    private final String authToken;
    private ChessGame game;
    private final WebSocketFacade ws;
    private final Scanner scanner;

    public GameplayUI(ServerFacade server, ClientGameplayData data) throws ResponseException {
        this.server = server;
        this.username = data.username();
        this.role = data.role();
        this.gameID = data.gameID();
        this.authToken = server.getAuthToken();
        this.game = new ChessGame();
        this.ws = new WebSocketFacade(server.getServerUrl(), this, authToken);
        this.scanner = new Scanner(System.in);

        var clientData = new ClientGameplayData(username, role, gameID);
        try {
            ws.joinGame(clientData);
        }
        catch (Exception e) {
            System.out.print("Error: unable to Connect");
        }
    }


    public void run() {
        System.out.print(help());
        String result = "";

        while (!result.contains("Leaving")) {
            prompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result + "\n");
            }
            catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }

        var postLoginUI = new PostLoginUI(server, username);
        postLoginUI.run();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
//                case "leave" -> leave();
                case "move" -> makeMove(params);
                case "resign" -> resign();
//                case "moves" -> legalMoves();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    // Local
    public String redraw() {
        DrawBoard board;
        if (role == ClientGameplayData.Role.BLACK) {
            board = new DrawBoard(game, "BLACK");
        }
        else {
            board = new DrawBoard(game, "WHITE");
        }
        board.draw();
        return "";
    }
    // WebsocketFacade
//    public String leave() {
//    }

    // WebsocketFacade
    public String makeMove(String ... params) {
        if (game.getWhiteTurn() && role == ClientGameplayData.Role.BLACK) {
            return "Error: It is not your turn";}
        else if (!game.getWhiteTurn() && role == ClientGameplayData.Role.WHITE) {
            return "Error: It is not your turn";}
        else if (role == ClientGameplayData.Role.OBSERVER) {
            return "Error: You can't make moves as an observer";}
        if (params.length < 2 || params[0].length() != 2 || params[1].length() != 2) {
            return "Error: input move in this format: 'a3 b4'";}

        char r0 = params[0].charAt(1);
        char c0 = params[0].charAt(0);
        char r1 = params[1].charAt(1);
        char c1 = params[1].charAt(0);

        DataCoordinates coordinates;
        try {
            coordinates = cleanCoordinates(r0, c0, r1, c1);
        } catch (Exception e) {
            return "Error: invalid coordinates entered. Inputs must be a-h and 1-8";}

        var startPosition = new ChessPosition(coordinates.r0(), coordinates.c0());
        var endPosition = new ChessPosition(coordinates.r1(), coordinates.c1());
        ChessPiece.PieceType piece = null;
        String letter;

        if (possiblePawnPromotion(startPosition, endPosition)) {
            System.out.print(
                    """
                    This move involves pawn promotion. Here are the following options for promotion:
                    -q (queen)
                    -r (rook)
                    -b (bishop)
                    -k (knight)
                    """);
            prompt();
            letter = scanner.nextLine().toLowerCase();
            try {
                piece = cleanPieceInput(letter);
            } catch (Exception e) {
                return "Error: bad piece type";}}

        var move = new ChessMove(startPosition, endPosition, piece);

        try {
            game.makeMove(move);
        } catch (Exception e) {
            return "Error: not a legal move";}

        var data = new ClientGameplayData(username, role, gameID);
        ws.makeMove(data, move);
        return "";}

    // WebsocketFacade
    public String resign() {
        if (game.getWhiteTurn() && role == ClientGameplayData.Role.BLACK) {
            return "Error: It is not your turn";}
        else if (!game.getWhiteTurn() && role == ClientGameplayData.Role.WHITE) {
            return "Error: It is not your turn";}
        else if (role == ClientGameplayData.Role.OBSERVER) {
            return "Error: You can't resign as an observer";}

        System.out.print(
                """
                Once you resign, the game is over. Are you sure you want to resign? y/n""");
        prompt();
        String letter = scanner.nextLine().toLowerCase();
        var data = new ClientGameplayData(username, role, gameID);
        if (letter.charAt(0) == 'y') {
            ws.resign(data);
            return "";
        } else {
            return "Not resigning";
        }
    }
    // Local
//    public String legalMoves() {
//
//    }

    public void notify(ServerMessage serverMessage) {
        // This is where the LOAD_GAME server message is received and client redraws board
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            var loadGame = (LoadGameMessage) serverMessage;
            game = loadGame.getGame();
            System.out.print("\n\n");
            redraw();
            System.out.println(SET_TEXT_COLOR_BLUE + "CONNECTED" + RESET_TEXT_COLOR);
        }
        else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            var notification = (NotificationMessage) serverMessage;
            System.out.println(SET_TEXT_COLOR_GREEN + notification.getMessage() + RESET_TEXT_COLOR);
        }
        else {
            var error = (ErrorMessage) serverMessage;
            System.out.println(SET_TEXT_COLOR_RED + error.getMessage() + RESET_TEXT_COLOR);
        }
        prompt();
    }

    public String help() {
        return """
                - redraw (board)
                - leave (leave game)
                - move <LOWERCASE_LETTER><NUMBER(1-8)><LOWERCASE_LETTER><NUMBER(1-8)>
                - resign
                - moves (highlight legal moves)
                - help
                """;
    }

    private void prompt() {
        System.out.print("\n" + ">>> ");
    }

    private boolean possiblePawnPromotion(ChessPosition start, ChessPosition end) {
        var board = game.getBoard();
        if (board.getPiece(start).getPieceType() != ChessPiece.PieceType.PAWN) {
            return false;
        }
        if (board.getPiece(start).getTeamColor() == WHITE) {
            return end.getRow() == 8;
        }
        else {
            return end.getRow() == 1;
        }
    }

    private DataCoordinates cleanCoordinates(char r0, char c0, char r1, char c1) throws ResponseException {
        Map<String, Integer> map0 = Map.of(
                "a", 1,
                "b", 2,
                "c", 3,
                "d", 4,
                "e", 5,
                "f", 6,
                "g", 7,
                "h", 8
        );
        int r0_, c0_, r1_, c1_;

        String _r0 = String.valueOf(r0);
        String _c0 = String.valueOf(c0).toLowerCase();
        String _r1 = String.valueOf(r1);
        String _c1 = String.valueOf(c1).toLowerCase();

        try {
            r0_ = Integer.parseInt(_r0);
            c0_ = map0.get(_c0);
            r1_ = Integer.parseInt(_r1);
            c1_ = map0.get(_c1);

            if (c0_ < 1 || c0_ > 8 || c1_ < 1 || c1_ > 8) {
                throw new ResponseException(ResponseException.Code.ServerError, "Bad input");
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, "Bad input");
        }
        return new DataCoordinates(r0_, c0_, r1_, c1_);
    }

    private ChessPiece.PieceType cleanPieceInput(String letter) {
        Map<String, ChessPiece.PieceType> map = Map.of(
                "q", ChessPiece.PieceType.QUEEN,
                "r", ChessPiece.PieceType.ROOK,
                "b", ChessPiece.PieceType.BISHOP,
                "k", ChessPiece.PieceType.KNIGHT
        );
        return map.get(letter);
    }
}
