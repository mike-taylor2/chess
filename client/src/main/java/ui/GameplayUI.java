package ui;

import chess.ChessGame;
import exception.ResponseException;
import facade.ServerFacade;
import model.ClientGameplayData;
import model.JoinGameRequest;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayUI implements ServerMessageHandler {
    private final ServerFacade server;
    private final String username;
    private final ClientGameplayData.Role role;
    private final int gameID;
    private ChessGame game;
    private final WebSocketFacade ws;

    public GameplayUI(ServerFacade server, ClientGameplayData data) {
        this.server = server;
        this.username = data.username();
        this.role = data.role();
        this.gameID = data.gameID();
        this.game = new ChessGame();
        this.ws = new WebSocketFacade(server.getServerUrl(), this);

        redraw();
        ws.joinGame(username, gameID);
    }


    public void run() {
        System.out.print(help());
        String result = "";
        Scanner scanner = new Scanner(System.in);

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
                case "leave" -> leave();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "moves" -> legalMoves();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    // Local
    public void redraw() {
        DrawBoard board;
        if (role == ClientGameplayData.Role.BLACK) {
            board = new DrawBoard(game, "BLACK");
        }
        else {
            board = new DrawBoard(game, "WHITE");
        }
        board.draw();
    }
    // WebsocketFacade
    public String leave() {

    }
    // WebsocketFacade
    public String makeMove(String ... params) {

    }
    // WebsocketFacade
    public String resign() {

    }
    // Local
    public String legalMoves() {

    }

    public void notify(ServerMessage serverMessage) {

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
}
