package Chapter6;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CellularAutomata {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final Worker[] workers;

    public CellularAutomata(Board mainBoard) {
        this.mainBoard = mainBoard;
        final int processors = Runtime.getRuntime().availableProcessors();
        this.barrier = new CyclicBarrier(processors, mainBoard::commitNewValues);
        this.workers = IntStream.range(0, processors)
                .mapToObj(i -> new Worker(mainBoard.getSubBoard(i)))
                .collect(Collectors.toList())
                .toArray(Worker[]::new);
    }


    private interface Board {
        void commitNewValues();

        boolean hasConverged();

        void setNewValue();

        Board getSubBoard(int i);

        void waitForConvergence();
    }

    private class Worker implements Runnable {
        private final Board board;

        private Worker(Board board) {
            this.board = board;
        }

        @Override
        public void run() {
            while (!board.hasConverged()) {
                board.setNewValue();
            }

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                return;
            }
        }
    }

    public void start() {
        for (Worker worker : workers) {
            new Thread(worker).start();
        }

        mainBoard.waitForConvergence();
    }
}
