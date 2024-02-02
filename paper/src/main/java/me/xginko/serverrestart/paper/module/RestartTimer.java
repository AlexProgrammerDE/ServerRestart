package me.xginko.serverrestart.paper.module;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.serverrestart.paper.ServerRestartPaper;
import me.xginko.serverrestart.paper.config.PaperConfigCache;
import me.xginko.serverrestart.paper.enums.RestartTaskState;
import me.xginko.serverrestart.paper.event.PreRestartEvent;
import me.xginko.serverrestart.paper.event.RestartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RestartTimer implements ServerRestartModule {

    private final Set<ScheduledTask> pendingRestarts;
    private RestartTask runningRestart;
    private final @NotNull PaperConfigCache config;
    private final boolean do_safe_restart;
    private @Nullable ZonedDateTime current_time;

    private record RestartTask(ScheduledTask task, RestartTaskState state) {}

    public RestartTimer() {
        shouldEnable();
        this.config = ServerRestartPaper.getConfiguration();
        this.pendingRestarts = new HashSet<>(config.restart_times.size());
        this.do_safe_restart = config.getBoolean("general.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will immediately shutdown/restart (not advised).""");
    }

    @Override
    public boolean shouldEnable() {
        return !ServerRestartPaper.getConfiguration().restart_times.isEmpty();
    }

    @Override
    public void enable() {
        ServerRestartPaper plugin = ServerRestartPaper.getInstance();
        AsyncScheduler async = plugin.getServer().getAsyncScheduler();
        for (ZonedDateTime restart_time : config.restart_times) {
            this.pendingRestarts.add(async.runDelayed(
                    plugin,
                    initRestart -> initiateRestart(),
                    Duration.between(ZonedDateTime.now(config.time_zone_id), restart_time).toNanos(),
                    TimeUnit.NANOSECONDS
            ));
        }
    }

    @Override
    public void disable() {
        this.pendingRestarts.forEach(ScheduledTask::cancel);
        if (this.runningRestart != null) runningRestart.cancel();
    }

    private void initiateRestart() {
        if (ServerRestartPaper.isRestarting) return;



        PreRestartEvent preRestartEvent = new PreRestartEvent(true);

        if (!preRestartEvent.callEvent()) {
            if (preRestartEvent.getDelayMillis() > 0L) {

            }

            return;
        }

        // Get if RestartCountDown is canceled / delayed, handle delay

        // If delayed, update restart time, note for RestartType -> DELAYED



        RestartEvent restartEvent = new RestartEvent(
                true,
                RestartEvent.RestartType.SCHEDULED,
                ServerRestartPaper.getConfiguration().restart_method,
                do_safe_restart,
                do_safe_restart,
                do_safe_restart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        ServerRestartPaper.getLog().info(Component.text("Restarting server!")
                .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        ServerRestartPaper.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
