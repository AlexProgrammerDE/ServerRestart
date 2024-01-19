package me.xginko.serverrestart.modules;

import me.xginko.serverrestart.ServerRestart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class DisallowJoinOnRestart implements ServerRestartModule, Listener {

    public DisallowJoinOnRestart() {

    }

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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerAttemptJoin(AsyncPlayerPreLoginEvent event) {
        if (ServerRestart.isRestarting()) event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                ServerRestart.getLang(ServerRestart.getConfigImpl().default_lang).server_is_restarting
        );
    }
}
