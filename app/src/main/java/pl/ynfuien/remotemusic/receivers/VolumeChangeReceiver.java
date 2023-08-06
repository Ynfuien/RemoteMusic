package pl.ynfuien.remotemusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import pl.ynfuien.remotemusic.MainActivity;
import pl.ynfuien.remotemusic.RemoteManager;

public class VolumeChangeReceiver extends BroadcastReceiver {
    private boolean cancelled = false;
    AudioManager audioManager;

    public VolumeChangeReceiver() {
        super();

        audioManager = (AudioManager) MainActivity.getInstance().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", 0) != AudioManager.STREAM_MUSIC) return;

        if (cancelled) {
            cancelled = false;
            return;
        }

        int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
        int prevVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);

        if (volume == prevVolume) return;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3, 0);
        cancelled = true;

        // Volume up
        if (volume > prevVolume) {
            RemoteManager.VolumeUp();
            return;
        }

        // Volume down
        RemoteManager.VolumeDown();
    }
}
