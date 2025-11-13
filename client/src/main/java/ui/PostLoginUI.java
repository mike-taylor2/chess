package ui;

import exception.ResponseException;
import model.CreateGameRequest;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;

    public PostLoginUI(ServerFacade server) {
        this.server = server;
    }


    public void run() {
        System.out.print(help());
        String result = "";
        Scanner scanner = new Scanner(System.in);

        while (!result.contains("Joined")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            }
            catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        if (result.contains("game")) {
            GameplayUI gameplayUI = new GameplayUI(server);
            gameplayUI.run();
        }
        else {
            PreLoginUI preUI = new PreLoginUI(server);
            preUI.run();
        }
    }

    public String help() {
        return """
                Here are the following game actions:
                - create <NAME>
                - list
                - join <ID> [WHITE|BLACK]
                - observe <ID>
                Other actions:
                - logout
                - help
                """;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
//                case "list" -> listGames(params);
//                case "join" -> joinGame(params);
//                case "observe" -> observeGame(params);
//                case "logout" -> logout(params);
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String ... params) throws ResponseException {
        if (params.length < 1) {
            return "Error: input game name";
        }
        var game = new CreateGameRequest(params[0]);
        try {
            var newGame = server.createGame(game);
            return "Success: Created new game " + String.format("(%d)", newGame.gameID());
        }
        catch (Exception e) {
            return e.getMessage();
        }

    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}