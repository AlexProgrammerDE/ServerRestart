package me.xginko.serverrestart;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class AsyncHeartbeat implements Runnable {

    private final @NotNull ScheduledTask HEARTBEAT;

    AsyncHeartbeat(long initialDelayMillis, long intervalMillis) {
        ServerRestart plugin = ServerRestart.getInstance();
        this.HEARTBEAT = plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                BEAT -> this.run(),
                initialDelayMillis,
                intervalMillis,
                TimeUnit.MILLISECONDS
        );
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
        new AsyncHeartbeatEvent().callEvent();
    }

    /**
     * Attempts to cancel the heartbeat, returning the result of the attempt. In all cases, if the heartbeat is currently
     * being executed no attempt is made to halt the heartbeat, however any executions in the future are halted.
     * @return the result of the cancellation attempt.
     */
    public @NotNull ScheduledTask.CancelledState stop() {
        return this.HEARTBEAT.cancel();
    }
}
