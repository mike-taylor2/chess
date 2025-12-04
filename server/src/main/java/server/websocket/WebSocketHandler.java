package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


import java.util.List;
import java.util.Map;

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
        if (!goodAuthData(session, command.getAuthToken(), command.getGameID())){
            return;}
        try {
            username = userService.getUsername(command.getAuthToken());
            connections.join(session, game, command, username);
        }
        catch (Exception e) {
            System.out.println("Error in joinGame");
        }
    }

    private void makeMove(MakeMoveUserGameCommand command, Session session) {
        if (!goodAuthData(session, command.getAuthToken(), command.getGameID())){
            return;
        }
        if (!correctRole(command.getAuthToken(), command.getGameID(), command.getMove(), session)){
            return;
        }
        if (checkFinishedGame(command.getGameID())) {
            var errorMessage = new ErrorMessage("Error: Game is finished");
            connections.directedMessage(command.getGameID(), session, errorMessage);
            return;
        }
        var username = userService.getUsername(command.getAuthToken());
        try {
            game = gameService.makeMove(command.getGameID(), command.getMove());
            var loadGameMessage = new LoadGameMessage(game);
            var formattedMove = formatMove(command.getMove());
            var notificationMessage = new NotificationMessage(String.format("%s moved from %s to %s",
                    username, formattedMove.getFirst(), formattedMove.getLast()));
            checkForCheck(command.getGameID(), session);
            connections.broadcastEveryone(command.getGameID(), session, loadGameMessage);
            connections.broadcast(command.getGameID(), session, notificationMessage);
        } catch (Exception e) {
            var errorMessage = new ErrorMessage("Error: Invalid move");
            connections.directedMessage(command.getGameID(), session, errorMessage);
        }

    }

    private boolean goodAuthData(Session session, String authToken, Integer gameID) {
        try {
            userService.verifyAuthData(authToken);
        }catch (Exception e) {
            var errorMessage = new ErrorMessage("Error: not authorized");
            connections.directedMessage(gameID, session, errorMessage);
            return false;
        }
        if (!gameService.verifyGameID(gameID)) {
            var errorMessage = new ErrorMessage("Error: gameID does not exist");
            connections.directedMessage(gameID, session, errorMessage);
            return false;
        }
        return true;
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void checkForCheck(int gameID, Session session) {
        var whiteTurn = game.getWhiteTurn();
        ChessGame.TeamColor color;
        NotificationMessage message = null;
        if (whiteTurn){
            color = ChessGame.TeamColor.WHITE;
        }
        else {
            color = ChessGame.TeamColor.BLACK;
        }
        if (game.isInCheckmate(color)){
            message = new NotificationMessage("CHECKMATE! Game is finished");
            finishGame(gameID);
        }
        else if (game.isInCheck(color)){
            message = new NotificationMessage("CHECK!");
        }
        else if (game.isInStalemate(color)){
            message = new NotificationMessage("STALEMATE! Game is finished");
            finishGame(gameID);
        }
        if (message != null){
            connections.broadcastEveryone(gameID, session, message);
        }
    }

    private List<String> formatMove(ChessMove move) {
        Map<Integer, String> map = Map.of(
                1, "a",
                2, "b",
                3, "c",
                4, "d",
                5, "e",
                6, "f",
                7, "g",
                8, "h"
        );

        var start = move.getStartPosition();
        var sRow = start.getRow();
        var sCol = start.getColumn();
        var end = move.getEndPosition();
        var eRow = end.getRow();
        var eCol = end.getColumn();

        String firstCoordinate = map.get(sCol) + sRow;
        String secondCoordinate = map.get(eCol) + eRow;
        return List.of(firstCoordinate, secondCoordinate);
    }

    private boolean correctRole(String authToken, int gameID, ChessMove chessMove, Session session) {
        String username = userService.getUsername(authToken);
        ChessPosition start = chessMove.getStartPosition();
        GameData gameData = getGame(gameID);
        ChessGame game = gameData.game();
        var color = game.getPiece(start).getTeamColor();
        ChessGame.TeamColor userColor = getUserColor(gameData, username);
        if (userColor == null) {
            var errorMessage = new ErrorMessage("Error: Observers can't make moves");
            connections.directedMessage(gameID, session, errorMessage);
            return false;
        }
        else if (!userColor.equals(color)){
            var errorMessage = new ErrorMessage("Error: Can't make moves for the opposite team");
            connections.directedMessage(gameID, session, errorMessage);
            return false;
        }
        return true;
    }

    private GameData getGame(int gameID) {
        var gameList = gameService.listGames().games();
        for (GameData game : gameList) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    private ChessGame.TeamColor getUserColor (GameData gameData, String username) {
        if (gameData.blackUsername() != null){
            if (gameData.blackUsername().equals(username)){
                return ChessGame.TeamColor.BLACK;
            }
        }
        if (gameData.whiteUsername() != null) {
            if (gameData.whiteUsername().equals(username)){
                return ChessGame.TeamColor.WHITE;
            }
        }
        return null;
    }

    private void finishGame(int gameID) {
        gameService.finishGame(gameID);
    }

    private boolean checkFinishedGame(int gameID) {
        return gameService.checkFinishedGame(gameID);
    }
}
