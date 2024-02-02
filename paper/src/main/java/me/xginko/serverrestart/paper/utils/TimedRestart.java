package me.xginko.serverrestart.paper.utils;

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

    }
}
