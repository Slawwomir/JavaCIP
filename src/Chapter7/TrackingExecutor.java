package Chapter7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;

public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService executorService;
    private final Set<Runnable> tasksCanceleedAtShutdown = Collections.synchronizedSet(new HashSet<>());

    /*
                    ...
     */

    public List<Runnable> getCancelledTasks() {
        if (!executorService.isTerminated()) {
            throw new IllegalStateException("...");
        }

        return new ArrayList<>(tasksCanceleedAtShutdown);
    }

    @Override
    public void execute(final Runnable runnable) {
        executorService.execute(
                () -> {
                    try {
                        runnable.run();
                    } finally {
                        if (isShutdown() && Thread.currentThread().isInterrupted()) {
                            tasksCanceleedAtShutdown.add(runnable);
                        }
                    }
                }
        );
    }

}
