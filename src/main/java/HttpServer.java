import org.apache.zookeeper.Watcher;

public class HttpServer implements Watcher {
    private static final String PATH = "";
    private static final String PATH_SERVERS = "localhost:";
    private static final String URL_QUERY_PARAM = "url";
    private static final String COUNT_QUERY_PARAM = "count";
    private static final String ZERO_COUNT_STRING = "0";
}
