package server.websocket;

import chess.ChessGame;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import websocket.commands.ConnectUserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public ConnectionManager() {
        var gameService = new GameService();
        var games = gameService.listGames().games();

        for (GameData game : games) {
            connections.put(game.gameID(), new HashSet<Session>());
        }
    }

    // Update later to create a message with specified role
    public void join(Session session, ChessGame game, ConnectUserGameCommand command, String username) throws IOException {
        try{
            for (Integer i : connections.keySet()) {
                if (i.equals(command.getGameID())) {
                    connections.get(i).add(session);
                    var loadGameMessage = new LoadGameMessage(game);
                    var notificationMessage = new NotificationMessage(String.format("Update: %s joined the game as %s", username, command.getRole()));
                    directedMessage(session, loadGameMessage);
                    broadcast(session, notificationMessage);
                    return;
                }
            }
        }
        catch (Exception e) {
            var errorMessage = new ErrorMessage("Error: gameID does not exist");
            directedMessage(session, errorMessage);
        }
    }

    public void leave(int GameID, Session session) throws IOException {
        for (Integer i : connections.keySet()) {
            if (i.equals(GameID)) {
                connections.get(i).remove(session);
                return;
            }
        }
        var errorMessage = new ErrorMessage("Error: gameID does not exist");
        directedMessage(session, errorMessage);
    }

    public void broadcast(Session excludeSession, ServerMessage message) throws IOException {

    }

    public void directedMessage(Session session, ServerMessage message) throws IOException {

    }
}
