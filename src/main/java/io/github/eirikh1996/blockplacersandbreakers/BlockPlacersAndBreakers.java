package io.github.eirikh1996.blockplacersandbreakers;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.github.eirikh1996.blockplacersandbreakers.listener.BlockListener;
import io.github.eirikh1996.blockplacersandbreakers.listener.InteractListener;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import io.github.eirikh1996.blockplacersandbreakers.update.UpdateManager;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static io.github.eirikh1996.blockplacersandbreakers.Messages.TAB;

public class BlockPlacersAndBreakers extends JavaPlugin {
    private static BlockPlacersAndBreakers instance;
    private final Set<BlockPlacer> blockPlacers = new HashSet<>();
    private final Set<BlockBreaker> blockBreakers = new HashSet<>();
    private final File file = new File(getDataFolder(),"placersandbreakers.yml");
    private Economy economy;
    private WorldGuardPlugin worldGuardPlugin;
    private RedProtect redProtectPlugin;
    private GriefPrevention griefPreventionPlugin;

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

        saveResource("placersandbreakers.yml",false);

        Settings.is1_13 = versionNumber > 12;
        if (Settings.is1_13){
            saveDefaultConfig();
        } else {
            saveLegacyConfig();
        }
        UpdateManager.initialize();
        Settings.debug = getConfig().getBoolean("debug", false);
        getLogger().info("Detected server version: " + getServer().getVersion());
        blockBreakers.addAll(getBlockBreakersFromFile());
        blockPlacers.addAll(getBlockPlacersFromFile());
        Settings.tool = Material.getMaterial(getConfig().getString("Tool", Settings.is1_13 ? "WOODEN_HOE" : "WOOD_HOE"));
        Settings.ApplyDamageToBreakerPickaxe = getConfig().getBoolean("ApplyDamageToBreakerPickaxe", true);
        this.getCommand("BlockPlacersAndBreakers").setExecutor(new MainCommand(this));
        //Check for WorldGuard
        Plugin wgPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgPlugin != null){
            if (wgPlugin instanceof WorldGuardPlugin){
                worldGuardPlugin = (WorldGuardPlugin) wgPlugin;
                getLogger().info("Found a compatible version of WorldGuard. Enabling WorldGuard integration");
            }
        }
        //Check for RedProtect
        Plugin rp = getServer().getPluginManager().getPlugin("RedProtect");
        if (rp instanceof RedProtect){
            getLogger().info("Found a compatible version of RedProtect. Enabling RedProtect integration");
            redProtectPlugin = (RedProtect) rp;
        }
        //Check for GriefPrevention
        Plugin gp = getServer().getPluginManager().getPlugin("GriefPrevention");
        if (gp instanceof GriefPrevention){
            getLogger().info("Found a compatible version of GriefPrevention. Enabling RedProtect integration");
            griefPreventionPlugin = (GriefPrevention) gp;
        }
        //Check for Vault
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
        UpdateManager.getInstance().start();
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

    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuardPlugin;
    }

    public RedProtect getRedProtectPlugin() {
        return redProtectPlugin;
    }

    public GriefPrevention getGriefPreventionPlugin() {
        return griefPreventionPlugin;
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
                int entry = 0;
                for (BlockPlacer bp : getBlockPlacers()) {
                    Location l = bp.getLocation();
                    writer.println(TAB + l.getWorld().getUID().toString() + "_" + entry + ":");
                    writer.println(TAB + TAB + "location: [" + l.getBlockX() + ", " + l.getBlockY()  + ", " + l.getBlockZ() + "]");
                    writer.println(TAB + TAB + "owner: " + bp.getOwner().toString());
                    entry++;
                }
            }
            writer.println("breakers:");
            if (!getBlockBreakers().isEmpty()) {
                int entry = 0;
                for (BlockBreaker bb : getBlockBreakers()) {
                    Location l = bb.getLocation();
                    writer.println(TAB + l.getWorld().getUID().toString() + "_" + entry +  ":");
                    writer.println(TAB + TAB + "location: [" + l.getBlockX() + ", " + l.getBlockY()  + ", " + l.getBlockZ() + "]");
                    writer.println(TAB + TAB + "owner: " + bb.getOwner().toString());
                    entry++;
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
            return Collections.emptySet();
        final HashMap<String,Object> placerData = (HashMap<String, Object>) data.get("placers");
        if (placerData == null)
            return Collections.emptySet();
        if (Settings.debug)
            getLogger().info(String.format("Loading %d stored block placer entries", placerData.entrySet().size()));
        for (Map.Entry<String, Object> entry : placerData.entrySet()){
            String[] parts = entry.getKey().split("_");
            UUID wID = UUID.fromString(parts[0]);
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
        if (Settings.debug)
            getLogger().info(String.format("Loading %d stored block breaker entries", breakerData.entrySet().size()));
        for (Map.Entry<String, Object> entry : breakerData.entrySet()){
            String[] parts = entry.getKey().split("_");
            UUID wID = UUID.fromString(parts[0]);
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

    private void saveLegacyConfig(){
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists())
            return;
        InputStream resource = getResource("config_legacy.yml");
        Reader reader = new InputStreamReader(resource);
        FileConfiguration config = YamlConfiguration.loadConfiguration(reader);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class PlacersAndBreakersNotFoundException extends RuntimeException{
        public PlacersAndBreakersNotFoundException(String s, Throwable cause){
            super(s,cause);
        }
    }


}
