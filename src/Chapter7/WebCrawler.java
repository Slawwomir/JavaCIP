package Chapter7;

import net.jcip.annotations.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class WebCrawler {
    private volatile TrackingExecutor executor;
    @GuardedBy("this")
    private final Set<URL> urlsToCrawl = new HashSet<>();

    public synchronized void start() {
        executor = new TrackingExecutor();
        for (URL url : urlsToCrawl) {
            submitCrawlTask(url);
        }
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(executor.shutdownNow());
            if (executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                saveUncrawled(executor.getCancelledTasks());
            }
        } finally {
            executor = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    private void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable runnable : uncrawled) {
            urlsToCrawl.add(((CrawlTask) runnable).getPage());
        }
    }

    private void submitCrawlTask(URL url) {
        executor.execute(new CrawlTask(url));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        private CrawlTask(URL url) {
            this.url = url;
        }

        @Override
        public void run() {
            for (URL url1 : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                submitCrawlTask(url1);
            }
        }

        public URL getPage() {
            return url;
        }
    }
}
