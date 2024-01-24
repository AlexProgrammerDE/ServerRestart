package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.event.RestartCountDownEvent;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class PlayerCountDelay implements ServerRestartModule, Listener {

    private final Server server;
    private final long delayMillis;
    private final int minPlayersToDelay;

    public PlayerCountDelay() {
        shouldEnable();
        this.server = ServerRestart.getInstance().getServer();
        Config config = ServerRestart.getConfiguration();
        config.master().addComment("player-count-delay.delay-restart-on-high-count",
                "If enabled, will only restart once playercount is below the configured number.");
        this.minPlayersToDelay = Math.max(1, config.getInt("player-count-delay.min-players-for-delay", 20,
                "If the playercount is this value or bigger, restart logic will be delayed."));
        this.delayMillis = TimeUnit.SECONDS.toMillis(Math.max(1, config.getInt("player-count-delay.delay-seconds", 300,
                "Time in seconds until plugin will check again if it can restart.")));
    }

    @Override
    public boolean shouldEnable() {
        return ServerRestart.getConfiguration().getBoolean("player-count-delay.delay-restart-on-high-count", false);
    }

    @Override
    public void enable() {
        ServerRestart plugin = ServerRestart.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCountdown(RestartCountDownEvent event) {
        if (server.getOnlinePlayers().size() >= minPlayersToDelay) {
            event.setCancelled(true);
            event.setDelayMillis(delayMillis);
            // Log
        }
    }
}
