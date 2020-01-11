package Chapter11;

import net.jcip.annotations.ThreadSafe;

import java.util.stream.IntStream;

@ThreadSafe
public class StripedMap {
    // buckets[n] guarded by locks[n%N_LOCKS]
    private static final int N_LOCKS = 16;
    private final Node[] buckets;
    private final Object[] locks;

    public StripedMap(int numBuckets) {
        this.buckets = new Node[numBuckets];
        locks = IntStream.range(0, N_LOCKS).boxed().map(i -> new Object()).toArray();
    }

    private int hash(Object key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    public Object get(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            for (Node m = buckets[hash]; m != null; m = m.next()) {
                if (m.key().equals(key)) {
                    return m.value();
                }
            }

            return null;
        }
    }

    public void clear() {
        IntStream.range(0, buckets.length)
                .forEach(i -> {
                    synchronized (locks[i % N_LOCKS]) {
                        buckets[i] = null;
                    }
                });
    }

    private interface Node {
        Node next();

        Object key();

        Object value();
    }
}
