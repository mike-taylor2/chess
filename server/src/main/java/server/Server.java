package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService(userService, gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            RegisterRequest req = serializer.fromJson(ctx.body(), RegisterRequest.class);
            var result = userService.register(req);
            var answer = serializer.toJson(result);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (EmptyFieldException | AlreadyTakenException | DataAccessException e){
            exceptionHandler(ctx, e);
        }
    }

    private void login(Context ctx){
        var serializer = new Gson();
        try {
            LoginRequest req = serializer.fromJson(ctx.body(), LoginRequest.class);
            var result = userService.login(req);
            var answer = serializer.toJson(result);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (EmptyFieldException | UnauthorizedException | DataAccessException e){
            exceptionHandler(ctx, e);
        }
    }

    private void clear(Context ctx){
        try{
            var answer = clearService.clear();
            ctx.status(200);
            ctx.result(answer);
        }
        catch (DataAccessException e){
            exceptionHandler(ctx, e);
        }

    }

    private void logout(Context ctx){
        LogoutRequest req = new LogoutRequest(ctx.header("authorization"));
        try {
            var answer = userService.logout(req);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (UnauthorizedException | DataAccessException e){
            exceptionHandler(ctx, e);
        }
    }

    private void listGames(Context ctx){
        var serializer = new Gson();
        var authToken = ctx.header("authorization");
        try {
            userService.verifyAuthData(authToken);
            var result = gameService.listGames();
            var answer = serializer.toJson(result);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (UnauthorizedException | DataAccessException e) {
            exceptionHandler(ctx, e);
        }
    }

    private void createGame(Context ctx){
        var serializer = new Gson();
        try {
            CreateGameRequest req = serializer.fromJson(ctx.body(), CreateGameRequest.class);
            String authToken = ctx.header("authorization");
            userService.verifyAuthData(authToken);
            var result = gameService.createGame(req);
            var answer = serializer.toJson(result);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (UnauthorizedException | EmptyFieldException | DataAccessException e) {
            exceptionHandler(ctx, e);
        }
    }

    private void joinGame(Context ctx){
        var serializer = new Gson();
        try {
            JoinGameRequest req = serializer.fromJson(ctx.body(), JoinGameRequest.class);
            String authToken = ctx.header("authorization");
            userService.verifyAuthData(authToken);
            var username = userService.getUsername(authToken);
            var result = gameService.joinGame(req, username);
            ctx.status(200);
            ctx.result(result);
        }
        catch (UnauthorizedException | EmptyFieldException | AlreadyTakenException | DataAccessException e){
            exceptionHandler(ctx, e);
        }
    }

    private void exceptionHandler(Context ctx, ResponseException e){
        var serializer = new Gson();
        ctx.status(e.getStatusCode());
        var result = Map.of("message", e.getMessage());
        var answer = serializer.toJson(result);
        ctx.json(answer);
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
