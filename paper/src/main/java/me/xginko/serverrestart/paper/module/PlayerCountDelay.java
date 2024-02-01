package me.xginko.serverrestart.paper.module;

import me.xginko.serverrestart.paper.ServerRestart;
import me.xginko.serverrestart.common.CommonUtil;
import me.xginko.serverrestart.paper.config.PaperConfigImpl;
import me.xginko.serverrestart.paper.event.PreRestartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PlayerCountDelay implements ServerRestartModule, Listener {

    private final Server server;
    private final long delay_millis;
    private final int min_players_for_delay;
    private final boolean should_log;

    public PlayerCountDelay() {
        shouldEnable();
        this.server = ServerRestart.getInstance().getServer();
        PaperConfigImpl config = ServerRestart.getConfiguration();
        config.master().addComment("restart-delay.player-count.enable",
                "If enabled, will only restart once playercount is below the configured number.");
        this.should_log = config.getBoolean("restart-delay.player-count.log", true);
        this.min_players_for_delay = Math.max(1, config.getInt("restart-delay.player-count.min-players-for-delay", 20,
                "If the player count is this value or bigger, restart logic will be delayed."));
        this.delay_millis = TimeUnit.SECONDS.toMillis(Math.max(1, config.getInt("restart-delay.player-count.delay-seconds", 300,
                "Time in seconds until plugin will check again if it can restart.")));
    }

    @Override
    public boolean shouldEnable() {
        return ServerRestart.getConfiguration().getBoolean("restart-delay.player-count.enable", false);
    }

    @Override
    public void enable() {
        server.getPluginManager().registerEvents(this, ServerRestart.getInstance());
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCountdown(PreRestartEvent event) {
        if (server.getOnlinePlayers().size() < min_players_for_delay) return;

        event.setCancelled(true);
        event.setDelayMillis(delay_millis);

        if (should_log) ServerRestart.getLog().info(Component.text("Server restart has been delayed by " +
                CommonUtil.formatDuration(Duration.ofMillis(delay_millis)) + " due to high player count.").color(NamedTextColor.GOLD));
    }
}
