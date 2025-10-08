package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("db", ctx -> ctx.result("{}"));
        javalin.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx){
        ctx.result("{\"username\":\"joe\", \"authToken\":\"xyz\"}");
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
