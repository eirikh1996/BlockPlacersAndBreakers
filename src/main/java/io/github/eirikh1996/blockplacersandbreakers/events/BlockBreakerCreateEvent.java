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
     * Constructs a new instance of this event
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

    /**
     * Gets the block breaker involved in this event
     * @return the block breaker involved in this event
     */
    public BlockBreaker getBreaker() {
        return breaker;
    }

    /**
     * Gets the player creating the block breaker involved in this event
     * @return the player creating the block breaker involved in this event
     */
    public Player getCreator() {
        return creator;
    }

    /**
     * Gets the cancellation state of this event
     * @return the cancellation state of this event
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event
     * @param cancelled cancellation state
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets the cancellation message for this event
     * @return the cancellation message for this event
     */
    public String getCancellationMessage() {
        return cancellationMessage;
    }

    /**
     * Sets a cancellation message for this event, which is sent to the block placer's owner if the event is cancelled
     * @param cancellationMessage the cancellation message
     */
    public void setCancellationMessage(String cancellationMessage) {
        this.cancellationMessage = cancellationMessage;
    }
}
