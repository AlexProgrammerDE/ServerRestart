package me.xginko.serverrestart.event;

import me.xginko.serverrestart.enums.RestartMethod;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GracefulServerRestartEvent extends Event implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private RestartMethod restartMethod;
    private boolean kickAll, saveAll, disableJoining;

    /**
     * Called when the server is performing various .
     * Will not be called when pre-shutdown tasks are skipped.
     */
    public GracefulServerRestartEvent(RestartMethod restartMethod, boolean kickAll, boolean saveAll, boolean disableJoining) {
        this.restartMethod = restartMethod;
        this.kickAll = kickAll;
        this.saveAll = saveAll;
        this.disableJoining = disableJoining;
    }

    /**
     * Called when the server is performing various .
     * Will not be called when pre-shutdown tasks are skipped.
     */
    public GracefulServerRestartEvent(RestartMethod restartMethod, boolean kickAll, boolean saveAll, boolean disableJoining, boolean isAsync) {
        super(isAsync);
        this.restartMethod = restartMethod;
        this.kickAll = kickAll;
        this.saveAll = saveAll;
        this.disableJoining = disableJoining;
    }

    public RestartMethod getRestartMethod() {
        return restartMethod;
    }

    public void setRestartMethod(RestartMethod restartMethod) {
        this.restartMethod = restartMethod;
    }

    public boolean shouldKickAll() {
        return kickAll;
    }

    public void setKickAll(boolean kickAll) {
        this.kickAll = kickAll;
    }

    public boolean shouldSaveAll() {
        return saveAll;
    }

    public void setSaveAll(boolean saveAll) {
        this.saveAll = saveAll;
    }

    public boolean shouldDisableJoin() {
        return disableJoining;
    }

    public void setDisableJoining(boolean disableJoining) {
        this.disableJoining = disableJoining;
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