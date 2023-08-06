package pl.ynfuien.remotemusic;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.session.MediaController;

import org.json.JSONException;
import org.json.JSONObject;

import pl.ynfuien.remotemusic.services.PlaybackService;
import pl.ynfuien.remotemusic.utils.ConfigManager;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private ConfigManager configManager;
    private JSONObject config;
    private Intent serviceIntent;
    private PowerManager.WakeLock wakeLock;

    private EditText editTextIP;
    private EditText editTextPort;
    private Button stopButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        serviceIntent = new Intent(this, PlaybackService.class);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "remotemusic:mediawakelock");

        editTextIP = findViewById(R.id.editText_ip);
        editTextPort = findViewById(R.id.editText_port);
        stopButton = findViewById(R.id.button_stop);
        startButton = findViewById(R.id.button_start);


        configManager = new ConfigManager(getFilesDir());
        configManager.loadConfig();
        config = configManager.getConfig();

        editTextIP.setText(config.optString("ip"));
        editTextPort.setText(config.optString("port"));


        if (isPlaybackServiceRunning()) {
            stopButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isPlaybackServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = PlaybackService.class.getName();

        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (name.equals(service.service.getClassName())) return true;
        }

        return false;
    }

    public void button_saveClick(View v) {
        String ip = String.valueOf(editTextIP.getText());
        String port = String.valueOf(editTextPort.getText());

        Toast result = Toast.makeText(this, "Data successfully saved!", Toast.LENGTH_SHORT);
        try {
            config.put("ip", ip);
            config.put("port", Integer.valueOf(port));
        } catch (JSONException e) {
            e.printStackTrace();

            result.setText("Couldn't save data to config object!");
            result.show();
            return;
        }

        if (!configManager.saveConfig()) {
            result.setText("Couldn't save config to file!");
            result.show();
            return;
        }

        result.show();
    }

    public void button_stopClick(View v) {
        MediaController c = PlaybackService.getMediaController();
        if (c != null) c.release();

        stopService(serviceIntent);
        if (wakeLock.isHeld()) wakeLock.release();

        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
    }

    public void button_startClick(View v) {
        wakeLock.acquire();
        startService(serviceIntent);

        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.INVISIBLE);
    }

    public void imageButton_infoClick(View v) {
        Intent activityIntent = new Intent(this, AboutActivity.class);
        startActivity(activityIntent);
    }

    // Media button events
    public void imageButton_previousClick(View v) {
        RemoteManager.PreviousTrack();
    }
    public void imageButton_playPauseClick(View v) {
        RemoteManager.PlayPause();
    }
    public void imageButton_nextClick(View v) {
        RemoteManager.NextTrack();
    }
    public void imageButton_volumeDownClick(View v) {
        RemoteManager.VolumeDown();
    }
    public void imageButton_volumeUpClick(View v) {
        RemoteManager.VolumeUp();
    }
    public void imageButton_volumeMuteClick(View v) {
        RemoteManager.VolumeMute();
    }


    public static MainActivity getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}