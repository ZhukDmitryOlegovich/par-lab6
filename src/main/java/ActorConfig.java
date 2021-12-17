import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.Random;

public class ActorConfig extends AbstractActor {
    private ArrayList<String> servers = new ArrayList<>();

    private final Random random = new Random();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }

    public String getRandomServerPort() {
        return servers.get(random.nextInt(servers.size()));
    }
}
