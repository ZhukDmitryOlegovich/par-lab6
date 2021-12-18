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
    public static final String HOST_ORIGIN = "localhost";
    private static final String HOST_URL = String.format("http://%s:", HOST_ORIGIN);
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: need port");
            System.exit(-1);
        }
        BasicConfigurator.configure();
        System.out.printf("Start! Ports: %s\n", Arrays.toString(args));
        ActorSystem system = ActorSystem.create("lab6");
        ActorRef actorConfig = system.actorOf(Props.create(ActorConfig.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ZooKeeper zooKeeper = null;

        try {
            zooKeeper = new ZooKeeper(args[0], TIMEOUT, null);
            new ZooKeeperWatcher(zooKeeper, actorConfig);
        } catch (KeeperException | IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        ArrayList<CompletionStage<ServerBinding>> bindings = new ArrayList<>();

        StringBuilder serversInfo = new StringBuilder("Servers online at\n");
        String port = args[1];
        try {
            HttpServer server = new HttpServer(http, actorConfig, zooKeeper, port);
            final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow
                    = server.createRoute().flow(system, materializer);
            bindings.add(http.bindAndHandle(
                    routeFlow,
                    ConnectHttp.toHost(HOST_ORIGIN, Integer.parseInt(port)),
                    materializer
            ));
            serversInfo.append(HOST_URL).append(port).append("/\n");
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.printf("%s\nPress RETURN to stop...\n", serversInfo);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for (CompletionStage<ServerBinding> binding : bindings) {
            binding.thenCompose(ServerBinding::unbind)
                    .thenAccept(unbound -> system.terminate());
        }
    }
}
