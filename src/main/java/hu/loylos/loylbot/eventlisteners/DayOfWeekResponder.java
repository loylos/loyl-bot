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

    private List<String> blackList = List.of("440239361104150538");

    public DayOfWeekResponder(Gateway gateway) {
        super(gateway);
    }

    @Override
    public Mono<Message> messageHandler(Message message) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        var dow = localDateTime.getDayOfWeek().getValue();
        var msg = "";
        switch (dow) {
            case 1:
                msg = "hétfő van";
                break;
            case 2:
                msg = "kedd van";
                break;
            case 3:
                msg = "szerda van";
                break;
            case 4:
                msg = "csütörtök van";
                break;
            case 5:
                msg = "péntek van";
                break;
            case 6:
                msg = "szombat van";
                break;
            case 7:
                msg = "vasárnap van";
                break;
            default:
                msg = "mittomén";
                break;
        }
        String finalMsg = msg;
        return message.getChannel()
                .flatMap(
                        messageChannel -> messageChannel.createMessage(finalMsg)
                );
    }

    @Override
    public boolean messageFilter(Message message) {
        if(blackList.contains(message.getChannelId().asString()))
            return false;
        var msg = message.getContent().toLowerCase();
        var milyen = msg.contains("milyen") || msg.contains("mien") || msg.contains("mijen");
        var nap = msg.contains("nap");
        var van = msg.contains("van");
        return milyen && nap && van;
    }


}
