package server;

import com.google.gson.Gson;
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
        catch (EmptyFieldException | AlreadyTakenException e){
            ctx.status(e.getStatusCode());
            var result = Map.of("message", e.getMessage());
            var answer = serializer.toJson(result);
            ctx.json(answer);
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
        catch(EmptyFieldException | UnauthorizedException e){
            ctx.status(e.getStatusCode());
            var result = Map.of("message", e.getMessage());
            var answer = serializer.toJson(result);
            ctx.json(answer);
        }
    }

    private void clear(Context ctx){
        var answer = clearService.clear();
        ctx.status(200);
        ctx.result(answer);
    }

    private void logout(Context ctx){
        LogoutRequest req = new LogoutRequest(ctx.header("authorization"));
        var serializer = new Gson();
        try{
            var answer = userService.logout(req);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (UnauthorizedException e){
            ctx.status(e.getStatusCode());
            var result = Map.of("message", e.getMessage());
            var answer = serializer.toJson(result);
            ctx.json(answer);
        }
    }

    private void listGames(Context ctx){
        var serializer = new Gson();
        ListGamesRequest req = new ListGamesRequest(ctx.header("authorization"));
        if (userService.verifyAuthData(req.authToken())){
            var result = gameService.listGames();
            var answer = serializer.toJson(result);
            ctx.status(200);
            ctx.result(answer);
        }
        else{
            ctx.status(401);
            var result = Map.of("message", "unauthorized");
            var answer = serializer.toJson(result);
            ctx.json(answer);
        }
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
