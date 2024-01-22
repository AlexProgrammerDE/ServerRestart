package me.xginko.serverrestart;

import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.config.LanguageCache;
import me.xginko.serverrestart.enums.RestartMode;
import me.xginko.serverrestart.events.GracefulServerRestartEvent;
import me.xginko.serverrestart.events.ServerRestartEvent;
import me.xginko.serverrestart.modules.ServerRestartModule;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public final class ServerRestart extends JavaPlugin {

    private static ServerRestart instance;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Config config;
    private static TPSCache tpsCache;
    private static Server server;
    private static Logger logger;
    private static boolean isFolia = false;
    private static boolean isRestarting = false;
    private static boolean joiningAllowed = false;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        server = getServer();

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            logger.info("Detected Folia");
            isFolia = true;
        } catch (ClassNotFoundException ignored) {}

        reloadLang();
        reloadConfiguration();
    }

    public static void restartGracefully(RestartMode mode, boolean kickAll, boolean saveAll, boolean disableJoining) {
        GracefulServerRestartEvent graceful = new GracefulServerRestartEvent(mode, kickAll, saveAll, disableJoining);
        if (!graceful.callEvent()) return;
        setJoiningAllowed(!graceful.shouldDisableJoin());
        if (graceful.shouldKickAll()) kickAll();
        if (graceful.shouldSaveAll()) saveAll();
        restartNow(graceful.getRestartMode());
    }

    public static void restartNow(RestartMode mode) {
        ServerRestartEvent restart = new ServerRestartEvent(mode);
        if (restart.callEvent()) {
            switch (restart.getRestartMode()) {
                case SPIGOT_RESTART -> server.spigot().restart();
                case BUKKIT_SHUTDOWN -> server.shutdown();
            }
        }
    }

    public static void kickAll() {
        for (Player player : server.getOnlinePlayers()) {
            player.kick(ServerRestart.getLang(player.locale()).server_is_restarting, PlayerKickEvent.Cause.RESTART_COMMAND);
        }
    }

    public static void saveAll() {
        server.savePlayers();
        for (World world : server.getWorlds()) {
            world.save();
        }
    }

    public static boolean isFolia() {
        return isFolia;
    }

    public static boolean isJoiningAllowed() {
        return joiningAllowed;
    }

    public static void setJoiningAllowed(boolean allowed) {
        joiningAllowed = allowed;
    }

    public static boolean isRestarting() {
        return isRestarting;
    }

    public static ServerRestart getInstance() {
        return instance;
    }

    public static Config getConfiguration() {
        return config;
    }

    public static TPSCache getTPSCache() {
        return tpsCache;
    }

    public static Logger getLog() {
        return logger;
    }

    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player player ? getLang(player.locale()) : getLang(config.default_lang);
    }

    public static LanguageCache getLang(String lang) {
        if (!config.auto_lang) return languageCacheMap.get(config.default_lang.toString().toLowerCase());
        return languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(config.default_lang.toString().toLowerCase()));
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
    }

    public void disablePlugin() {
        ServerRestartModule.modules.forEach(ServerRestartModule::disable);
        ServerRestartModule.modules.clear();
    }

    private void reloadConfiguration() {
        try {
            config = new Config();
            tpsCache = TPSCache.create(config.max_tps_check_interval_millis);
            ServerRestartModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Error loading config! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(getDataFolder() + File.separator + "lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                final String localeString = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf('.'));
                logger.info("Found language file for " + localeString);
                languageCacheMap.put(localeString, new LanguageCache(localeString));
            }
            final Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                final Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if (!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        logger.info("Found language file for "+ localeString);
                        languageCacheMap.put(localeString, new LanguageCache(localeString));
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
            e.printStackTrace();
        }
    }

    private Set<String> getDefaultLanguageFiles() {
        try (final JarFile pluginJarFile = new JarFile(this.getFile())) {
            return pluginJarFile.stream()
                    .map(ZipEntry::getName)
                    .filter(name -> name.startsWith("lang" + File.separator) && name.endsWith(".yml"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.severe("Failed getting default lang files! - " + e.getLocalizedMessage());
            return Collections.emptySet();
        }
    }
}
