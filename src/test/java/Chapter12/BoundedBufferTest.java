package Chapter12;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundedBufferTest {

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
}