package hu.loylos.loylbot.eventlisteners;

import discord4j.core.object.entity.Message;
import hu.loylos.loylbot.gateway.Gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@Slf4j
public class DayOfWeekResponder extends MessageResponder<Message> {

    private final List<String> blackList = List.of("440239361104150538");

    private final String[] days = new String[] {"hétfő", "kedd", "szerda", "csütörtök", "péntek", "szombat", "vasárnap"};

    public DayOfWeekResponder(Gateway gateway) {
        super(gateway);
    }

    @Override
    public Mono<Message> messageHandler(Message message) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        var dow = localDateTime.getDayOfWeek().getValue();
        var post = " van";
        if(message.getContent().contains("lesz") || message.getContent().contains("holnap")) {
            if (message.getContent().contains("holnapután")) {
                dow = (dow+2)%7;
                post = " lesz holnapután";
            } else {
                dow = (dow + 1) % 7;
                post = " lesz holnap";
            }
        } else if(message.getContent().contains("volt") || message.getContent().contains("tegnap")) {
            if (message.getContent().contains("tegnapelőtt")) {
                dow = (dow-2)%7;
                post = " volt tegnapelőtt";
            } else {
                dow = (dow-1)%7;
                post = " volt tegnap";
            }
        }
        var day = days[dow - 1];

        String finalMsg = day + post;
        return message.getChannel()
                .flatMap(
                        messageChannel -> messageChannel.createMessage(finalMsg)
                );
    }

    @Override
    public boolean messageFilter(Message message) {
        if (blackList.contains(message.getChannelId().asString()))
            return false;
        var msg = message.getContent().toLowerCase();
        var milyen = msg.contains("milyen") || msg.contains("mien") || msg.contains("mijen");
        var nap = msg.contains("nap");
        var van = msg.contains("van");
        var lesz = msg.contains("lesz");
        var volt = msg.contains("volt");
        return milyen && nap && (van || lesz || volt);
    }


}
