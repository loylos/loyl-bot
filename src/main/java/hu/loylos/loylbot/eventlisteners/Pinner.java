package hu.loylos.loylbot.eventlisteners;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import hu.loylos.loylbot.gateway.Gateway;
import hu.loylos.loylbot.model.Pin;
import hu.loylos.loylbot.repository.PinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class Pinner extends ReactionResponder<Pin> {

    public Pinner(Gateway gateway, PinRepository pinRepository) {

        super(gateway);
        this.pinRepository = pinRepository;

    }

    private static final ReactionEmoji PIN = ReactionEmoji.of(null, "\uD83D\uDCCC", false);
    private static final Integer LIMIT = 5;
    private static final Long PIN_CHANNEL = 632863376426532865L;
    private static final Long ERHU = 341945834512056321L;

    private final PinRepository pinRepository;

    @Override
    public Mono<Pin> messageHandler(ReactionAddEvent event) {
        String link = "http://discordapp.com/channels/" + event.getGuildId().get().asLong() + "/" + event.getChannelId().asLong() + "/" + event.getMessageId().asLong();
        var user = event.getMessage()
                .map(message -> message.getAuthor().get())
                .map(foundUser -> foundUser.getUsername() + "#" + foundUser.getDiscriminator());
        var avatar = event.getMessage()
                .map(Message::getAuthor)
                .filter(Optional::isPresent)
                .map(userOpt -> userOpt.get().getAvatarUrl());
        var imageUrl = event.getMessage()
                .map(Message::getAttachments)
                .flatMapMany(Flux::fromIterable)
                .next()
                .filter(attachment -> attachment.getHeight().isPresent())
                .map(Attachment::getUrl)
                .switchIfEmpty(Mono.just(""));
        var timestamp = event.getMessage().
                map(Message::getTimestamp);
        var message = event.getMessage();
        var content = message.map(Message::getContent);
        var count = message.flatMap(msg -> msg.getReactors(PIN).count());
        var pinChannel = message.flatMap(Message::getGuild)
                .flatMap(guild -> guild.getChannelById(Snowflake.of(PIN_CHANNEL)))
                .map(channel -> (TextChannel) channel);
        var mono = Mono.zip(user, avatar, imageUrl, timestamp, message, content, count, pinChannel);
        var newMono = mono
                .filter(tuple -> tuple.getT7() >= LIMIT)
                .filter(tuple -> pinRepository.findByUrl(link).isEmpty())
                .flatMap(tuple ->
                        tuple.getT8()
                                .createEmbed(
                                        embedCreateSpec -> embedCreateSpec.setColor(Color.of(16753152))
                                                .setTimestamp(tuple.getT4())
                                                .setFooter("ID " + event.getMessageId().asString(), null)
                                                .setImage(tuple.getT3())
                                                .setAuthor(tuple.getT1(), null, tuple.getT2())
                                                .setDescription(tuple.getT6())
                                                .addField(":pushpin:", " " + tuple.getT7() + " <#" + event.getChannelId().asLong() + ">", false)
                                ).flatMap(msg -> msg.edit(
                                messageEditSpec -> messageEditSpec.setContent(link + " (Jump to message)")
                        )))
                .doOnNext(msg -> log.info(msg.getContent()))
                .map(msg -> Pin.builder().id(msg.getId().asLong()).url(link).build())
                .map(pinRepository::save);

        return Mono.fromCallable(() -> pinRepository.findByUrl(link))
                .map(pins -> Optional.ofNullable(pins.isEmpty() ? null : pins.get(0)))
                .flatMap(Mono::justOrEmpty)
                .map(Pin::getId)
                .flatMap(messageId -> getMessageInGuild(messageId, event))
                .zipWith(mono)
                .flatMap(tuple -> tuple.getT1().edit(
                        messageEditSpec -> messageEditSpec.setEmbed(
                                embedCreateSpec -> embedCreateSpec.setColor(Color.of(16753152))
                                        .setTimestamp(tuple.getT2().getT4())
                                        .setFooter("ID " + event.getMessageId().asString(), null)
                                        .setImage(tuple.getT2().getT3())
                                        .setAuthor(tuple.getT2().getT1(), null, tuple.getT2().getT2())
                                        .setDescription(tuple.getT2().getT6())
                                        .addField(":pushpin:", " " + tuple.getT2().getT7() + " <#" + event.getChannelId().asLong() + ">", false)
                        )))
                .doOnNext(msg -> log.info(msg.getContent()))
                .map(msg -> Pin.builder().id(msg.getId().asLong()).url(link).build())
                .map(pinRepository::save)
                .switchIfEmpty(newMono);
    }

    @Override
    public boolean messageFilter(ReactionAddEvent event) {
        log.info(String.valueOf(event.getEmoji().equals(PIN)));

        return event.getEmoji().equals(PIN) &&
                !event.getChannelId().equals(Snowflake.of(PIN_CHANNEL)) &&
                event.getGuildId().orElse(Snowflake.of(0)).equals(Snowflake.of(ERHU));
    }


    private Mono<Message> getMessageInGuild(Long messageId, ReactionAddEvent event) {
        return event.getGuild()
                .flatMap(guild -> guild.getChannelById(Snowflake.of(PIN_CHANNEL)))
                .map(guildChannel -> (TextChannel) guildChannel)
                .flatMap(textChannel -> textChannel.getMessageById(Snowflake.of(messageId)));

    }

}
