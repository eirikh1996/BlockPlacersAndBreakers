package io.github.eirikh1996.blockplacersandbreakers.update;


import io.github.eirikh1996.blockplacersandbreakers.BlockPlacersAndBreakers;
import io.github.eirikh1996.blockplacersandbreakers.Messages;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager extends BukkitRunnable {
    private static UpdateManager instance;
    private boolean running = false;

    private UpdateManager(){

    }
    @Override
    public void run() {
        final double currentVersion = getCurrentVersion();
        final double newVersion = checkUpdate(currentVersion);
        BlockPlacersAndBreakers.getInstance().getLogger().info("Checking for updates");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (newVersion > currentVersion){
                    String[] message = Messages.updateMessage(newVersion);
                    for (int index = 0 ; index < message.length ; index++){
                        BlockPlacersAndBreakers.getInstance().getLogger().warning(ChatColor.stripColor(message[index]));
                    }
                    return;
                }
                BlockPlacersAndBreakers.getInstance().getLogger().info("You are up to date");
            }
        }.runTaskLaterAsynchronously(BlockPlacersAndBreakers.getInstance(), 100);

    }

    public static void initialize(){
        instance = new UpdateManager();

    }

    public static UpdateManager getInstance(){
        return instance;
    }

    public boolean start(){
        if (running){
            return false;
        }
        instance.runTaskTimerAsynchronously(BlockPlacersAndBreakers.getInstance(), 0, 1000000);
        running = true;
        return true;
    }
    public double getCurrentVersion(){
        return Double.parseDouble(BlockPlacersAndBreakers.getInstance().getDescription().getVersion());
    }

    public double checkUpdate(double currentVersion){
        try {
            URL url = new URL("https://servermods.forgesvc.net/servermods/files?projectids=339014");
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "BlockPlacersAndBreakers Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray jsonArray = (JSONArray) JSONValue.parse(response);
            if (jsonArray.size() == 0) {
                BlockPlacersAndBreakers.getInstance().getLogger().warning("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            JSONObject jsonObject = (JSONObject) jsonArray.get(jsonArray.size() - 1);
            String versionName = ((String) jsonObject.get("name"));
            String newVersion = versionName.substring(versionName.lastIndexOf("v") + 1);
            return Double.parseDouble(newVersion);
        } catch (Exception e) {
            e.printStackTrace();
            return currentVersion;
        }
    }
}
