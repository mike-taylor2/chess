package websocket.commands;

public class LeaveUserGameCommand extends UserGameCommand {
    public LeaveUserGameCommand(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}
