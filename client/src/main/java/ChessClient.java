import server.ServerFacade;
import ui.PreLoginUI;

public class ChessClient {
    private final PreLoginUI preUI;

    public ChessClient(String serverUrl){
        ServerFacade server = new ServerFacade(serverUrl);
        preUI = new PreLoginUI(server);
    }


    public void run(){
        preUI.run();
    }
}
