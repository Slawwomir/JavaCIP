package Chapter6;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Cache {


    private interface Computable<A, V> {
        V compute(A arg) throws InterruptedException;
    }

    private static class ExpensiveFunction implements Computable<String, BigInteger> {
        @Override
        public BigInteger compute(String arg) throws InterruptedException {
            return new BigInteger(arg);
        }
    }

    private class Memoizer1<A, V> implements Computable<A, V> {

        private final Map<A, V> cache = new HashMap<>();
        private final Computable<A, V> c;

        private Memoizer1(Computable<A, V> c) {
            this.c = c;
        }

        @Override
        public synchronized V compute(A arg) throws InterruptedException {
            V result = cache.get(arg);
            if (result == null) {
                result = c.compute(arg);
                cache.put(arg, result);
            }

            return result;
        }
    }

    private class Memoizer2<A, V> implements Computable<A, V> {
        private final Map<A, V> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        private Memoizer2(Computable<A, V> c) {
            this.c = c;
        }

        @Override
        public V compute(A arg) throws InterruptedException {
            V result = cache.get(arg);
            if (result == null) {
                result = c.compute(arg);
                cache.put(arg, result);
            }

            return result;
        }
    }

    private class Memoizer3<A, V> implements Computable<A, V> {
        private final Map<A, FutureTask<V>> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        private Memoizer3(Computable<A, V> c) {
            this.c = c;
        }

        @Override
        public V compute(A arg) throws InterruptedException {
            Future<V> result = cache.get(arg);
            if (result == null) {
                final FutureTask<V> task = new FutureTask<>(() -> c.compute(arg));
                cache.put(arg, task);
                task.run();
            }

            try {
                return result.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class Memoizer<A, V> implements Computable<A, V> {
        private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        private Memoizer(Computable<A, V> c) {
            this.c = c;
        }

        @Override
        public V compute(A arg) throws InterruptedException {
            while (true) {
                Future<V> f = cache.get(arg);
                if (f == null) {
                    final FutureTask<V> futureTask = new FutureTask<>(() -> c.compute(arg));
                    f = cache.putIfAbsent(arg, futureTask);

                    if (f == null) {
                        f = futureTask;
                        futureTask.run();
                    }
                }

                try {
                    return f.get();
                } catch (CancellationException e) {
                    cache.remove(arg, f);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
