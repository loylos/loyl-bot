package hu.loylos.loylbot.eventlisteners.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.object.entity.Message;
import discord4j.voice.AudioProvider;
import hu.loylos.loylbot.config.TrackScheduler;
import hu.loylos.loylbot.eventlisteners.MessageResponder;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SkipResponder extends MessageResponder<Message> {

    private final TrackScheduler trackScheduler;
    private final AudioProvider audioProvider;
    private final AudioPlayerManager audioPlayerManager;

    public SkipResponder(Gateway gateway, TrackScheduler trackScheduler, AudioProvider audioProvider, AudioPlayerManager audioPlayerManager) {
        super(gateway);
        this.trackScheduler = trackScheduler;
        this.audioProvider = audioProvider;
        this.audioPlayerManager = audioPlayerManager;
    }

    @Override
    public Mono<Message> messageHandler(Message message) {
        return message.getChannel()
                .flatMap(messageChannel -> messageChannel.createMessage("Skippelem"))
                .doOnNext(msg -> trackScheduler.nextTrack());


    }

    @Override
    public boolean messageFilter(Message message) {
        return message.getContent().startsWith("$skip");
    }
}
