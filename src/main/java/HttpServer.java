import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.time.Duration;

public class HttpServer implements Watcher {
    private static final String PATH = "";
    private static final String PATH_SERVERS = "localhost:";
    private static final String URL_QUERY_PARAM = "url";
    private static final String COUNT_QUERY_PARAM = "count";
    private static final String ZERO_COUNT_STRING = "0";
    private static final String URL_PATTERN = "http://%s/?url=%s&count=%d";
    private static final Duration TIMEOUT = Duration.ofMillis(5000);

    private final Http http;
    private final ActorRef actorConfig;
    private final ZooKeeper zooKeeper;
    private final String path;

    HttpServer(Http http, ActorRef actorConfig, ZooKeeper zooKeeper, String port) {
        this.http = http;
        this.actorConfig = actorConfig;
        this.zooKeeper = zooKeeper;
        this.path = PATH_SERVERS + port;
        zooKeeper.create(
                "/servers/" + path,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }
}
