package hu.loylos.loylbot.eventlisteners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public abstract class MessageResponder<T> {

    private final Gateway gateway;

    @EventListener(ApplicationReadyEvent.class)
    public void add() {
        gateway.getGateway()
                .map(
                        gatewayDiscordClient -> gatewayDiscordClient.on(MessageCreateEvent.class)
                                .map(MessageCreateEvent::getMessage)
                                .filter(this::messageFilter)
                                .flatMap(this::messageHandler)
                                .onErrorContinue((ex, err) -> log.error(ex.getMessage() + ": " + err.toString()))
                                .subscribe()
                )
                .subscribe();
    }

    public abstract Mono<T> messageHandler(Message message);

    public abstract boolean messageFilter(Message message);
}
