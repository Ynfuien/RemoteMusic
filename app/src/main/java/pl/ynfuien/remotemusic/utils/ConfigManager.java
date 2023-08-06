package pl.ynfuien.remotemusic.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManager {
    private final File configFile;
    private JSONObject jsonConfig;

    public ConfigManager(File directory) {
        configFile = new File(directory, "config.json");
    }

    public boolean loadConfig() {
        if (!configFile.exists()) {
            jsonConfig = new JSONObject();
            try {
                jsonConfig.put("ip", "192.168.0.6");
                jsonConfig.put("port", 41502);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        String configValue;
        try {
            configValue = Files.readAllLines(configFile.toPath()).get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            jsonConfig = new JSONObject(configValue);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean saveConfig() {
        try {
            Files.write(configFile.toPath(), jsonConfig.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public JSONObject getConfig() {
        return jsonConfig;
    }
}
