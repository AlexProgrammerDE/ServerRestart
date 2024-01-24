package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class RestartDelay implements ServerRestartModule, Listener {

    public RestartDelay() {}

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
    private void onPreRestart() {
        // Delay logic
    }
}
