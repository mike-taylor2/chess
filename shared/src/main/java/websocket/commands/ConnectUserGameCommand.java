package websocket.commands;

public class ConnectUserGameCommand extends UserGameCommand {

    private final Role role;


    public Role getRole() {
        return role;
    }

    public ConnectUserGameCommand(String authToken, Integer gameID, Role role) {
        super(CommandType.CONNECT, authToken, gameID);
        this.role = role;
    }

    public enum Role {
        WHITE,
        BLACK,
        OBSERVER
    }
}
