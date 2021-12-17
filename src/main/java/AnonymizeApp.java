import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class AnonymizeApp {
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: need ports >2");
            System.exit(-1);
        }
        BasicConfigurator.configure();
        System.out.println("Start! Ports: " + Arrays.toString(args));
        ActorSystem system = ActorSystem.create("lab6");
        ActorRef actorConfig = system.actorOf(Props.create(ActorConfig.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ZooKeeper zooKeeper = null;

        try {
            zooKeeper = new ZooKeeper(args[0], 3000, null);
            new ZooKeeperWatcher(zooKeeper, actorConfig);
        } catch (KeeperException | IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        ArrayList<CompletionStage<ServerBinding>> bindings = new ArrayList<>();

        StringBuilder serversInfo = new StringBuilder("Servers online at\n");
        for (int i = 1; i < args.length; i++) {
            try {
                HttpServer server = new HttpServer(http, actorConfig, zooKeeper, args[i]);
                final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow
                        = server.createRoute().flow(system, materializer);
                bindings.add(http.bindAndHandle(
                        routeFlow,
                        ConnectHttp.toHost(HOST, Integer.parseInt(args[i])),
                        materializer
                ));
                serversInfo.append("http://localhost:").append(args[i]).append("/\n");
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }

        if (bindings.size() == 0) {
            System.err.println();
        }
    }
}
