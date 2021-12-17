import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;

public class ZooKeeperWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";

    private final ZooKeeper zooKeeper;
    private final ActorRef actorConfig;

    ZooKeeperWatcher(ZooKeeper zooKeeper, ActorRef actorConfig)
            throws InterruptedException, KeeperException {
        this.actorConfig = actorConfig;
        this.zooKeeper = zooKeeper;

        byte[] data = this.zooKeeper.getData(SERVERS_PATH, true, null);
        System.out.printf("servers data=%s", new String(data));
    }

    private void sendServers() throws InterruptedException, KeeperException {
        ArrayList<String> servers = new ArrayList<>();
        zooKeeper.getChildren(SERVERS_PATH, this).stream().map(
                
        )
    }
}
