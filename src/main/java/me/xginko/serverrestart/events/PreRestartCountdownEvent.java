package me.xginko.serverrestart.events;

import me.xginko.serverrestart.enums.RestartMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PreRestartCountdownEvent extends Event implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;



    /**
     * Called right before the countdown timer begins.
     */
    public PreRestartCountdownEvent() {

    }

    /**
     * Called right before the countdown timer begins.
     */
    public PreRestartCountdownEvent(boolean isAsync) {
        super(isAsync);

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
