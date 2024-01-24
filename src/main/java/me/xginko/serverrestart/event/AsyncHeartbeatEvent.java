package me.xginko.serverrestart.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHeartbeatEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();

    public AsyncHeartbeatEvent() {
        super(true);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
