package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import me.xginko.serverrestart.event.ServerRestartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class RestartTimer implements ServerRestartModule, Listener {

    private final boolean safelyRestart;

    public RestartTimer() {
        shouldEnable();
        Config config = ServerRestart.getConfiguration();
        this.safelyRestart = config.getBoolean("general.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will just immediately shutdown/restart (not recommended).""");

    }

    @Override
    public boolean shouldEnable() {
        return !ServerRestart.getConfiguration().RESTART_TIMES.isEmpty();
    }

    @Override
    public void enable() {
        ServerRestart plugin = ServerRestart.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBeat(AsyncHeartbeatEvent event) {
        if (ServerRestart.isRestarting) return;

        // Check if close to one of the configured restart times

        // If close to restart, call RestartCountDownEvent

        // Get if RestartCountDown is cancelled / delayed, handle delay

        // If delayed, update restart time, note for RestartType -> DELAYED

        ServerRestartEvent restartEvent = new ServerRestartEvent(
                true,
                ServerRestartEvent.RestartType.SCHEDULED,
                ServerRestart.getConfiguration().RESTART_METHOD,
                safelyRestart,
                safelyRestart,
                safelyRestart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        // Log to console

        ServerRestart.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
