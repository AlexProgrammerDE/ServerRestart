package me.xginko.serverrestart.config;


import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.serverrestart.ServerRestart;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;
import java.util.*;
import java.util.stream.Collectors;

public class Config {

    private final @NotNull ConfigFile configFile;
    public final @NotNull ZonedDateTime INIT_TIME;
    public final @NotNull Set<ZonedDateTime> RESTART_TIMES;
    public final @NotNull Locale default_lang;
    public final @NotNull Duration playercount_restart_delay;
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
            zoneId = ZoneId.of(getString("general.timezone", zoneId.getId(),
                    "The ZoneId to use for scheduling restart times."));
        } catch (ZoneRulesException e) {
            ServerRestart.getLog().warning("Configured timezone could not be found. Using '"+zoneId+"' (System Default)");
        } catch (DateTimeException e) {
            ServerRestart.getLog().warning("Configured timezone has an invalid format. Using '"+zoneId+"' (System Default)");
        }
        this.INIT_TIME = ZonedDateTime.now(zoneId);

        // Playercount Settings
        this.playercount_delay_enabled = getBoolean("playercount.delay-restart-on-high-playercount", false,
                "If enabled, will only restart once playercount is below the configured number.");
        this.playercount_restartable = getInt("playercount.min-players-for-delay", 10,
                "If the playercount is this value or bigger, restart logic will be delayed.");
        this.playercount_restart_delay = Duration.ofSeconds(getInt("playercount.delay-seconds", 300,
                "Time in seconds until plugin will check again if it can restart."));

        // Restart times
        this.RESTART_TIMES = getList("restart-times", List.of("02:00:00", "03:00:00", "04:00:00"))
                .stream()
                .distinct()
                .map(timeString -> {
                    try {
                        final String[] numbers = timeString.split(":");
                        return this.getRestartTime(
                                Integer.parseInt(numbers[0]),
                                Integer.parseInt(numbers[1]),
                                Integer.parseInt(numbers[2]));
                    } catch (Exception e) {
                        ServerRestart.getLog().warning("Restart time '"+timeString+"' is not formatted properly. " +
                                "Format: 23:59:59 -> hour:minute:second");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        if (this.RESTART_TIMES.isEmpty()) {
            final ZonedDateTime am2zoned = this.getRestartTime(2,0,0);
            this.RESTART_TIMES.add(am2zoned);
            ServerRestart.getLog().warning("Queued 1 restart for " + am2zoned + " due to restart times being invalid or empty.");
        }

    }

    private @NotNull ZonedDateTime getRestartTime(int hours, int minutes, int seconds) throws DateTimeException {
        ZonedDateTime nextRestart = this.INIT_TIME.withHour(hours).withMinute(minutes).withSecond(seconds);
        if (this.INIT_TIME.isAfter(nextRestart) || this.INIT_TIME.isEqual(nextRestart))
            return nextRestart.plusDays(1);
        return nextRestart;
    }

    public void saveConfig() {
        try {
            this.configFile.save();
        } catch (Exception e) {
            ServerRestart.getLog().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    @NotNull
    public ConfigFile master() {
        return configFile;
    }

    public void createTitledSection(String title, @NotNull String path) {
        this.configFile.addSection(title);
        this.configFile.addDefault(path, null);
    }

    public boolean getBoolean(@NotNull String path, boolean def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getBoolean(path, def);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getBoolean(path, def);
    }

    @NotNull
    public String getString(@NotNull String path, @NotNull String def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getString(path, def);
    }

    @NotNull
    public String getString(@NotNull String path, @NotNull String def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getString(path, def);
    }

    public double getDouble(@NotNull String path, double def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getDouble(path, def);
    }

    public double getDouble(@NotNull String path, double def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getDouble(path, def);
    }

    public int getInt(@NotNull String path, int def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getInteger(path, def);
    }

    public int getInt(@NotNull String path, int def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getInteger(path, def);
    }

    @NotNull
    public List<String> getList(@NotNull String path, List<String> def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getStringList(path);
    }

    @NotNull
    public List<String> getList(@NotNull String path, List<String> def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getStringList(path);
    }
}
