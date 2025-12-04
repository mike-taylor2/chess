package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
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

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    private void configure() {
        var gameService = new GameService();
        var games = gameService.listGames().games();

//        for (GameData game : games) {
//            connections.put(game.gameID(), new HashSet<Session>());
//            }
        for (GameData game : games) {
            int gameID = game.gameID();
            boolean out = true;
            for (Integer id : connections.keySet()) {
                if (id.equals(gameID)) {
                    out = false;
                }
            }
            if (out) {
                connections.put(game.gameID(), new HashSet<Session>());
            }
        }
    }


    // Update later to create a message with specified role
    public void join(Session session, ChessGame game, ConnectUserGameCommand command, String username) throws IOException {
        try{
            configure();
            for (Integer i : connections.keySet()) {
                if (i.equals(command.getGameID())) {
                    connections.get(i).add(session);
                    var loadGameMessage = new LoadGameMessage(game);
                    var notificationMessage = new NotificationMessage(String.format("Update: %s joined the game as %s", username, command.getRole()));
                    directedMessage(i, session, loadGameMessage);
                    broadcast(i, session, notificationMessage);
                    return;
                }
            }
        }
        catch (Exception e) {
            var errorMessage = new ErrorMessage("Error: gameID does not exist");
            directedMessage(command.getGameID(), session, errorMessage);
        }
    }

    public void leave(int gameID, Session session) throws IOException {
        for (Integer i : connections.keySet()) {
            if (i.equals(gameID)) {
                connections.get(i).remove(session);
                return;
            }
        }
        var errorMessage = new ErrorMessage("Error: gameID does not exist");
        directedMessage(gameID, session, errorMessage);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) {

        Set<Session> sessions = connections.get(gameID);
        String json = new Gson().toJson(message);
        try{
            for (Session s : sessions) {
                if (s.isOpen()) {
                    if (!s.equals(excludeSession)){
                        s.getRemote().sendString(json);
                    }
                }
            }
        } catch (Exception e){
            System.out.print("Error: broadcast failed");
        }
    }

    public void directedMessage(int gameID, Session session, ServerMessage message) {
        String json = new Gson().toJson(message);
        try{
            session.getRemote().sendString(json);
        } catch (Exception e){
            System.out.print("Error: directed message failed");
        }
    }

    public void broadcastEveryone(int gameID, Session session, ServerMessage message) {
        broadcast(gameID, session, message);
        directedMessage(gameID, session, message);
    }
}
