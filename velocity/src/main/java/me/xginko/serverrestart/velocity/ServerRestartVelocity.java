package me.xginko.serverrestart.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import me.xginko.serverrestart.velocity.commands.VelocityRestartsCmd;
import me.xginko.serverrestart.velocity.config.VelocityConfigCache;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import javax.inject.Inject;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public final class ServerRestartVelocity {

    private static VelocityConfigCache config;
    private static ComponentLogger logger;
    private final ProxyServer server;
    private final Path pluginDir;

    @Inject
    public ServerRestartVelocity(ProxyServer server, @DataDirectory Path pluginDir) {
        this.pluginDir = pluginDir;
        this.server = server;
        logger = ComponentLogger.logger();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reloadPlugin();
        new VelocityRestartsCmd(this, server.getCommandManager()).register();
    }

    public static ComponentLogger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public void disablePlugin() {
        server.getScheduler().tasksByPlugin(this).forEach(ScheduledTask::cancel);
    }

    public void reloadPlugin() {
        disablePlugin();
        reloadConfig();
        config.restart_times.forEach(restart_time -> server.getScheduler()
                .buildTask(this, shutdown -> server.shutdown(config.server_restarting))
                .delay(Duration.between(ZonedDateTime.now(config.time_zone_id), restart_time).getSeconds(), TimeUnit.SECONDS)
                .repeat(Duration.ofDays(1))
                .schedule());
    }

    private void reloadConfig() {
        try {
            config = new VelocityConfigCache(pluginDir.toFile());
            config.saveConfig();
        } catch (Exception e) {
            logger.error("Error loading config! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
