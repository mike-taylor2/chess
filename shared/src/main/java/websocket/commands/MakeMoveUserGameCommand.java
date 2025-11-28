package websocket.commands;

import chess.ChessMove;

public class MakeMoveUserGameCommand extends UserGameCommand {

    private final ChessMove move;

    public MakeMoveUserGameCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
