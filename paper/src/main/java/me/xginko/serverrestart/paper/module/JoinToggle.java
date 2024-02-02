package me.xginko.serverrestart.paper.module;

import me.xginko.serverrestart.paper.ServerRestartPaper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class JoinToggle implements ServerRestartModule, Listener {

    public JoinToggle() {}

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public void enable() {
        ServerRestartPaper plugin = ServerRestartPaper.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!ServerRestartPaper.joiningAllowed || ServerRestartPaper.isRestarting) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ServerRestartPaper.getLang(ServerRestartPaper.getConfiguration().default_lang).server_restarting
            );
        }
    }
}
