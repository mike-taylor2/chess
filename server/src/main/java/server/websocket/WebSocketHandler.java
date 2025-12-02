package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.*;


import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> {
                    ConnectUserGameCommand connectCommand = new Gson().fromJson(ctx.message(), ConnectUserGameCommand.class);
                    joinGame(connectCommand, ctx.session);}
                case MAKE_MOVE -> {
                    MakeMoveUserGameCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveUserGameCommand.class);
                    makeMove(makeMoveCommand, ctx.session);}
                case LEAVE -> {
                    LeaveUserGameCommand leaveCommand = new Gson().fromJson(ctx.message(), LeaveUserGameCommand.class);
                    leave(leaveCommand, ctx.session);}
                case RESIGN -> {
                    ResignUserGameCommand resignCommand = new Gson().fromJson(ctx.message(), ResignUserGameCommand.class);
                    resign(resignCommand, ctx.session);}
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
