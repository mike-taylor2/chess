package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.ClearService;
import service.GameService;
import service.ResponseException;
import service.UserService;

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

        javalin.delete("db", this::clear);
        javalin.post("user", this::register);
        javalin.post("session", this::login);
        javalin.delete("session", this::logout);

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
        catch (DataAccessException e){
            ctx.status(403);
            ctx.json(Map.of("error", e.getMessage()));
        }
        catch (ResponseException e){
            ctx.status(400);
            ctx.json(Map.of("error", e.getMessage()));
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
        catch(DataAccessException e){
            ctx.status(401);
            ctx.json(Map.of("error", e.getMessage()));
        }
        catch (ResponseException e){
            ctx.status(400);
            ctx.json(Map.of("error", e.getMessage()));
        }
    }

    private void clear(Context ctx){
        var answer = clearService.clear();
        ctx.status(200);
        ctx.result(answer);
    }

    private void logout(Context ctx){
        LogoutRequest req = new LogoutRequest(ctx.header("authorization"));
        try{
            var answer = userService.logout(req);
            ctx.status(200);
            ctx.result(answer);
        }
        catch (DataAccessException e){
            ctx.status(401);
            ctx.json(Map.of("error", e.getMessage()));
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
