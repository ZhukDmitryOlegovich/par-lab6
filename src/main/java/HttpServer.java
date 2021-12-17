import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.*;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

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

    HttpServer(Http http, ActorRef actorConfig, ZooKeeper zooKeeper, String port)
            throws InterruptedException, KeeperException {
        this.http = http;
        this.actorConfig = actorConfig;
        this.zooKeeper = zooKeeper;
        this.path = PATH_SERVERS + port;
        zooKeeper.create(
                "/servers/" + path,
                path.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    public Route createRoute() {
        return route(path(PATH, () -> route(get(() -> parameter(URL_QUERY_PARAM, (url) ->
                parameter(COUNT_QUERY_PARAM, (count) -> {
                    System.out.printf("count=%s on %s", count, path);
                    if (count.equals(ZERO_COUNT_STRING)) {
                        return completeWithFuture(
                                http.singleRequest(HttpRequest.create(url))
                        );
                    }
                    return completeWithFuture(Patterns.ask(
                                    actorConfig,
                                    new MessageGetRandomServerUrl(),
                                    TIMEOUT
                            )
                            .thenCompose(resPort -> http.singleRequest(HttpRequest.create(
                                    String.format(
                                            URL_PATTERN,
                                            resPort,
                                            url,
                                            Integer.parseInt(count) - 1
                                    )
                            ))));
                })
        )))));
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getData(path, this, null);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
