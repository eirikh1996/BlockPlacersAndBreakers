package io.github.eirikh1996.blockplacersandbreakers.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.eirikh1996.blockplacersandbreakers.BlockPlacersAndBreakers;
import io.github.eirikh1996.blockplacersandbreakers.Settings;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WorldGuardUtils {
    private static Method CAN_BUILD;
    static {
        try {
            CAN_BUILD = WorldGuardPlugin.class.getMethod("canBuild", Player.class, Location.class);
        } catch (NoSuchMethodException e) {
            CAN_BUILD = null;
        }
    }
    public static boolean allowedToBuild(Player player, Location location){
        if (Settings.is1_13){
            ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).getApplicableRegions(locToBlockVector3(location));
            LocalPlayer lp = BlockPlacersAndBreakers.getInstance().getWorldGuardPlugin().wrapPlayer(player);
            return regions.isOwnerOfAll(lp) || regions.isMemberOfAll(lp);
        } else {
            return canBuild(player, location);
        }
    }

    private static BlockVector3 locToBlockVector3(Location loc){
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private static boolean canBuild(Player player, Location location){
        try {
            return CAN_BUILD != null ? (boolean) CAN_BUILD.invoke(BlockPlacersAndBreakers.getInstance().getWorldGuardPlugin(), player, location) : true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return true;
        }
    }
}
