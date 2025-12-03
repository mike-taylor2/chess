package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;


import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();
    private ChessGame game = new ChessGame();

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
//                case LEAVE -> {
//                    LeaveUserGameCommand leaveCommand = new Gson().fromJson(ctx.message(), LeaveUserGameCommand.class);
//                    leave(leaveCommand, ctx.session);}
//                case RESIGN -> {
//                    ResignUserGameCommand resignCommand = new Gson().fromJson(ctx.message(), ResignUserGameCommand.class);
//                    resign(resignCommand, ctx.session);}
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void joinGame(ConnectUserGameCommand command, Session session) {
        String username;
        verifyInputData(session, command.getAuthToken(), command.getGameID();
        try {
            username = userService.getUsername(command.getAuthToken());
            connections.join(session, game, command, username);
        }
        catch (Exception e) {
            System.out.println("Error in joinGame");
        }
    }

    private void makeMove(MakeMoveUserGameCommand command, Session session) {
        verifyInputData(session, command.getAuthToken(), command.getGameID());

    }

    private void verifyInputData(Session session, String authToken, Integer gameID) throws IOException {
        try {
            userService.verifyAuthData(authToken);
        }catch (Exception e) {
            var errorMessage = new ErrorMessage("Error: not authorized");
            connections.directedMessage(session, errorMessage);
            return;
        }
        if (!gameService.verifyGameID(gameID)) {
            var errorMessage = new ErrorMessage("Error: gameID does not exist");
            connections.directedMessage(session, errorMessage);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
