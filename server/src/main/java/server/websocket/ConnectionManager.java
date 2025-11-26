package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void join(int GameID, Session session) {
        for (Integer i : connections.keySet()) {
            if (i.equals(GameID)) {
                connections.get(i).add(session);
                return;
            }
        }
        var errorMessage = new ServerMessage(ERROR, "Error: gameID does not exist");
        directedMessage(session, "Error: gameID")
    }

    public void leave(int GameID, Session session) {

    }

    public void broadcast(Session excludeSession, ServerMessage message) throws IOException {

    }

    public void directedMessage(Session session, ServerMessage message) throws IOException {

    }
}
