package hu.loylos.loylbot.config;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.voice.VoiceConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Component
@Slf4j
public final class TrackScheduler extends AudioEventAdapter implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private VoiceConnection voiceConnection;

    public TrackScheduler(final AudioPlayer player) {
        this.player = player;
        player.addListener(this);
        queue = new LinkedBlockingQueue<>();
    }

    public void setVoiceConnection(VoiceConnection voiceConnection) {
        this.voiceConnection = voiceConnection;
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        log.info("Track loaded: " + track.getInfo().title);
        // LavaPlayer found an audio source for us to play
        queue(track);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
        log.info("Playlist loaded: " + playlist.getName());
        // LavaPlayer found multiple AudioTracks from some playlist
    }

    @Override
    public void noMatches() {
        log.info("No matches");
        // LavaPlayer did not find any audio to extract
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        log.info("Load failed: " + exception.getMessage());
        // LavaPlayer could not parse an audio source for some reason
        exception.printStackTrace();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        log.info("Queue: " + track.getInfo().title);
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public boolean nextTrack() {
        if(queue.peek() != null) {
            log.info("Next track: " + queue.peek().getInfo().title);
            // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
            // giving null to startTrack, which is a valid argument and will simply stop the player.
            return player.startTrack(queue.poll(), false);
        } else {
            log.info("Next track: nul");
            voiceConnection.disconnect().subscribe();
            return false;
        }
    }

    public void stop() {
        log.info("stop");
        player.stopTrack();
        queue.clear();
        voiceConnection
                .disconnect()
                .subscribe();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        log.info("OnTrackEnd " + endReason.mayStartNext);
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public List<String> getPlaylist() {
        log.info("Get playlist");
        return new ArrayList<>(queue)
                .stream().map(track -> track.getInfo().title)
                .collect(Collectors.toList());
    }

    public String getPlaying() {
        log.info("Get playing");
        return player.getPlayingTrack().getInfo().title;
    }
}
