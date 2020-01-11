package Chapter7;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BrokenPrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger one = BigInteger.ONE;
            while (!cancelled) {
                queue.put(one = one.nextProbablePrime());
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public static void consumePrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = new ArrayBlockingQueue<>(100);

        BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
        producer.start();

        try {
            while (needMorePrimes()) {
                consume(primes.take());
            }
        } finally {
            producer.cancel();
        }

    }

    private static boolean needMorePrimes() {
        return false;
    }

    private static void consume(BigInteger prime) {
        //
    }
}
