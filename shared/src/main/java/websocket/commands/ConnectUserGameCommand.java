package websocket.commands;

public class ConnectUserGameCommand extends UserGameCommand {

    public ConnectUserGameCommand(String authToken, Integer gameID, Role role) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    public enum Role {
        WHITE,
        BLACK,
        OBSERVER
    }
}
