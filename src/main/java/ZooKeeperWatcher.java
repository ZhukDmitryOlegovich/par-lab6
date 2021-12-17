import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;

public class ZooKeeperWatcher implements Watcher {
    private final ZooKeeper zooKeeper;
    private final ActorRef actorConfig;

    ZooKeeperWatcher(ZooKeeper zooKeeper, ActorRef actorConfig)
            throws InterruptedException, KeeperException {
        this.actorConfig = actorConfig;
        this.zooKeeper = zooKeeper;

        byte[] data = this.zooKeeper.getData("/servers", true, null);
        System.out.printf("servers data=%s", new String(data));
    }

    private void sendServers() {
        ArrayList<String> servers = new ArrayList<>();
        zooKeeper.getChildren()
    }
}
