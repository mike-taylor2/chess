package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.CreateGameRequest;
import model.GameData;
import model.JoinGameRequest;
import serverFacade.ServerFacade;


import java.util.*;

public class PostLoginUI {
    private final ServerFacade server;
    private ArrayList<GameData> gameList = new ArrayList<>();
    private HashMap<Integer, Integer> numberToID = new HashMap<>();

    public PostLoginUI(ServerFacade server) {
        this.server = server;
    }


    public void run() {
        System.out.print(help());
        String result = "";
        Scanner scanner = new Scanner(System.in);

        while (!result.contains("Joined game") && !result.contains("Logged out")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result + "\n");
            }
            catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        if (result.contains("observer")) {
            var board = new DrawBoard(new ChessGame(), "WHITE");
            board.draw();
            run();
        }
        else if (result.contains("Joined game")) {
            if (result.contains("black")) {
                var board = new DrawBoard(new ChessGame(), "BLACK");
                board.draw();
                run();
            }
            else {
                var board = new DrawBoard(new ChessGame(), "WHITE");
                board.draw();
                run();
            }
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
            gameList.add(new GameData(newGame.gameID(), null, null, params[0], new ChessGame()));
            numberToID.put(gameList.size(), newGame.gameID());
            return "Success: Created new game " + String.format("(%d)", gameList.size());
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public String listGames() throws ResponseException {
        try {
            gameList = server.listGames().games();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        StringBuilder sb = new StringBuilder();
        numberToID.clear();

        for (int i = 0; i < gameList.size(); i++) {
            String number = String.valueOf(i+1);
            String game = "(" + number + ") " + gameList.get(i).toString() + "\n";
            sb.append(game);
            numberToID.put(i+1, gameList.get(i).gameID());
        }
        return sb.toString();
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
            return "Error: missing game number or Team Color Selection";
        }

        try {
            var game = new JoinGameRequest(params[1].toUpperCase(), numberToID.get(Integer.parseInt(params[0])));
            server.joinGame(game);
            return "Success: Joined game" + String.format("%d", numberToID.get(Integer.parseInt(params[0])))+ " as " +String.format("%s", params[1]);
        }
        catch (Exception e) {
            if (!e.getMessage().contains("Error")){
                return "Error: Invalid input, list games to get valid game numbers";
            }
            return e.getMessage();
        }
    }

    public String observeGame(String ... params) {
        if (params.length < 1) {
            return "Error: must input game number";
        }
        try {
            if (gameExists(numberToID.get(Integer.parseInt(params[0])))) {
                return "Success: Joined game " + String.format("%s", params[0]) + " as observer";}
        }
        catch (Exception e) {
            return "Error: invalid game number";
        }
        return "Error: invalid game number";
    }

    private boolean gameExists(int iD) {
        try {
            for (GameData game : gameList) {
                if (game.gameID() == iD) {
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