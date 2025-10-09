package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("db", ctx -> ctx.result("{}"));
        javalin.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx){
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        req.put("authToken", "cow");
        var res = serializer.toJson(req);
        ctx.result(res);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
