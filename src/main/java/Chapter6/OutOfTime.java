package Chapter6;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OutOfTime {
    public static void main(String[] args) throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        }, 1);

        SECONDS.sleep(1);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        }, 1);
        SECONDS.sleep(5);
    }
}
