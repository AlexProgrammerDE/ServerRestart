package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;
import me.xginko.serverrestart.enums.MessageMode;
import me.xginko.serverrestart.enums.RestartMethod;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import me.xginko.serverrestart.event.ServerRestartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RestartTimer implements ServerRestartModule, Listener {

    private final @NotNull Config config;
    public final @NotNull List<Duration> notifications;
    private @NotNull ZonedDateTime current_time;
    private final @NotNull MessageMode messageMode;
    private final boolean do_safe_restart;

    public RestartTimer() {
        shouldEnable();
        this.config = ServerRestart.getConfiguration();
        this.current_time = ZonedDateTime.now(config.timeZone);
        this.do_safe_restart = config.getBoolean("general.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will just immediately shutdown/restart (not advised).""");
        config.createTitledSection("Notifications", "notifications");
        MessageMode mode = MessageMode.ACTIONBAR;
        final String configuredMode = config.getString("notifications.notify-mode", mode.name(),
                "Available options are: " + String.join(", ", Arrays.stream(MessageMode.values()).map(Enum::name).toList()));
        try {
            mode = MessageMode.valueOf(configuredMode);
        } catch (IllegalArgumentException e) {
            ServerRestart.getLog().warn("MessageMode '"+configuredMode+"' is not a valid mode. Valid modes are as follows: ");
            ServerRestart.getLog().warn(String.join(", ", Arrays.stream(RestartMethod.values()).map(Enum::name).toList()));
        }
        this.messageMode = mode;
        this.notifications = config.getList("notifications.notify-times", List.of(
                "PT15M", "PT10M", "PT5M", "PT4M", "PT3M", "PT2M", "PT1M", "PT30S", "PT15S",
                "PT10S", "PT9S", "PT8S", "PT7S", "PT6S", "PT5S", "PT4S", "PT3S", "PT2S", "PT1S"))
                .stream().distinct().map(text -> {
                    try {
                        return Duration.parse(text);
                    } catch (DateTimeParseException e) {
                        ServerRestart.getLog().warn("Unable to parse Duration '"+text+"'. Is it formatted correctly?");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(Duration::toNanos))
                .toList();
        config.master().addComment("notifications.notify-times", """
                At which time remaining should we send a notification to all online players.\s
                \s
                The formats accepted are based on the ISO-8601 duration format PnDTnHnMn.nS with days\s
                considered to be exactly 24 hours.\s
                The string starts with an optional sign, denoted by the ASCII negative or positive symbol.\s
                If negative, the whole period is negated.\s
                The ASCII letter "P" is next in upper or lower case.\s
                There are then four sections, each consisting of a number and a suffix.\s
                The sections have suffixes in ASCII of "D", "H", "M" and "S" for days, hours, minutes and seconds,\s
                accepted in upper or lower case.\s
                The suffixes must occur in order. The ASCII letter "T" must occur before the first occurrence, if any,\s
                of an hour, minute or second section. At least one of the four sections must be present\s
                and if "T" is present, there must be at least one section after the "T".\s
                \s
                The number part of each section must consist of one or more ASCII digits.\s
                The number may be prefixed by the ASCII negative or positive symbol.\s
                The number of days, hours and minutes must parse to a long.\s
                The number of seconds must parse to a long with optional fraction.\s
                The decimal point may be either a dot or a comma.\s
                The fractional part may have from zero to 9 digits.\s
                \s
                EXAMPLES:\s
                   "PT20.345S" -- parses as "20.345 seconds"\s
                   "PT15M"     -- parses as "15 minutes" (where a minute is 60 seconds)\s
                   "PT10H"     -- parses as "10 hours" (where an hour is 3600 seconds)\s
                   "P2D"       -- parses as "2 days" (where a day is 24 hours or 86400 seconds)\s
                   "P2DT3H4M"  -- parses as "2 days, 3 hours and 4 minutes"\s
                   "PT-6H3M"    -- parses as "-6 hours and +3 minutes"\s
                   "-PT6H3M"    -- parses as "-6 hours and -3 minutes"\s
                   "-PT-6H+3M"  -- parses as "+6 hours and -3 minutes"
                """);
    }

    @Override
    public boolean shouldEnable() {
        return !ServerRestart.getConfiguration().RESTART_TIMES.isEmpty();
    }

    @Override
    public void enable() {
        ServerRestart plugin = ServerRestart.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBeat(AsyncHeartbeatEvent event) {
        if (ServerRestart.isRestarting) return;

        // Check if close to one of the configured restart times
        current_time = ZonedDateTime.now(config.timeZone);

        for (ZonedDateTime restartTime : config.RESTART_TIMES) {
            Duration between = Duration.between(current_time, restartTime);

        }


        // If close to restart, call RestartCountDownEvent

        // Get if RestartCountDown is cancelled / delayed, handle delay

        // If delayed, update restart time, note for RestartType -> DELAYED

        ServerRestartEvent restartEvent = new ServerRestartEvent(
                true,
                ServerRestartEvent.RestartType.SCHEDULED,
                ServerRestart.getConfiguration().RESTART_METHOD,
                do_safe_restart,
                do_safe_restart,
                do_safe_restart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        // Log to console

        ServerRestart.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
