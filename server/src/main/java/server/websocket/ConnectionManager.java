package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    // Update later to create a message with specified role
    public void join(int GameID, Session session) throws IOException {
        for (Integer i : connections.keySet()) {
            if (i.equals(GameID)) {
                connections.get(i).add(session);
                return;
            }
        }
        var errorMessage = new ErrorMessage("Error: gameID does not exist");
        directedMessage(session, errorMessage);
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
