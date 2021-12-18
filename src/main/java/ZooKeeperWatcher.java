import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;

public class ZooKeeperWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";
    private static final String FORMAT_JOIN_PATH = "%s/%s";

    private final ZooKeeper zooKeeper;
    private final ActorRef actorConfig;

    ZooKeeperWatcher(ZooKeeper zooKeeper, ActorRef actorConfig)
            throws InterruptedException, KeeperException {
        this.actorConfig = actorConfig;
        this.zooKeeper = zooKeeper;

        byte[] data = this.zooKeeper.getData(SERVERS_PATH, true, null);
        System.out.printf("servers data=%s", new String(data));
    }

    public static String joinPath(String s) {
        return String.format(FORMAT_JOIN_PATH, SERVERS_PATH, s);
    }

    private void sendServers() throws InterruptedException, KeeperException {
        ArrayList<String> servers = new ArrayList<>();
        for (String s : zooKeeper.getChildren(SERVERS_PATH, this)) {
            servers.add(new String(zooKeeper.getData(
                    joinPath(s), false, null
            )));
        }
        actorConfig.tell(new MessageSendServersList(servers), ActorRef.noSender());
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getChildren(SERVERS_PATH, this);
            sendServers();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
