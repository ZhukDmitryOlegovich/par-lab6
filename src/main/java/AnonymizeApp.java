import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.apache.log4j.BasicConfigurator;

import java.util.Arrays;

public class AnonymizeApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: need ports >2");
            System.exit(-1);
        }
        BasicConfigurator.configure();
        System.out.println("Start! Ports: " + Arrays.toString(args));
        ActorSystem system = ActorSystem.create("lab6");
        ActorRef actorConfig = system.actorOf(Props.create());
    }
}
