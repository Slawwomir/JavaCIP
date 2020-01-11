package Chapter6;

import java.util.concurrent.CountDownLatch;

public class TestHarness {
    public long timeTasks(int nThread, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThread);

        for (int i = 0; i < nThread; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    startGate.await();

                    try {
                        task.run();
                    } finally {
                        endGate.countDown();
                    }
                } catch (InterruptedException ignored) {
                    //ignored
                }
            });

            thread.start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await();

        long end = System.nanoTime();
        return end - start;
    }
}
