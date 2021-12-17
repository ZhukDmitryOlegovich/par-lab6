import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class AnonymizeApp {
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
        for (int i = 0; i < args.length; i++) {
            try {
                
            }
        }
    }
}
