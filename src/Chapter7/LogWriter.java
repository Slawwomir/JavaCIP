package Chapter7;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;

    private boolean shutdownRequested;

    public LogWriter(PrintWriter writer) {
        this.queue = new LinkedBlockingQueue<>();
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        logger.start();
    }

    public void log(String message) throws InterruptedException {
        queue.put(message);
    }

    public void logImproved(String message) throws InterruptedException {
        if (!shutdownRequested) {
            queue.put(message);
        } else {
            throw new IllegalStateException("Logger is shutdown");
        }
    }

    public void shutdown() {
        this.shutdownRequested = true;
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer;

        private LoggerThread(PrintWriter writer) {
            this.writer = writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    writer.println(queue.take());
                }
            } catch (InterruptedException e) {
                // ignored
            } finally {
                writer.close();
            }
        }
    }
}
