package hu.loylos.loylbot.eventlisteners;

import discord4j.core.event.domain.message.ReactionAddEvent;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public abstract class ReactionResponder<T> {

    private final Gateway gateway;

    @EventListener(ApplicationReadyEvent.class)
    public void add() {
        gateway.getGateway()
                .map(
                        gatewayDiscordClient -> gatewayDiscordClient.on(ReactionAddEvent.class)
                                .filter(this::messageFilter)
                                .flatMap(this::messageHandler)
                                .subscribe()
                )
                .subscribe();
    }

    public abstract Mono<T> messageHandler(ReactionAddEvent event);

    public abstract boolean messageFilter(ReactionAddEvent event);
}
