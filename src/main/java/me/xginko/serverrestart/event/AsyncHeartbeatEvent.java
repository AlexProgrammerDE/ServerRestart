package me.xginko.serverrestart.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHeartbeatEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();

    private final long postLastCall, callTime;

    public AsyncHeartbeatEvent(long postLastCall, long callTime) {
        super(true);
        this.postLastCall = postLastCall;
        this.callTime = callTime;
    }

    public long getPostLastCallTime() {
        return postLastCall;
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
