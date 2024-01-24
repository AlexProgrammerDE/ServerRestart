package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import me.xginko.serverrestart.event.ServerRestartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicLong;

public class LowTPSRestart implements ServerRestartModule, Listener {

    private final AtomicLong consec_millis_spent_lagging;
    private final boolean safelyRestart;

    public LowTPSRestart() {
        shouldEnable();
        this.consec_millis_spent_lagging = new AtomicLong();
        Config config = ServerRestart.getConfiguration();
        config.createTitledSection("Fire Extinguisher", "restart-on-low-TPS");
        config.master().addComment("restart-on-low-TPS.enable",
                "Reboot the server when below the configured tps value for the configurable amount of time.");
        this.safelyRestart = config.getBoolean("restart-on-low-TPS.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will just immediately shutdown/restart.""");
    }

    @Override
    public boolean shouldEnable() {
        return ServerRestart.getConfiguration().getBoolean("restart-on-low-TPS.enable", true);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBeat(AsyncHeartbeatEvent event) {
        if (ServerRestart.isRestarting) return;

        // Check tps

        // Check consecutive millis lagging

        ServerRestartEvent restartEvent = new ServerRestartEvent(
                true,
                ServerRestartEvent.RestartType.ON_FIRE,
                ServerRestart.getConfiguration().METHOD,
                safelyRestart,
                safelyRestart,
                safelyRestart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        ServerRestart.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
