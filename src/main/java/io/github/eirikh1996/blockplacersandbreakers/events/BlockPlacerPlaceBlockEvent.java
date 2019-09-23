package io.github.eirikh1996.blockplacersandbreakers.events;

import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BlockPlacerPlaceBlockEvent extends BPBevent implements Cancellable {
    private static HandlerList HANDLERS = new HandlerList();
    private final Block placed;
    private final BlockPlacer blockPlacer;
    private boolean cancelled = false;
    private String cancellationMessage = "";

    /**
     * Constructs a new instance of this event
     * @param placed The block being placed
     * @param blockPlacer The block placer placing the block
     */
    public BlockPlacerPlaceBlockEvent(Block placed, BlockPlacer blockPlacer){
        this.placed = placed;
        this.blockPlacer = blockPlacer;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    /**
     * Gets the block placer involved in this event
     * @return the block placer involved in this event
     */
    public BlockPlacer getBlockPlacer() {
        return blockPlacer;
    }

    /**
     * Gets the block placed by the block placer involved in this event
     * @return the block placed by the block placer involved in this event
     */
    public Block getPlaced() {
        return placed;
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
