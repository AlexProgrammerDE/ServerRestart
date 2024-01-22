package me.xginko.serverrestart.events;

import me.xginko.serverrestart.enums.RestartMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerRestartEvent extends Event implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private RestartMode restartMode;

    /**
     * Called right before the restart/shutdown methods are executed.
     */
    public ServerRestartEvent(RestartMode restartMode) {
        this.restartMode = restartMode;
    }

    public RestartMode getRestartMode() {
        return restartMode;
    }

    public void setRestartMode(RestartMode restartMode) {
        this.restartMode = restartMode;
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
