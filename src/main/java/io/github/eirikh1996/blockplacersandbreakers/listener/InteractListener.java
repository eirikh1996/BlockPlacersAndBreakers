package io.github.eirikh1996.blockplacersandbreakers.listener;

import io.github.eirikh1996.blockplacersandbreakers.BlockPlacersAndBreakers;
import io.github.eirikh1996.blockplacersandbreakers.Settings;
import io.github.eirikh1996.blockplacersandbreakers.events.BlockBreakerCreateEvent;
import io.github.eirikh1996.blockplacersandbreakers.events.BlockPlacerCreateEvent;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static io.github.eirikh1996.blockplacersandbreakers.Messages.BPB_PREFIX;
import static io.github.eirikh1996.blockplacersandbreakers.Messages.ERROR;

public class InteractListener implements Listener {
    private Economy eco = BlockPlacersAndBreakers.getInstance().getEconomy();
    @EventHandler
    public void onDispenserClick(PlayerInteractEvent event){
        if (event.getItem() == null)
            return;
        if (!event.getItem().getType().equals(Settings.tool)){
            return;
        }
        if (!(event.getClickedBlock().getState() instanceof Dispenser)){
            return;
        }
        final Dispenser d = (Dispenser) event.getClickedBlock().getState();
        final Player p = event.getPlayer();
        final BlockPlacersAndBreakers bpb = BlockPlacersAndBreakers.getInstance();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (!event.getPlayer().hasPermission("blockplacersandbreakers.blockbreaker.create") && !event.getPlayer().hasPermission("bpb.blockbreaker.create")){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "You have no permission to create block breakers");
                return;
            }
            if (bpb.getBlockBreakers().contains(BlockBreaker.at(d.getLocation()))){
                p.sendMessage(BPB_PREFIX + ERROR + "This dispenser is already a block breaker");
                event.setCancelled(true);
                return;
            }
            if (bpb.getBlockPlacers().contains(BlockPlacer.at(d.getLocation()))){
                if (!BlockPlacer.at(d.getLocation()).getOwner().equals(p.getUniqueId()) && !p.hasPermission("blockplacersandbreakers.blockplacer.create.others") && !p.hasPermission("bpb.blockplacer.create.others")){
                    p.sendMessage(BPB_PREFIX + ERROR + "You don't own this block placer and have no permission to alter its functionality");
                    return;
                }
                p.sendMessage(BPB_PREFIX + "This dispenser is no longer a block placer");
                bpb.getBlockPlacers().remove(BlockPlacer.at(d.getLocation()));
            } else {
                if (eco != null && !eco.has(p, Settings.PlacerCreateCost)){
                    p.sendMessage(BPB_PREFIX + ERROR + "You cannot afford to create a block placer!");
                    return;
                }
                BlockPlacer bp = new BlockPlacer(d.getLocation(), p.getUniqueId());
                BlockPlacerCreateEvent bpce = new BlockPlacerCreateEvent(bp, p);
                BlockPlacersAndBreakers.getInstance().getServer().getPluginManager().callEvent(bpce);
                if (bpce.isCancelled()){
                    p.sendMessage(bpce.getCancellationMessage());
                    return;
                }
                p.sendMessage(BPB_PREFIX + "This dispenser is now a block placer");
                bpb.getBlockPlacers().add(bp);
            }
        } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            if (!event.getPlayer().hasPermission("blockplacersandbreakers.blockplacer.create") && !event.getPlayer().hasPermission("bpb.blockplacer.create")){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "You have no permission to create block placers");
                d.setCustomName(null);
                return;
            }
            if (bpb.getBlockPlacers().contains(BlockPlacer.at(d.getLocation()))){
                p.sendMessage(BPB_PREFIX + ERROR + "This dispenser is already a block placer");
                d.setCustomName("Block placer");
                event.setCancelled(true);
                return;
            }

            if (bpb.getBlockBreakers().contains(BlockBreaker.at(d.getLocation()))){
                if (!BlockBreaker.at(d.getLocation()).getOwner().equals(p.getUniqueId()) && !p.hasPermission("blockplacersandbreakers.blockbreaker.create.others") && !p.hasPermission("bpb.blockbreaker.create.others")){
                    p.sendMessage(BPB_PREFIX + ERROR + "You don't own this block breaker and have no permission to alter its functionality");
                    return;
                }
                p.sendMessage(BPB_PREFIX + "This dispenser is no longer a block breaker");
                d.setCustomName(null);
                bpb.getBlockBreakers().remove(BlockBreaker.at(d.getLocation()));
            } else {
                if (eco != null && !eco.has(p, Settings.BreakerCreateCost)){
                    p.sendMessage(BPB_PREFIX + ERROR + "You cannot afford to create a block breaker!");
                    return;
                }
                BlockBreaker bb = new BlockBreaker(d.getLocation(), p.getUniqueId());
                BlockBreakerCreateEvent bpce = new BlockBreakerCreateEvent(bb, p);
                BlockPlacersAndBreakers.getInstance().getServer().getPluginManager().callEvent(bpce);
                if (bpce.isCancelled()){
                    p.sendMessage(bpce.getCancellationMessage());
                    return;
                }
                p.sendMessage(BPB_PREFIX + "This dispenser is now a block breaker");
                d.setCustomName("Block breaker");
                bpb.getBlockBreakers().add(bb);
            }
        }
        bpb.updatePBFile();
        event.setCancelled(true);
    }
}
