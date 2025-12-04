package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import model.ClientGameplayData;
import websocket.commands.ConnectUserGameCommand;
import websocket.commands.MakeMoveUserGameCommand;
import websocket.commands.ResignUserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;
    String authToken;
    ChessGame currentGame;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler, String authToken) {
        this.authToken = authToken;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        var loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        currentGame = loadGameMessage.getGame();
                        serverMessageHandler.notify(loadGameMessage);
                    }
                    else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                        var notification = new Gson().fromJson(message, NotificationMessage.class);
                        serverMessageHandler.notify(notification);
                    }
                    else {
                        var error = new Gson().fromJson(message, ErrorMessage.class);
                        serverMessageHandler.notify(error);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.print("Error: (give context to error)");
        }
    }

    public void joinGame(ClientGameplayData data) throws ResponseException {
        // For each method, use the information from the parameters to create specific UserGameCommand
        // authToken can be obtained from ServerFacade

        // joinGame specifically will only send the message that the player has joined to all other participants
        ConnectUserGameCommand command = getConnectUserGameCommand(data);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));   
        }
        catch (Exception e) {
            System.out.print("Error: unable to send");
        }
    }

    private ConnectUserGameCommand getConnectUserGameCommand(ClientGameplayData data) {
        ConnectUserGameCommand command;
        if (data.role() == ClientGameplayData.Role.WHITE){
            command = new ConnectUserGameCommand(authToken, data.gameID(), ConnectUserGameCommand.Role.WHITE);
        }
        else if (data.role() == ClientGameplayData.Role.BLACK){
            command = new  ConnectUserGameCommand(authToken, data.gameID(), ConnectUserGameCommand.Role.BLACK);
        }
        else {
            command = new  ConnectUserGameCommand(authToken, data.gameID(), ConnectUserGameCommand.Role.OBSERVER);
        }
        return command;
    }

    public void makeMove(ClientGameplayData data, ChessMove move) {
        var command = new MakeMoveUserGameCommand(authToken, data.gameID(), move);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            System.out.print("Error: Unable to connect to server");
        }
    }

    public void leave(ClientGameplayData data) {

    }

    public void resign(ClientGameplayData data) {
        var command = new ResignUserGameCommand(authToken, data.gameID());
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            System.out.print("Error: failed to connect to server");
        }
    }

    public ChessGame getCurrentGame(){
        return currentGame;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
