package me.xginko.serverrestart.module;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.config.Config;

public class LowTPSRestart implements ServerRestartModule, Runnable {
    private final ServerRestart plugin;

    public LowTPSRestart() {
        this.plugin = ServerRestart.getInstance();
        Config config = ServerRestart.getConfiguration();

    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void run() {

    }
}
