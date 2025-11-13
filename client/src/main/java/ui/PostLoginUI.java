package ui;

import exception.ResponseException;
import model.CreateGameRequest;
import model.GameData;
import model.JoinGameRequest;
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

        while (!result.contains("Joined") && !result.contains("out")) {
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
            if (result.contains("BLACK")) {}
            else {}
//            GameplayUI gameplayUI = new GameplayUI(server);
//            gameplayUI.run();
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
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
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

    public String listGames() throws ResponseException {
        try {
            var gameList = server.listGames().games();
            return gameList.toString();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public String logout() throws ResponseException {
        try {
            server.logout();
            return "Success: Logged out" + "\n";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public String joinGame(String ... params) {
        if (params.length < 2) {
            return "Error: missing gameID or Team Color Selection";
        }
        var game = new JoinGameRequest(params[1], Integer.parseInt(params[0]));
        try {
            server.joinGame(game);
            return "Success: joined game as " + String.format("%s", params[1]);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public String observeGame(String ... params) {
        if (params.length < 1) {
            return "Error: must input gameID number";
        }
        if (gameExists(params[0])) {
            return "Success: observing game " + String.format("%s", params[0]);
        }
        return "Error: invalid game ID";
    }

    private boolean gameExists(String ID) {
        try {
            var gameList = server.listGames().games();
            for (GameData game : gameList) {
                if (game.gameID() == Integer.parseInt(ID)) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}