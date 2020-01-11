package Chapter6;

import java.util.List;
import java.util.concurrent.*;

public class Renderer {
    private static final long TIME_BUDGET_NANOS = 5_000_000_000L; // 5s
    private final ExecutorService executor;

    public Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        CompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        imageInfos.forEach(imageInfo -> completionService.submit(imageInfo::downloadImage));

        renderText(source);
        try {
            for (int i = 0; i < imageInfos.size(); i++) {
                Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    Page renderPageWithAd() {
        long endNanos = System.nanoTime() + TIME_BUDGET_NANOS;
        Future<Ad> f = executor.submit(new FetchAdTask());
        // Render page
        Page page = renderPageBody();
        Ad ad;

        try {
            // Only wait for the remaining time budget
            long timeLeft = endNanos - System.nanoTime();
            ad = f.get(timeLeft, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            f.cancel(true);
        } catch (ExecutionException ignored) {
        }

        page.setAd(ad);
        return page;
    }

    private void renderImage(ImageData imageData) {
        // Render image
    }

    private void renderText(CharSequence source) {
        // Render text
    }

    private List<ImageInfo> scanForImageInfo(CharSequence source) {
        // Scan for images
        return List.of();
    }

    private interface ImageInfo {
        ImageData downloadImage();
    }

    private interface ImageData {

    }

    private interface Page {
        void setAd(Ad ad);
    }

    private interface Ad {

    }
}

