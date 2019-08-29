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

    public BlockPlacer getBlockPlacer() {
        return blockPlacer;
    }

    public Block getPlaced() {
        return placed;
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
