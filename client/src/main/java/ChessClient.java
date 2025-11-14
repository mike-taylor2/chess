import facade.ServerFacade;
import ui.PreLoginUI;

public class ChessClient {
    private final PreLoginUI preUI;

    public ChessClient(String serverUrl){
        ServerFacade server = new ServerFacade(serverUrl);
        preUI = new PreLoginUI(server);
    }

// Create code for application to fail if server isn't running
    public void run(){
        preUI.run();
    }
}
