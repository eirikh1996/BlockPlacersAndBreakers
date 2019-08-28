package io.github.eirikh1996.blockplacersandbreakers.events;

import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BlockPlacerCreateEvent extends BPBevent implements Cancellable {
    private static HandlerList HANDLERS = new HandlerList();
    private final Player creator;
    private final BlockPlacer placer;
    private boolean cancelled = false;
    private String cancellationMessage;

    public BlockPlacerCreateEvent(BlockPlacer placer, Player creator){
        this.placer = placer;
        this.creator = creator;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public BlockPlacer getPlacer() {
        return placer;
    }

    public Player getCreator() {
        return creator;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancellationMessage() {
        return cancellationMessage;
    }

    public void setCancellationMessage(String cancellationMessage) {
        this.cancellationMessage = cancellationMessage;
    }
}
