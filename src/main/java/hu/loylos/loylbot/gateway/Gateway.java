package hu.loylos.loylbot.gateway;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import hu.loylos.loylbot.config.LoylbotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Gateway {

    @Autowired
    private final LoylbotConfig loylbotConfig;

    private Mono<GatewayDiscordClient> gateway;

    public Mono<GatewayDiscordClient> getGateway() {
        if (gateway == null) {

            gateway = DiscordClient.create(loylbotConfig.getClientSecret())
                    .login();

        }
        return gateway;
    }

}
