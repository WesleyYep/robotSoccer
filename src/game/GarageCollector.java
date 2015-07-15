package game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wesley on 15/07/2015.
 */
public class GarageCollector {
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void runGarbageCollectScheduler() {
        final Runnable beeper = new Runnable() {
            public void run() {
                System.gc();
                System.out.println("garbage collection done!"); }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 1, 1, TimeUnit.MINUTES);
        scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(false); }
        }, 60, TimeUnit.MINUTES);
    }
}
