package Chapter8;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@ThreadSafe
public class BoundedExecutor {
    private final Executor executor;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor executor, int bound) {
        this.executor = executor;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable runnable) throws InterruptedException {
        semaphore.acquire();

        try {
            executor.execute(runnable);
        } finally {
            semaphore.release();
        }
    }
}

class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }

    private static class MyAppThread extends Thread {
        static final String DEFAULT_NAME = "MyAppThread";
        private static volatile boolean debugLifecycle = false;
        private static final AtomicInteger created = new AtomicInteger();
        private static final AtomicInteger alive = new AtomicInteger();
        private static final Logger log = Logger.getAnonymousLogger();

        public MyAppThread(Runnable r) {
            this(r, DEFAULT_NAME);
        }

        MyAppThread(Runnable r, String name) {
            super(r, name + "-" + created.incrementAndGet());
            setUncaughtExceptionHandler((thread, throwable) -> {
                log.log(Level.SEVERE, "UNCAUGHT in thread " + thread.getName(), throwable);
            });
        }

        @Override
        public void run() {
            boolean debug = debugLifecycle;
            if (debug) {
                log.log(Level.FINE, "Created " + getName());
            }
            try {
                alive.incrementAndGet();
                super.run();
            } finally {
                alive.decrementAndGet();
                if (debug) {
                    log.log(Level.FINE, "Exiting " + getName());
                }
            }
        }

        public static boolean isDebugLifecycle() {
            return debugLifecycle;
        }

        public static AtomicInteger getCreated() {
            return created;
        }

        public static AtomicInteger getAlive() {
            return alive;
        }

        public static void setDebugLifecycle(boolean debugLifecycle) {
            MyAppThread.debugLifecycle = debugLifecycle;
        }
    }
}
