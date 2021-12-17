import java.util.ArrayList;

public class MessageSendServersList {
    private final ArrayList<String> servers;

    MessageSendServersList(ArrayList<String> servers) {
        this.servers = servers;
    }

    public ArrayList<String> getServers() {
        return servers;
    }
}
