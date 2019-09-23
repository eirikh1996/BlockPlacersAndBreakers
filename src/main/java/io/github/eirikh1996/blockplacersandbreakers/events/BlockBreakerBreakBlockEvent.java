package io.github.eirikh1996.blockplacersandbreakers.events;

import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BlockBreakerBreakBlockEvent extends BPBevent implements Cancellable {
    private static HandlerList HANDLERS = new HandlerList();
    private final Block broken;
    private final BlockBreaker blockBreaker;
    private boolean cancelled = false;
    private String cancellationMessage = "";

    public BlockBreakerBreakBlockEvent(Block broken, BlockBreaker blockBreaker){
        this.broken = broken;
        this.blockBreaker = blockBreaker;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    /**
     * Gets the block breaker involved in this event
     * @return the block breaker involved in this event
     */
    public BlockBreaker getBlockBreaker() {
        return blockBreaker;
    }

    /**
     * Gets the block broken in this event
     * @return the block broken in this event
     */
    public Block getBroken() {
        return broken;
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
