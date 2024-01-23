package me.xginko.serverrestart.modules;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.events.GracefulServerRestartEvent;
import me.xginko.serverrestart.events.PreRestartCountdownEvent;
import me.xginko.serverrestart.events.ServerRestartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class DelayRestartOnPlayerCount implements ServerRestartModule, Listener {

    public DelayRestartOnPlayerCount() {}

    @Override
    public boolean shouldEnable() {
        return true;
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
    private void onPreRestartCountdown(PreRestartCountdownEvent event) {
        // Delay logic
    }
}
