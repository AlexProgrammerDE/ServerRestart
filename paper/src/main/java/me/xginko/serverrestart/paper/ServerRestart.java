package me.xginko.serverrestart.paper;

import me.xginko.serverrestart.common.CachedTickReport;
import me.xginko.serverrestart.common.SRPermission;
import me.xginko.serverrestart.folia.FoliaTickReport;
import me.xginko.serverrestart.paper.commands.RestartsCmd;
import me.xginko.serverrestart.paper.config.PaperConfigCache;
import me.xginko.serverrestart.paper.config.PaperLangCache;
import me.xginko.serverrestart.paper.enums.RestartMethod;
import me.xginko.serverrestart.paper.module.ServerRestartModule;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public final class ServerRestart extends JavaPlugin {

    private static ServerRestart instance;
    private static PaperConfigCache config;
    private static Map<String, PaperLangCache> languageCacheMap;
    private static Server server;
    private static CachedTickReport cachedTickReport;
    private static ComponentLogger logger;
    public static boolean isFolia, isRestarting, joiningAllowed;

    @Override
    public void onEnable() {
        isFolia = joiningAllowed = isRestarting = false;
        instance = this;
        logger = getComponentLogger();
        server = getServer();

        // Register permissions
        for (SRPermission permission : SRPermission.values()) {
            new Permission(permission.permission(), permission.getDescription(), permission.getDefault()).recalculatePermissibles();
        }

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            logger.info("Detected Folia");
            isFolia = true;
        } catch (ClassNotFoundException ignored) {}

        reloadLang();
        reloadConfiguration();
        getCommand("restarts").setExecutor(new RestartsCmd());
    }

    public static void restart(RestartMethod method, boolean disableJoining, boolean kickAll, boolean saveAll) {
        isRestarting = true;

        if (disableJoining) {
            joiningAllowed = false;
        }

        if (kickAll) {
            for (Player player : server.getOnlinePlayers()) {
                player.getScheduler().run(instance, kick -> player.kick(
                        ServerRestart.getLang(player.locale()).server_restarting,
                        PlayerKickEvent.Cause.RESTART_COMMAND
                ), null);
            }
        }

        if (saveAll) {
            server.savePlayers();
            for (World world : server.getWorlds()) {
                world.save();
            }
        }

        switch (method) {
            case SPIGOT_RESTART -> server.spigot().restart();
            case BUKKIT_SHUTDOWN -> server.shutdown();
        }
    }

    public static ServerRestart getInstance() {
        return instance;
    }

    public static PaperConfigCache getConfiguration() {
        return config;
    }

    public static CachedTickReport getTickReports() {
        return cachedTickReport;
    }

    public static ComponentLogger getLog() {
        return logger;
    }

    public static PaperLangCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static PaperLangCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player player ? getLang(player.locale()) : getLang(config.default_lang);
    }

    public static PaperLangCache getLang(String lang) {
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
            config = new PaperConfigCache(this.getDataFolder());
            cachedTickReport = isFolia ? new FoliaTickReport(this, config.tick_report_cache_time) : new PaperTickReport(this, config.tick_report_cache_time);
            ServerRestartModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.error("Error loading config! - " + e.getLocalizedMessage());
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
                languageCacheMap.put(localeString, new PaperLangCache(localeString));
            }
            final Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                final Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if (!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        logger.info("Found language file for "+ localeString);
                        languageCacheMap.put(localeString, new PaperLangCache(localeString));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
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
            logger.error("Failed getting default lang files! - " + e.getLocalizedMessage());
            return Collections.emptySet();
        }
    }
}
