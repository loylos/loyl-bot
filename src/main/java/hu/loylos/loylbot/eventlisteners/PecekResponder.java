package hu.loylos.loylbot.eventlisteners;

import discord4j.core.object.entity.Message;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.*;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class PecekResponder extends MessageResponder<Message> {

    private String timestamp;

    public PecekResponder(Gateway gateway) {
        super(gateway);
        try (BufferedReader br =
                     new BufferedReader(new FileReader("pecek.txt"))) {
            timestamp = br.readLine();
        } catch (Exception ex) {
            timestamp = "0";
        }

    }

    @Override
    public Mono<Message> messageHandler(Message message) {
            timestamp = String.valueOf(ZonedDateTime.now().toEpochSecond());
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("pecek.txt")))
            {
                bw.write(String.valueOf(timestamp));
            } catch (Exception ex) {
                log.error("Oh no: {}", ex.getMessage());
            }
            return message.getChannel()
                    .flatMap(messageChannel -> messageChannel.createMessage(
                            messageCreateSpec -> messageCreateSpec
                                    .setMessageReference(message.getId())
                                    .setContent("jóreggelt drágám <3")
                    ));
    }

    @Override
    public boolean messageFilter(Message message) {
        var localDate = LocalDate.ofInstant(Instant.ofEpochSecond(Long.parseLong(timestamp)), ZoneId.systemDefault());
        var nextDay4am = localDate.atStartOfDay().plus(28, ChronoUnit.HOURS);
        return LocalDateTime.now().isAfter(nextDay4am) && message.getAuthor().get().getId().asLong() == 227440405967273984L;

    }


}
