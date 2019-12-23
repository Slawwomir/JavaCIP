package Chapter7;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.util.concurrent.*;

public class LogService2 {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final BlockingQueue<String> queue;
    private final PrintWriter printWriter;

    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogService2(BlockingQueue<String> queue, PrintWriter printWriter) {
        this.queue = queue;
        this.printWriter = printWriter;
    }

    public void start() {
    }

    public void stop() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            printWriter.close();
        }
    }

    public void log(String message) throws InterruptedException {
        try {
            executorService.execute(new WriterTask(message));
        } catch (RejectedExecutionException e) {
            // ignored
        }
    }

    private class WriterTask implements Runnable {

        private WriterTask(String message) {

        }

        @Override
        public void run() {
            // Mock
        }
    }
}