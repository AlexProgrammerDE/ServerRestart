package me.xginko.serverrestart.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.xginko.serverrestart.velocity.config.VelocityConfigImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "me.xginko", name = "ProxyServerRestart", version = "1.0.0")
public final class ProxyServerRestart {

    private static ProxyServerRestart instance;
    private static VelocityConfigImpl config;
    private static ProxyServer proxyServer;
    private static ComponentLogger logger;
    private static Path pluginDirPath;

    @Inject
    public ProxyServerRestart(ProxyServer server, @DataDirectory Path pluginDir) throws IOException {
        proxyServer = server;
        pluginDirPath = pluginDir;
        logger = ComponentLogger.logger();
    }

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        proxyServer.getScheduler()
            .buildTask(this, () -> proxyServer.shutdown(Component.text("Server restarting.")))
            .delay(delayMillis, TimeUnit.MILLISECONDS)
            .schedule();
    }

    private void reloadConfig() {
        try {
            config = new VelocityConfigImpl(pluginDirPath.toFile());

            config.saveConfig();
        } catch (Exception e) {
            logger.error("Error loading config! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static ComponentLogger getLogger() {
        return logger;
    }

    public static ProxyServer getServer() {
        return proxyServer;
    }
}
