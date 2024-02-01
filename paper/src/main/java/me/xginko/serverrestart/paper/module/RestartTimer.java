package me.xginko.serverrestart.paper.module;

import me.xginko.serverrestart.paper.ServerRestart;
import me.xginko.serverrestart.paper.config.PaperConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;

public class RestartTimer implements ServerRestartModule {

    private final @NotNull PaperConfigImpl config;
    private final boolean do_safe_restart;

    private @Nullable ZonedDateTime current_time;
    private @Nullable Duration between;

    public RestartTimer() {
        shouldEnable();
        this.config = ServerRestart.getConfiguration();
        this.do_safe_restart = config.getBoolean("general.restart-gracefully", true, """
                Will disable joining, kick all players and save everything before restarting.\s
                If set to false, will immediately shutdown/restart (not advised).""");
    }

    @Override
    public boolean shouldEnable() {
        return !ServerRestart.getConfiguration().restart_times.isEmpty();
    }

    @Override
    public void enable() {
        ServerRestart plugin = ServerRestart.getInstance();
    }

    @Override
    public void disable() {

    }


}
