package hu.loylos.loylbot.eventlisteners.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.voice.AudioProvider;
import hu.loylos.loylbot.config.TrackScheduler;
import hu.loylos.loylbot.eventlisteners.MessageResponder;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class PlayResponder extends MessageResponder<Message> {

    private final TrackScheduler trackScheduler;
    private final AudioProvider audioProvider;
    private final AudioPlayerManager audioPlayerManager;

    public PlayResponder(Gateway gateway, TrackScheduler trackScheduler, AudioProvider audioProvider, AudioPlayerManager audioPlayerManager) {
        super(gateway);
        this.trackScheduler = trackScheduler;
        this.audioProvider = audioProvider;
        this.audioPlayerManager = audioPlayerManager;
    }

    @Override
    @SneakyThrows
    public Mono<Message> messageHandler(Message message) {
        var link = message.getContent().split(" ")[1];
        return message.getAuthor().get().asMember(message.getGuildId().get())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .filter(voiceChannel -> voiceChannel.getGuildId().equals(message.getGuildId().get()))
                .flatMap(voiceChannel -> voiceChannel.join(voiceChannelJoinSpec -> voiceChannelJoinSpec.setProvider(audioProvider)))
                .doOnNext(trackScheduler::setVoiceConnection)
                .doOnNext(yo -> {
                    try {
                        audioPlayerManager.loadItem(link, trackScheduler).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                })
                .flatMap(yo -> message.getChannel())
                .flatMap(messageChannel -> messageChannel.createMessage(getLast(trackScheduler.getPlaylist(), trackScheduler.getPlaying()) + " a listához adva"));

    }

    @Override
    public boolean messageFilter(Message message) {
        return message.getContent().startsWith("$play ") && message.getContent().length() > 6;
    }

    private String getLast(List<String> list, String def) {
        if(list.size() == 0)
            return def;
        return list.get(list.size()-1);
    }
}
