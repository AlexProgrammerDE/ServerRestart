package me.xginko.serverrestart.event;

import me.xginko.serverrestart.enums.RestartMethod;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerRestartEvent extends Event implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull RestartType restartType;
    private @NotNull RestartMethod restartMethod;
    private boolean disableJoining, kickAll, saveAll, isCancelled;

    /**
     * Called right before the restart/shutdown methods are executed.
     */
    public ServerRestartEvent(
            boolean isAsync, @NotNull RestartType restartType, @NotNull RestartMethod restartMethod,
            boolean disableJoining, boolean kickAll, boolean saveAll
    ) {
        super(isAsync);
        this.isCancelled = false;
        this.restartType = restartType;
        this.restartMethod = restartMethod;
        this.kickAll = kickAll;
        this.saveAll = saveAll;
        this.disableJoining = disableJoining;
    }

    public @NotNull RestartType getType() {
        return restartType;
    }

    public @NotNull RestartMethod getMethod() {
        return restartMethod;
    }

    public void setMethod(@NotNull RestartMethod restartType) {
        this.restartMethod = restartType;
    }

    public boolean getKickAll() {
        return kickAll;
    }

    public void setKickAll(boolean kickAll) {
        this.kickAll = kickAll;
    }

    public boolean getSaveAll() {
        return saveAll;
    }

    public void setSaveAll(boolean saveAll) {
        this.saveAll = saveAll;
    }

    public boolean getDisableJoin() {
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

    public enum RestartType {
        /**
         * Restart is happening exactly as it should according to configured time
         */
        SCHEDULED,
        /**
         * Restart is happening after it was previously delayed due to high player count
         */
        DELAYED,
        /**
         * Restart because of low TPS
         */
        ON_FIRE,
        /**
         * Restart because of other reasons
         */
        OTHER;
    }
}
