package me.xginko.serverrestart.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class RestartCountDownEvent extends Event implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean isCancelled = false;
    private long delayMillis = 0L;

    public RestartCountDownEvent(boolean isAsync) {
        super(isAsync);
    }

    public long getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
