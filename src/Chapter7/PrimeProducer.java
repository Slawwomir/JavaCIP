package Chapter7;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;

    PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger one = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(one = one.nextProbablePrime());
            }
        } catch (InterruptedException ignored) {
            /* Allow thread to exit */
        }
    }

    public void cancel() {
        interrupt();
    }

    public static void consumePrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = new ArrayBlockingQueue<>(100);

        PrimeProducer producer = new PrimeProducer(primes);
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
