package pl.ynfuien.remotemusic.services;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import pl.ynfuien.remotemusic.RemoteManager;
import pl.ynfuien.remotemusic.receivers.VolumeChangeReceiver;

public class PlaybackService extends MediaSessionService {
    private MediaSession mediaSession = null;
    private static MediaController controller = null;
    private VolumeChangeReceiver volumeChangeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

//        Log.d("CREATE", "Created!");
        // Receiver for volume change
        volumeChangeReceiver = new VolumeChangeReceiver();
        registerReceiver(volumeChangeReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));

        // Media player
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();

        startMedia();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        if (mediaSession.getPlayer() != null) mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;

        unregisterReceiver(volumeChangeReceiver);

        super.onDestroy();
    }

    private int prevIndex = 0;
    private Date lastTrackChange = new Date();
    private boolean playPause = false;

    private void startMedia() {
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, PlaybackService.class));

        // Get silence sound uri
        String uriPath = "android.resource://"+getPackageName()+"/raw/silence";
        Uri uri = Uri.parse(uriPath);

        // Create "song" info
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setArtist("Control media of you computer!")
                .setTitle("Remote Music Controller")
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .build();

        // Create song object
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(metadata)
                .build();


        ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                controller = controllerFuture.get();

                int tracksCount = 10;
                for (int i = 0; i < tracksCount; i++) {
                    controller.addMediaItem(mediaItem);
                }
                controller.setRepeatMode(Player.REPEAT_MODE_ALL);

                controller.play();

                final Date startDate = new Date();

                controller.addListener(new Player.Listener() {
                    @Override
                    public void onEvents(Player player, Player.Events events) {
                        Player.Listener.super.onEvents(player, events);

                        if (new Date().getTime() - startDate.getTime() < 1000) return;

//                        Log.d("EVENTS", "VVVVVVVVVV");
//                        for (int i = 0; i < events.size(); i++) {
//                            int event = events.get(i);
//                            Log.d("EVENTS", "" + event);
//                        }
//                        Log.d("EVENTS", "^^^^^^^^^^");


                        // Play/pause
                        if (events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) && events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                            if (playPause) {
                                playPause = false;
                                return;
                            }

                            RemoteManager.PlayPause();

                            playPause = true;
                            controller.play();
                            return;
                        }

                    }

                    // Next/previous track
                    @Override
                    public void onTracksChanged(Tracks tracks) {
                        Player.Listener.super.onTracksChanged(tracks);

                        Date now = new Date();
                        int index = controller.getCurrentMediaItemIndex();

                        if ((now.getTime() - startDate.getTime() < 1000) ||
                            (now.getTime() - lastTrackChange.getTime() < 200)) {
                            prevIndex = index;
                            return;
                        }
                        lastTrackChange = now;

                        boolean nextTrack = false;
//                        Log.d("INDEXES", "Current: " + index + ", Previous: " + prevIndex);

                        if (prevIndex > 0 && prevIndex < tracksCount - 1) {
                            if (index > prevIndex) nextTrack = true;
                        } else if (prevIndex == 0) {
                            if (index == 1) nextTrack = true;
                        } else if (prevIndex == tracksCount - 1) {
                            if (index == 0) nextTrack = true;
                        }

                        prevIndex = index;
                        if (nextTrack) {
                            RemoteManager.NextTrack();
                            return;
                        }

                        RemoteManager.PreviousTrack();
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
//                if (e.getCause() instanceof SecurityException) {
//                    // The session rejected the connection.
//                }
            }
        }, MoreExecutors.directExecutor());
    }

    public static MediaController getMediaController() {
        return controller;
    }
}
