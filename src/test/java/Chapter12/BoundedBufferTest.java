package Chapter12;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundedBufferTest {

    private static final long LOCKUP_DETECT_TIMEOUT = 5000;
    @Test
    public void testIsEmptyWhenConstructed() {
        BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<Integer>(10);
        assertTrue(boundedBuffer.isEmpty());
    }

    @Test
    public void testIsFullAfterPuts() throws InterruptedException {
        BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<Integer>(10);
        for (int i = 0; i < 10; i++) {
            boundedBuffer.put(i);
        }
        assertTrue(boundedBuffer.isFull());
    }

    @Test
    public void testTakeBlocksWhenEmpty() {
        final BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                int unused = bb.take();
                fail();
            } catch (InterruptedException ignored) {
            }
        });

        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            taker.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());
        } catch (InterruptedException e) {
            fail();
        }
    }
}