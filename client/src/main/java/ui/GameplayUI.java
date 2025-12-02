package ui;

import facade.ServerFacade;
import model.ClientGameplayData;

public class GameplayUI {
    private final ServerFacade server;
    private final String username;
    private final ClientGameplayData.Role role;

    public GameplayUI(ServerFacade server, ClientGameplayData data){
        this.server = server;
        this.username = data.username();
        this.role = data.role();


    }


    public void run() {

    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                - help
                """;
    }
}
