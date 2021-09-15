//package hu.loylos.loylbot.eventlisteners.music;
//
//import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
//import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
//import discord4j.core.object.entity.Message;
//import discord4j.voice.AudioProvider;
//import hu.loylos.loylbot.config.TrackScheduler;
//import hu.loylos.loylbot.eventlisteners.MessageResponder;
//import hu.loylos.loylbot.gateway.Gateway;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//@Slf4j
//public class VolumeResponder extends MessageResponder<Message> {
//
//    private final TrackScheduler trackScheduler;
//    private final AudioProvider audioProvider;
//    private final AudioPlayerManager audioPlayerManager;
//    private final AudioPlayer audioPlayer;
//
//
//    public VolumeResponder(Gateway gateway, TrackScheduler trackScheduler, AudioProvider audioProvider, AudioPlayerManager audioPlayerManager, AudioPlayer audioPlayer) {
//        super(gateway);
//        this.trackScheduler = trackScheduler;
//        this.audioProvider = audioProvider;
//        this.audioPlayerManager = audioPlayerManager;
//        this.audioPlayer = audioPlayer;
//    }
//
//    @Override
//    public Mono<Message> messageHandler(Message message) {
//        var splitMsg = message.getContent().split(" ");
//        if(!audioPlayer.isPaused() && splitMsg.length > 1) {
//            try {
//                var vol = Integer.parseInt(splitMsg[1]);
//                if(vol >= 0 && vol < 1000) {
//                    audioPlayer.setVolume(vol);
//                    return message.getChannel()
//                            .flatMap(messageChannel -> messageChannel.createMessage("Hangerő beállítva: " + splitMsg[1]));
//                } else {
//                    return message.getChannel()
//                            .flatMap(messageChannel -> messageChannel.createMessage("Anyád, ezt be nem állítom"));
//                }
//            } catch (NumberFormatException ex) {
//                return message.getChannel()
//                        .flatMap(messageChannel -> messageChannel.createMessage("Fúj mi ez: " + splitMsg[1]));
//            }
//        } else {
//            return message.getChannel()
//                    .flatMap(messageChannel -> messageChannel.createMessage("Jó, de mennyi?"));
//        }
//    }
//
//    @Override
//    public boolean messageFilter(Message message) {
//        return message.getContent().startsWith("$vol ");
//    }
//
//    private String getLast(List<String> list) {
//        if(list.size() == 0)
//            return null;
//        return list.get(list.size()-1);
//    }
//}
