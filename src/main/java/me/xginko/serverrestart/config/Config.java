package me.xginko.serverrestart.config;


import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.serverrestart.ServerRestart;

import java.io.File;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.List;
import java.util.Locale;

public class Config {

    private final ConfigFile configFile;
    public final Locale default_lang;
    public final ZoneId timeZone;
    public final Duration playercount_restart_delay;
    public final long max_tps_check_interval_millis;
    public final int playercount_restartable;
    public final boolean auto_lang, playercount_delay_enabled;

    public Config() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = ServerRestart.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            ServerRestart.getLog().severe("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        this.configFile = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));

        // Language Settings
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us",
                "The default language that will be used if auto-language is false or no matching language file was found.")
                .replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true,
                "If set to true, will display messages based on client language");

        // General Settings
        this.max_tps_check_interval_millis = getInt("general.tps-cache-time-ticks", 40,
                "How long a checked tps is cached to save resources in ticks (1 sec = 20 ticks)") * 50L;

        ZoneId zoneId = ZoneId.systemDefault();
        try {
            zoneId = ZoneId.of(getString("general.timezone", ZoneId.systemDefault().getId(),
                    "The ZoneId to use for scheduling restart times."));
        } catch (ZoneRulesException e) {
            ServerRestart.getLog().warning("Configured timezone could not be found. Using system default.");
        } catch (DateTimeException e) {
            ServerRestart.getLog().warning("Configured timezone has an invalid format. Using system default.");
        }
        this.timeZone = zoneId;

        // Playercount Settings
        this.playercount_delay_enabled = getBoolean("playercount.delay-restart-on-high-playercount", false,
                "If enabled, will only restart once playercount is below the configured number.");
        this.playercount_restartable = getInt("playercount.min-players-for-delay", 10,
                "If the playercount is this value or bigger, restart logic will be delayed.");
        this.playercount_restart_delay = Duration.ofSeconds(getInt("playercount.delay-seconds", 300,
                "Time in seconds until plugin will check again if it can restart."));
    }

    public void saveConfig() {
        try {
            this.configFile.save();
        } catch (Exception e) {
            ServerRestart.getLog().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public ConfigFile master() {
        return configFile;
    }

    public void createTitledSection(String title, String path) {
        this.configFile.addSection(title);
        this.configFile.addDefault(path, null);
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getString(path, def);
    }

    public String getString(String path, String def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getString(path, def);
    }

    public double getDouble(String path, double def, String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getDouble(path, def);
    }

    public double getDouble(String path, double def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getInteger(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getStringList(path);
    }
}
