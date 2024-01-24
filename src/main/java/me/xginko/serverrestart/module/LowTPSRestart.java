package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicLong;

public class LowTPSRestart implements ServerRestartModule, Listener {
    private static final AtomicLong consec_millis_spent_lagging = new AtomicLong();

    private final ServerRestart plugin;

    public LowTPSRestart() {
        shouldEnable();
        this.plugin = ServerRestart.getInstance();
        consec_millis_spent_lagging.set(0);
        Config config = ServerRestart.getConfiguration();
        config.createTitledSection("Fire Extinguisher", "restart-on-low-TPS");
        config.master().addComment("restart-on-low-TPS.enable",
                "Reboot the server when below the configured tps value for the configurable amount of time.");

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

    }
}
