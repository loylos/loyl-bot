package hu.loylos.loylbot.api;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import hu.loylos.loylbot.config.LoylbotConfig;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class LoylbotApi {

    private final LoylbotConfig loylbotConfig;
    //private final WordCloudGenerator wordCloudGenerator;
    private final Gateway gateway;


//    @GetMapping("/wordCount")
//    public Map<Long, Map<String, Integer>> findAll() {
//        return wordCounter.getWordCount();
//    }
//
//    @GetMapping("/wordCount/{id}")
//    public Map<String, Integer> findAllForChannel(@PathVariable("id") Long id) {
//        return wordCounter.getWordCount().get(id);
//    }
//
//    @GetMapping("/wordCount/{id}/{word}")
//    public Integer findWordForChannel(@PathVariable("id") Long id, @PathVariable("word") String word) {
//        return Optional.ofNullable(wordCounter.getWordCount().get(id)).orElse(new HashMap<>()).get(word);
//    }

    @PostMapping("/{id}")
    public void writeMessage(@PathVariable("id") Long channelId, @RequestBody String message) {
        gateway.getGateway()
                .flatMap(gatewayDiscordClient -> gatewayDiscordClient.getChannelById(Snowflake.of(channelId)))
                .flatMap(channel -> ((MessageChannel) channel).createMessage(message))
                .subscribe();
    }

    @DeleteMapping("/deleteMessage/{channelId}/{messageId}")
    public void deleteMessage(@PathVariable("channelId") Long channelId, @PathVariable("messageId") Long messageId) {
        gateway.getGateway()
                .flatMap(gatewayDiscordClient -> gatewayDiscordClient.getMessageById(Snowflake.of(channelId),Snowflake.of(messageId)))
                .flatMap(Message::delete)
                .subscribe();
    }

    @GetMapping("/{id}/{from}/{until}")
    public Mono<List<String>> getDayShit(@PathVariable("id") Long userId, @PathVariable("from") Long from, @PathVariable("until") Long until) {
        return gateway.getGateway()
                .flatMap(gatewayDiscordClient -> gatewayDiscordClient.getChannelById(Snowflake.of(341945834512056321L)))
                .doOnNext(channel -> log.info(channel.toString()))
                .map(channel -> (TextChannel)channel)
                .flatMapMany(channel -> channel.getMessagesAfter(Snowflake.of(from)))
                .takeWhile(message -> message.getId().asLong() < until)
                .doOnNext(message -> log.info(message.getAuthor().get().getUsername()))
               // .filter(message -> message.getAuthor().get().getId().asLong() == userId)
                .map(Message::getEmbeds)
                .filter(embeds -> embeds.size()>0)
                .map(embeds -> embeds.get(0))
                .map(embed -> embed.getUrl().orElse(""))
                .collectList();

    }

//    @PostMapping("/cloud/{id}")
//    public void makeCloud(@PathVariable("id") Long channelId) {
//        wordCloudGenerator.makeCloud(channelId);
//    }
}
