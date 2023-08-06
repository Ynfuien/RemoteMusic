package pl.ynfuien.remotemusic;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteManager {
    private static boolean active = true;

    public static void VolumeUp()
    {
        SendRequest("volume-up");
    }
    public static void VolumeDown()
    {
        SendRequest("volume-down");
    }
    public static void VolumeMute()
    {
        SendRequest("volume-mute");
    }
    public static void NextTrack()
    {
        SendRequest("next-track");
    }
    public static void PreviousTrack()
    {
        SendRequest("previous-track");
    }
    public static void PlayPause()
    {
        SendRequest("play-pause");
    }

    private static void SendRequest(String data)
    {
        JSONObject config = MainActivity.getInstance().getConfigManager().getConfig();

        Thread thread = new Thread(() -> {
            try
            {
//                Socket socket = new Socket(config.optString("ip"), config.optInt("port"));
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(config.optString("ip"), config.optInt("port")), 1000);

                socket.getOutputStream().write((data + "<EOF>").getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        thread.start();
    }


    public static void Enable() {
        active = true;
    }
    public static void Disable() {
        active = false;
    }
}
