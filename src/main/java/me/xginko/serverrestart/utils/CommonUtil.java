package me.xginko.serverrestart.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class CommonUtil {

    public static @NotNull String formatDuration(Duration duration) {
        final int seconds = duration.toSecondsPart();
        final int minutes = duration.toMinutesPart();
        final int hours = duration.toHoursPart();

        if (hours > 0) {
            return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02dm %02ds", minutes, seconds);
        } else {
            return String.format("%02ds", seconds);
        }
    }
}
