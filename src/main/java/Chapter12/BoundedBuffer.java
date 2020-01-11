package Chapter12;

import net.jcip.annotations.GuardedBy;

import java.util.concurrent.Semaphore;

public class BoundedBuffer<E> {
    private final Semaphore availableItems;
    private final Semaphore availableSpace;
    @GuardedBy("this")
    private final E[] items;
    @GuardedBy("this")
    private int putPosition = 0, takePosition = 0;

    public BoundedBuffer(int capacity) {
        availableItems = new Semaphore(0);
        availableSpace = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return availableItems.availablePermits() == 0;
    }

    public boolean isFull() {
        return availableSpace.availablePermits() == 0;
    }

    public void put(E e) throws InterruptedException {
        availableSpace.acquire();
        doInsert(e);
        availableItems.release();
    }

    public E take() throws InterruptedException {
        availableItems.acquire();
        E e = doExtract();
        availableSpace.release();
        return e;
    }

    private synchronized void doInsert(E e) {
        int i = putPosition;
        items[i] = e;
        putPosition = ++i % items.length;
    }

    private synchronized E doExtract() {
        int i = takePosition;
        E e = items[i];
        items[i] = null;
        takePosition = ++i % items.length;
        return e;
    }
}
