package me.xginko.serverrestart.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHeartbeatEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();

    private final long lastFinish, callTime;

    public AsyncHeartbeatEvent(long lastFinish, long callTime) {
        super(true);
        this.lastFinish = lastFinish;
        this.callTime = callTime;
    }

    public long getLastCallEndTime() {
        return lastFinish;
    }

    public long getCallTime() {
        return callTime;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
