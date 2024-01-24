package me.xginko.serverrestart.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHeartbeatEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();

    private final long lastFinish, start;

    public AsyncHeartbeatEvent(long lastFinish, long start) {
        super(true);
        this.lastFinish = lastFinish;
        this.start = start;
    }

    public long getLastFinishedMillis() {
        return lastFinish;
    }

    public long getStartMillis() {
        return start;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
