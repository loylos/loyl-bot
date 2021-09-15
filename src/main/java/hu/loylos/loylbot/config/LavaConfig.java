package hu.loylos.loylbot.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.voice.AudioProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LavaConfig {

    @Bean AudioPlayerManager getAudioPlayerManager() {
        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        var manager = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize. It is not important to understand
        manager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(manager);

        return manager;
    }

    @Bean
    public AudioPlayer getAudioPlayer(AudioPlayerManager audioPlayerManager) {
        // Create an AudioPlayer so Discord4J can receive audio data
        return audioPlayerManager.createPlayer();
    }

    @Bean
    public AudioProvider getAudioProvider(AudioPlayer audioPlayer) {
        // We will be creating LavaPlayerAudioProvider in the next step
        return new LavaPlayerAudioProvider(audioPlayer);
    }
}
