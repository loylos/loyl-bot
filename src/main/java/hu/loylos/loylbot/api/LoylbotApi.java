package hu.loylos.loylbot.api;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.MessageChannel;
import hu.loylos.loylbot.config.LoylbotConfig;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


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

//    @PostMapping("/cloud/{id}")
//    public void makeCloud(@PathVariable("id") Long channelId) {
//        wordCloudGenerator.makeCloud(channelId);
//    }
}
