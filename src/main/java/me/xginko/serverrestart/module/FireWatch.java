package me.xginko.serverrestart.module;

import me.xginko.serverrestart.CachedTickData;
import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import me.xginko.serverrestart.event.ServerRestartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FireWatch implements ServerRestartModule, Listener {

    private final CachedTickData tickData;
    private final AtomicLong millis_spent_lagging;
    private final long max_millis_lagging;
    private final double restart_tps, restart_mspt;
    private final boolean do_safe_restart;

    public FireWatch() {
        shouldEnable();
        this.tickData = ServerRestart.getTickData();
        this.millis_spent_lagging = new AtomicLong();
        Config config = ServerRestart.getConfiguration();
        config.createTitledSection("Fire Watch", "fire-watch");
        config.master().addComment("fire-watch.enable",
                "Reboot the server when lagging for a configurable amount of time.");
        this.do_safe_restart = config.getBoolean("fire-watch.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will just immediately shutdown/restart.""");
        this.restart_tps = Math.max(0.01, config.getDouble("fire-watch.restart-TPS", 12.5,
                "The TPS (ticks per seconds) at which to start taking measures."));
        this.restart_mspt = Math.max(0.01, config.getDouble("fire-watch.restart-MSPT", 100.0,
                "The MSPT (milliseconds per tick) at which to start taking measures."));
        this.max_millis_lagging = TimeUnit.SECONDS.toMillis(Math.max(1, config.getInt("fire-watch.min-lag-duration", 10,
                "How long in seconds the server needs to be lower than the configured tps to restart.")));
    }

    @Override
    public boolean shouldEnable() {
        return ServerRestart.getConfiguration().getBoolean("fire-extinguisher.enable", true);
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

        final double tps = tickData.getTPS();
        final double mspt = tickData.getMSPT();

        if (tps > restart_tps && mspt < restart_mspt) {
            millis_spent_lagging.set(0L); // No lag, reset time
            return;
        }

        if (millis_spent_lagging.addAndGet(System.currentTimeMillis() - event.getPostLastCallTime()) <= max_millis_lagging) {
            return; // Not lagging for long enough yet
        }

        ServerRestartEvent restartEvent = new ServerRestartEvent(
                true,
                ServerRestartEvent.RestartType.ON_FIRE,
                ServerRestart.getConfiguration().RESTART_METHOD,
                do_safe_restart,
                do_safe_restart,
                do_safe_restart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        ServerRestart.getLog().error(Component.text("Restarting server because on fire! - " +
                        "TPS: " + String.format("%.2f", tps) + ", " +
                        "MSPT: " + String.format("%.2f", mspt))
                .color(TextColor.color(255, 81, 112)).decorate(TextDecoration.BOLD));

        ServerRestart.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
