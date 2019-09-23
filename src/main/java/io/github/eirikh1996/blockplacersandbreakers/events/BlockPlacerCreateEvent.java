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
