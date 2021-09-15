package hu.loylos.loylbot.eventlisteners.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import hu.loylos.loylbot.config.TrackScheduler;
import hu.loylos.loylbot.eventlisteners.MessageResponder;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class StopResponder extends MessageResponder<Message> {

    private final TrackScheduler trackScheduler;
    private final AudioProvider audioProvider;
    private final AudioPlayerManager audioPlayerManager;

    public StopResponder(Gateway gateway, TrackScheduler trackScheduler, AudioProvider audioProvider, AudioPlayerManager audioPlayerManager) {
        super(gateway);
        this.trackScheduler = trackScheduler;
        this.audioProvider = audioProvider;
        this.audioPlayerManager = audioPlayerManager;
    }

    @Override
    public Mono<Message> messageHandler(Message message) {
        return message.getGuild()
                .flatMap(Guild::getSelfMember)
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(VoiceChannel::getVoiceConnection)
                .flatMap(VoiceConnection::disconnect)
                .then(message.getChannel())
                .flatMap(messageChannel -> messageChannel.createMessage("Ok csá"))
                .doOnNext(m -> trackScheduler.stop());


    }

    @Override
    public boolean messageFilter(Message message) {
        return message.getContent().startsWith("$stop");
    }
}
