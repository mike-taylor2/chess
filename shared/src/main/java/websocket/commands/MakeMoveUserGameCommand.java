package websocket.commands;

import chess.ChessMove;

public class MakeMoveUserGameCommand extends UserGameCommand {

    public MakeMoveUserGameCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
    }
}
