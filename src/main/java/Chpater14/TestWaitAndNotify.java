package Chpater14;

public class TestWaitAndNotify {

    private volatile int counter;

    public static void main(String[] args) throws InterruptedException {
        TestWaitAndNotify dis = new TestWaitAndNotify();
        Thread thread1 = new Thread(() -> {
            try {
                dis.increaseCounter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                dis.increaseCounter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        System.out.println("Started");
        Thread.sleep(100);

        synchronized (dis) {
            System.out.println("Counter = " + dis.counter);
        }

        System.out.println("Time to notify");

        synchronized (dis) {
            dis.notifyAll();
        }

        thread1.join();
        thread2.join();

        System.out.println("Counter = " + dis.counter);
    }

    private synchronized void increaseCounter() throws InterruptedException {
        counter++;
        wait();
        counter++;
        System.out.println("I'm leaving");
    }
}
