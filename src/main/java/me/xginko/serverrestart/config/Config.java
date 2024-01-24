package me.xginko.serverrestart.config;


import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.enums.RestartMethod;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.DateTimeException;
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
    public final @NotNull RestartMethod METHOD;
    public final long max_tps_check_interval_millis, heartbeat_initial_delay_millis, heartbeat_interval_millis;
    public final boolean auto_lang;

    public Config() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = ServerRestart.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            ServerRestart.getLog().severe("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        this.configFile = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));

        // Language Settings
        this.createTitledSection("Language Settings", "language");
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us",
                "The default language that will be used if auto-language is false or no matching language file was found.")
                .replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true,
                "If set to true, will display messages based on client language");

        // General Settings
        this.createTitledSection("General Settings", "general");
        RestartMethod method = RestartMethod.BUKKIT_SHUTDOWN;
        String configuredMethod = getString("general.restart-method", RestartMethod.BUKKIT_SHUTDOWN.name(),
                "Available options are: " + String.join(", ", Arrays.stream(RestartMethod.values()).map(Enum::name).toList()));
        try {
            method = RestartMethod.valueOf(configuredMethod);
        } catch (IllegalArgumentException e) {
            ServerRestart.getLog().warning("RestartMethod '"+configuredMethod+"' is not valid. Valid values are: " +
                    String.join(", ", Arrays.stream(RestartMethod.values()).map(Enum::name).toList()));
        }
        this.METHOD = method;
        ZoneId zoneId = ZoneId.systemDefault();
        try {
            zoneId = ZoneId.of(getString("general.timezone", zoneId.getId(),
                    "The TimeZone (ZoneId) to use for scheduling restart times."));
        } catch (ZoneRulesException e) {
            ServerRestart.getLog().warning("Configured timezone could not be found. Using host zone '"+zoneId+"' (System Default)");
        } catch (DateTimeException e) {
            ServerRestart.getLog().warning("Configured timezone has an invalid format. Using '"+zoneId+"' (System Default)");
        }
        this.INIT_TIME = ZonedDateTime.now(zoneId);
        this.max_tps_check_interval_millis = Math.max(getInt("general.tps-cache-time-ticks", 40,
                "How long a checked tps is cached to save resources in ticks (1 sec = 20 ticks)"), 20) * 50L;
        this.heartbeat_initial_delay_millis = getInt("general.heartbeat.initial-delay-millis", 5000);
        this.heartbeat_interval_millis = getInt("general.heartbeat.interval-millis", 1000);

        // Restart times
        this.createTitledSection("Restart Times", "restart-times");
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
            final ZonedDateTime am2zoned = this.getRestartTime(2,30,0);
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

    public @NotNull ConfigFile master() {
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

    public int getInt(@NotNull String path, int def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getInteger(path, def);
    }

    public int getInt(@NotNull String path, int def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getInteger(path, def);
    }

    public double getDouble(@NotNull String path, double def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getDouble(path, def);
    }

    public double getDouble(@NotNull String path, double def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getDouble(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getString(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getString(path, def);
    }

    public @NotNull List<String> getList(@NotNull String path, List<String> def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getStringList(path);
    }

    public @NotNull List<String> getList(@NotNull String path, List<String> def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getStringList(path);
    }
}
