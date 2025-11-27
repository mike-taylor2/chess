package websocket.commands;

public class ResignUserGameCommand extends UserGameCommand {

    public ResignUserGameCommand(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }
}
