import akka.actor.AbstractActor;
import akka.actor.Actor;

import java.util.ArrayList;
import java.util.Random;

public class ActorConfig extends AbstractActor {
    private ArrayList<String> servers = new ArrayList<>();

    private final Random random = new Random();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        MessageGetRandomServerUrl.class,
                        msg -> sender().tell(
                                getRandomServerPort(),
                                Actor.noSender()
                        )
                )
                .match(
                        MessageSendServersList.class,
                        msg -> servers = msg.getServers()
                )
                .build();
    }

    public String getRandomServerPort() {
        return servers.get(random.nextInt(servers.size()));
    }
}
