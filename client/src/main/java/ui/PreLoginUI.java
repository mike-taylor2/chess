package ui;

import exception.ResponseException;
import model.LoginRequest;
import ServerFacade.ServerFacade;
import model.RegisterRequest;
import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade server;


    public PreLoginUI(ServerFacade server) {
        this.server = server;
    }


    public void run() {
        System.out.print(help());
        String result = "";
        Scanner scanner = new Scanner(System.in);

        while (!result.contains("Terminating") && !result.contains("Success")) {
            printPrompt();
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
        if (result.contains("Success")) {
            PostLoginUI postUI = new PostLoginUI(server);
            postUI.run();
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                - help
                """;
    }

    public String quit() {
        return "Terminating program";
    }

    public String register(String ... params) throws ResponseException {
        if (params.length < 3) {
            return "Error: missing username or password or email";
        }
        var user = new RegisterRequest(params[0], params[1], params[2]);
        try {
            server.register(user);
            server.login(new LoginRequest(params[0], params[1]));
        }
        catch (Exception e){
            return e.getMessage();
        }
        return "Success: Registered and Logged in";
    }

    public String login(String ... params) throws ResponseException {
        if (params.length < 2) {
            return "Error: missing username or password";
        }
        var user = new LoginRequest(params[0], params[1]);
        try {
            server.login(user);
            return "Success: Logged in";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
