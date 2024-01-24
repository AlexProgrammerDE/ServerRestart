package me.xginko.serverrestart;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.serverrestart.event.AsyncHeartbeatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class AsyncHeart implements Runnable {

    private final @NotNull ScheduledTask HEARTBEAT;
    private final @NotNull AtomicLong LAST_CALL;

    AsyncHeart(long initialDelayMillis, long intervalMillis) {
        this.LAST_CALL = new AtomicLong();
        ServerRestart plugin = ServerRestart.getInstance();
        this.HEARTBEAT = plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                BEAT_TASK -> this.run(),
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
        if (ServerRestart.isRestarting) {
            this.HEARTBEAT.cancel(); // Self end
        } else {
            new AsyncHeartbeatEvent(this.LAST_CALL.get(), System.currentTimeMillis()).callEvent();
            this.LAST_CALL.set(System.currentTimeMillis());
        }
    }

    /**
     * Returns the current execution state of the heartbeat task.
     * @return the current execution state of the heartbeat task.
     */
    public @NotNull ScheduledTask.ExecutionState getExecutionState() {
        return this.HEARTBEAT.getExecutionState();
    }

    /**
     * Cancels the heartbeat, only completing once {@link AsyncHeart#getExecutionState()} == {@link ScheduledTask.ExecutionState#CANCELLED}.
     * If the heartbeat is currently being executed, no attempt is made to interrupt the heartbeat.
     */
    public @NotNull CompletableFuture<Void> stop() {
        this.HEARTBEAT.cancel();
        while (true) {
            if (this.getExecutionState() == ScheduledTask.ExecutionState.CANCELLED) {
                return CompletableFuture.completedFuture(null);
            }
        }
    }
}
