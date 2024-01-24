package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import me.xginko.serverrestart.event.ServerRestartEvent;
import me.xginko.serverrestart.utils.CommonUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LowTPSRestart implements ServerRestartModule, Listener {

    private final AtomicLong millisSpentLagging;
    private final long maxLagMillis;
    private final double restartTPS;
    private final boolean safelyRestart;

    public LowTPSRestart() {
        shouldEnable();
        this.millisSpentLagging = new AtomicLong();
        Config config = ServerRestart.getConfiguration();
        config.createTitledSection("Fire Extinguisher", "restart-on-low-TPS");
        config.master().addComment("restart-on-low-TPS.enable",
                "Reboot the server when below the configured tps value for the configurable amount of time.");
        this.safelyRestart = config.getBoolean("restart-on-low-TPS.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will just immediately shutdown/restart.""");
        this.restartTPS = Math.max(0.01, config.getDouble("restart-on-low-TPS.restart-TPS", 12.5,
                "The tps at which to start taking measures."));
        this.maxLagMillis = TimeUnit.SECONDS.toMillis(Math.max(1, config.getInt("restart-on-low-TPS.min-lag-duration", 10,
                "How long in seconds the server needs to be lower than the configured tps to restart.")));
    }

    @Override
    public boolean shouldEnable() {
        return ServerRestart.getConfiguration().getBoolean("restart-on-low-TPS.enable", true);
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

        if (ServerRestart.getTPSCache().getTPS() > restartTPS) {
            millisSpentLagging.set(0L); // No lag, reset time
            return;
        }

        if (millisSpentLagging.addAndGet(System.currentTimeMillis() - event.getLastCallEndTime()) <= maxLagMillis) {
            return; // Not lagging for long enough yet
        }

        ServerRestartEvent restartEvent = new ServerRestartEvent(
                true,
                ServerRestartEvent.RestartType.ON_FIRE,
                ServerRestart.getConfiguration().RESTART_METHOD,
                safelyRestart,
                safelyRestart,
                safelyRestart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        ServerRestart.getLog().severe("Restarting server because on fire! - TPS was lower than " +
                String.format("%.2f", restartTPS) + " for " + CommonUtil.formatDuration(Duration.ofMillis(maxLagMillis)));

        ServerRestart.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
