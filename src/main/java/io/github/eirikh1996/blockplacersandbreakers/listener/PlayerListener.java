package io.github.eirikh1996.blockplacersandbreakers.listener;

import io.github.eirikh1996.blockplacersandbreakers.Messages;
import io.github.eirikh1996.blockplacersandbreakers.update.UpdateManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if (!event.getPlayer().hasPermission("blockplacersandbreakers.update") && !event.getPlayer().hasPermission("bpb.update") ){
            return;
        }
        double currentVersion = UpdateManager.getInstance().getCurrentVersion();
        double newVersion = UpdateManager.getInstance().checkUpdate(currentVersion);
        if (newVersion <= currentVersion){
            return;
        }
        event.getPlayer().sendMessage(Messages.updateMessage(newVersion));
    }
}
