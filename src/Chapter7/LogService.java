package Chapter7;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter printWriter;

    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogService(BlockingQueue<String> queue, PrintWriter printWriter) {
        this.queue = queue;
        this.printWriter = printWriter;
        this.loggerThread = new LoggerThread();
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String message) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException("...");
            }
            reservations++;
        }
        queue.put(message);
    }

    private class LoggerThread extends Thread {
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (this) {
                            if (isShutdown && reservations == 0) {
                                break;
                            }

                            String message = queue.take();

                            synchronized (this) {
                                reservations--;
                            }

                            printWriter.println(message);

                        }
                    } catch (InterruptedException e) {
                        /* Retry */
                    }
                }
            } finally {
                printWriter.close();
            }
        }
    }
}
