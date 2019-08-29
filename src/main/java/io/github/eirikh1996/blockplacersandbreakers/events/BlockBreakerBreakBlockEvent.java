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

    public BlockBreaker getBlockBreaker() {
        return blockBreaker;
    }

    public Block getBroken() {
        return broken;
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
