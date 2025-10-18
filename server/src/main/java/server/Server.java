package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.LoginRequest;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import service.ResponseException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService service;

    public Server() {
        this.service = new UserService();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("db", ctx -> ctx.result("{}"));
        javalin.post("user", this::register);
//        javalin.post("session", this::login);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            RegisterRequest req = serializer.fromJson(ctx.body(), RegisterRequest.class);
            var result = service.register(req);
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



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
