package me.xginko.serverrestart.event;

import me.xginko.serverrestart.enums.RestartMethod;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerRestartEvent extends Event implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean isCancelled = false;

    private RestartMethod restartMethod;

    /**
     * Called right before the restart/shutdown methods are executed.
     */
    public ServerRestartEvent(RestartMethod restartMethod) {
        this.restartMethod = restartMethod;
    }

    /**
     * Called right before the restart/shutdown methods are executed.
     */
    public ServerRestartEvent(RestartMethod restartMethod, boolean isAsync) {
        super(isAsync);
        this.restartMethod = restartMethod;
    }

    public RestartMethod getRestartMethod() {
        return restartMethod;
    }

    public void setRestartMethod(RestartMethod restartMethod) {
        this.restartMethod = restartMethod;
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
