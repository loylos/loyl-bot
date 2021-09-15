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

import java.util.List;

@Component
@Slf4j
public class ListResponder extends MessageResponder<Message> {

    private final TrackScheduler trackScheduler;

    public ListResponder(Gateway gateway, TrackScheduler trackScheduler, AudioProvider audioProvider, AudioPlayerManager audioPlayerManager) {
        super(gateway);
        this.trackScheduler = trackScheduler;
    }

    @Override
    public Mono<Message> messageHandler(Message message) {
        return message.getChannel()
                .flatMap(messageChannel -> messageChannel.createMessage(toPlaylist(trackScheduler.getPlaylist())));

    }

    @Override
    public boolean messageFilter(Message message) {
        return message.getContent().startsWith("$list");
    }

    private String toPlaylist(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<list.size();i++) {
            builder.append(i+1).append(". ").append(list.get(i)).append("\n");
        }
        return builder.toString();
    }
}
