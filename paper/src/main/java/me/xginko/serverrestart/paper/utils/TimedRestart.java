package me.xginko.serverrestart.paper.utils;

import me.xginko.serverrestart.paper.ServerRestart;
import me.xginko.serverrestart.paper.event.PreRestartEvent;
import me.xginko.serverrestart.paper.event.RestartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.time.ZonedDateTime;

public class TimedRestart implements Runnable {

    private long millis_until_countdown;
    private boolean do_safe_restart;

    public TimedRestart(ZonedDateTime restartTime, boolean do_safe_restart) {

    }

    private void schedule() {

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
        if (ServerRestart.isRestarting) return;

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
                ServerRestart.getConfiguration().restart_method,
                do_safe_restart,
                do_safe_restart,
                do_safe_restart
        );

        if (!restartEvent.callEvent()) {
            return;
        }

        ServerRestart.getLog().info(Component.text("Restarting server!")
                .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        ServerRestart.restart(
                restartEvent.getMethod(),
                restartEvent.getDisableJoin(),
                restartEvent.getKickAll(),
                restartEvent.getSaveAll()
        );
    }
}
