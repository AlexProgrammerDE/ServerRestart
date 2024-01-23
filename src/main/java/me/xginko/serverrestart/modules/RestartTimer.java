package me.xginko.serverrestart.modules;

import me.xginko.serverrestart.ServerRestart;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RestartTimer extends TimerTask implements ServerRestartModule {

    public RestartTimer() {

    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public void enable() {
        Timer timer = new Timer(ServerRestart.getInstance().getName()+"_Restart-Timer_"+ UUID.randomUUID());
        // init delay: duration.of first restarttime -> now - 15min
        // period: 1sec
        timer.scheduleAtFixedRate(this, 1000L, 1000L);
    }

    @Override
    public void disable() {
        this.cancel();
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

    }
}
