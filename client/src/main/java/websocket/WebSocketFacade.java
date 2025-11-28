package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) {
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
                    serverMessageHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.print("Error: (give context to error)");
        }
    }

    public void connect(String username, int gameID) {
        // For each method, use the information from the parameters to create specific UserGameCommand
        // authToken can be obtained from ServerFacade
    }

    public void makeMove(String username, String gameID, String move) {

    }

    public void leave(String username, int gameID) {

    }

    public void resign(String username, int gameID) {

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
