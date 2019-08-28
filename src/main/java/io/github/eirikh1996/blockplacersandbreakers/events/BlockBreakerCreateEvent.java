package io.github.eirikh1996.blockplacersandbreakers.events;

import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a block breaker is created
 */
public class BlockBreakerCreateEvent extends BPBevent implements Cancellable {
    private static HandlerList HANDLERS = new HandlerList();
    private final Player creator;
    private final BlockBreaker breaker;
    private boolean cancelled = false;
    private String cancellationMessage;

    /**
     *
     * @param breaker the block breaker being created
     * @param creator the player creating the block breaker
     */
    public BlockBreakerCreateEvent(BlockBreaker breaker, Player creator) {
        this.breaker = breaker;
        this.creator = creator;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public BlockBreaker getBreaker() {
        return breaker;
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
