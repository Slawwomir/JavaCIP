package Chapter6;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class FutureRenderer {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    void renderPage(CharSequence source) {
        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        Callable<List<ImageData>> task = () -> imageInfos.stream()
                .map(ImageInfo::downloadImage)
                .collect(Collectors.toList());

        Future<List<ImageData>> future = executor.submit(task);
        renderText(source);

        try {
            future.get().forEach(this::renderImage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
}

interface ImageInfo {
    ImageData downloadImage();
}

interface ImageData {
}
