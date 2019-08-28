package io.github.eirikh1996.blockplacersandbreakers.objects;

import io.github.eirikh1996.blockplacersandbreakers.BlockPlacersAndBreakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public final class BlockBreaker {
    private final Location location;
    private final UUID owner;


    public BlockBreaker(Location location, UUID owner) {
        this.location = location;
        this.owner = owner;
    }

    public static BlockBreaker at(Location location){
        for (BlockBreaker breaker : BlockPlacersAndBreakers.getInstance().getBlockBreakers()){
            if (location.equals(breaker.getLocation())) return breaker;
        }
        return null;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, owner);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockPlacer){
            BlockPlacer other = (BlockPlacer) obj;
            return other.getOwner().equals(getOwner()) && other.getLocation().equals(getLocation());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{Block placer, Location: +" + location.toString() + ", Owner: " + Bukkit.getOfflinePlayer(owner).getName() + "}";
    }
}
