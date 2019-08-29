package io.github.eirikh1996.blockplacersandbreakers;

import io.github.eirikh1996.blockplacersandbreakers.listener.BlockListener;
import io.github.eirikh1996.blockplacersandbreakers.listener.InteractListener;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

import static io.github.eirikh1996.blockplacersandbreakers.Messages.TAB;

public class BlockPlacersAndBreakers extends JavaPlugin {
    private static BlockPlacersAndBreakers instance;
    private final Set<BlockPlacer> blockPlacers = new HashSet<>();
    private final Set<BlockBreaker> blockBreakers = new HashSet<>();
    private final File file = new File(getDataFolder(),"placersandbreakers.yml");
    private Economy economy;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        String[] parts = version.split("_");
        int versionNumber = Integer.valueOf(parts[1]);
        saveDefaultConfig();
        saveResource("placersandbreakers.yml",false);

        Settings.is1_13 = versionNumber > 12;
        getLogger().info("Detected server version: " + getServer().getVersion());
        blockBreakers.addAll(getBlockBreakersFromFile());
        blockPlacers.addAll(getBlockPlacersFromFile());
        Settings.tool = Material.getMaterial(getConfig().getString("Tool","WOODEN_HOE"));
        Settings.ApplyDamageToBreakerPickaxe = getConfig().getBoolean("ApplyDamageToBreakerPickaxe", true);
        this.getCommand("BlockPlacersAndBreakers").setExecutor(new MainCommand(this));
        if (getServer().getPluginManager().getPlugin("Vault") != null){
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null){
                getLogger().info("Found a compatible version of Vault. Enabling Vault integration");
                Settings.BreakerCreateCost = getConfig().getLong("BreakerCreateCost", 1000);
                Settings.PlacerCreateCost = getConfig().getLong("PlacerCreateCost", 1000);
                economy = rsp.getProvider();
            } else {
                getLogger().info("BlockPlacersAndBreakers did not find a compatible version of Vault. Disabling Vault integration");
                Settings.BreakerCreateCost = 0;
                Settings.PlacerCreateCost = 0;
                economy = null;
            }
        }

        getServer().getPluginManager().registerEvents(new BlockListener(),this);
        getServer().getPluginManager().registerEvents(new InteractListener(),this);
        getLogger().info(String.format("Loaded %d block placers and %d block breakers",blockPlacers.size(),blockBreakers.size()));
    }

    public static BlockPlacersAndBreakers getInstance() {
        return instance;
    }

    public Set<BlockBreaker> getBlockBreakers() {
        return blockBreakers;
    }

    public Set<BlockPlacer> getBlockPlacers() {
        return blockPlacers;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void updatePBFile(){

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(file);
            writer.println("#DO NOT EDIT OR IMPORTANT DATA WILL BE CORRUPTED!");
            writer.println("placers:");
            if (!getBlockPlacers().isEmpty()) {
                for (BlockPlacer bp : getBlockPlacers()) {
                    Location l = bp.getLocation();
                    writer.println(TAB + l.getWorld().getUID().toString() + ":");
                    writer.println(TAB + TAB + "location: [" + l.getBlockX() + ", " + l.getBlockY()  + ", " + l.getBlockZ() + "]");
                    writer.println(TAB + TAB + "owner: " + bp.getOwner().toString());
                }
            }
            writer.println("breakers:");
            if (!getBlockBreakers().isEmpty()) {
                for (BlockBreaker bb : getBlockBreakers()) {
                    Location l = bb.getLocation();
                    writer.println(TAB + l.getWorld().getUID().toString() + ":");
                    writer.println(TAB + TAB + "location: [" + l.getBlockX() + ", " + l.getBlockY()  + ", " + l.getBlockZ() + "]");
                    writer.println(TAB + TAB + "owner: " + bb.getOwner().toString());
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Set<BlockPlacer> getBlockPlacersFromFile(){
        Set<BlockPlacer> ret = new HashSet<>();
        final Map data;
        try {
            InputStream input = new FileInputStream(file);
            Yaml yaml = new Yaml();
            data = yaml.load(input);
        } catch (FileNotFoundException e) {
            throw new PlacersAndBreakersNotFoundException("No placer or breaker data found!",e);
        }
        if (!data.containsKey("placers"))
            return Collections.emptySet();;
        final HashMap<String,Object> placerData = (HashMap<String, Object>) data.get("placers");
        if (placerData == null)
            return Collections.emptySet();
        for (Map.Entry<String, Object> entry : placerData.entrySet()){
            UUID wID = UUID.fromString(entry.getKey());
            Map<String, Object> placerDataMap = (Map<String, Object>) entry.getValue();

            int x = ((ArrayList<Integer>)placerDataMap.get("location")).get(0);
            int y = ((ArrayList<Integer>)placerDataMap.get("location")).get(1);
            int z = ((ArrayList<Integer>)placerDataMap.get("location")).get(2);
            UUID owner = UUID.fromString((String) placerDataMap.get("owner"));
            Block block = getServer().getWorld(wID).getBlockAt(x,y,z);
            if (block.getState() instanceof Dispenser){
                ret.add(new BlockPlacer(block.getLocation(), owner));
            }
        }
        return ret;
    }
    private Set<BlockBreaker> getBlockBreakersFromFile(){
        Set<BlockBreaker> ret = new HashSet<>();
        final Map data;
        try {
            InputStream input = new FileInputStream(file);
            Yaml yaml = new Yaml();
            data = yaml.load(input);
        } catch (FileNotFoundException e) {
            throw new PlacersAndBreakersNotFoundException("No placer or breaker data found!",e);
        }
        if (!data.containsKey("breakers"))
            return Collections.emptySet();;
        final HashMap<String,Object> breakerData = (HashMap<String, Object>) data.get("breakers");
        if (breakerData == null)
            return Collections.emptySet();
        for (Map.Entry<String, Object> entry : breakerData.entrySet()){
            UUID wID = UUID.fromString(entry.getKey());
            Map<String, Object> placerDataMap = (Map<String, Object>) entry.getValue();

            int x = ((ArrayList<Integer>)placerDataMap.get("location")).get(0);
            int y = ((ArrayList<Integer>)placerDataMap.get("location")).get(1);
            int z = ((ArrayList<Integer>)placerDataMap.get("location")).get(2);
            UUID owner = UUID.fromString((String) placerDataMap.get("owner"));
            Block block = getServer().getWorld(wID).getBlockAt(x,y,z);
            if (block.getState() instanceof Dispenser){
                ret.add(new BlockBreaker(block.getLocation(), owner));
            }
        }
        return ret;
    }
    private class PlacersAndBreakersNotFoundException extends RuntimeException{
        public PlacersAndBreakersNotFoundException(String s, Throwable cause){
            super(s,cause);
        }
    }


}
