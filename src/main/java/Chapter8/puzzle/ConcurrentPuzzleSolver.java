package Chapter8.puzzle;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService executorService;
    private final ConcurrentMap<P, Boolean> visited;
    final ValueLatch<Node<P, M>> solution = new ValueLatch();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle, ExecutorService executorService, ConcurrentMap<P, Boolean> visited) {
        this.puzzle = puzzle;
        this.executorService = executorService;
        this.visited = visited;
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            executorService.execute(newTask(p, null, null));
            // block until the solution is found
            Node<P, M> solnNode = solution.getValue();
            return (solnNode == null) ? null : solnNode.asMoveList();
        } finally {
            executorService.shutdown();
        }
    }

    protected Runnable newTask(P p, M m, Node<P, M> prev) {
        return new SolverTask(p, m, prev);
    }

    class SolverTask extends Node<P, M> implements Runnable {

        public SolverTask(P pos, M move, Node<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            if (solution.isSet() || visited.putIfAbsent(pos, true) != null) {
                return;
            }

            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (M move : puzzle.legalMoves(pos)) {
                    executorService.execute(newTask(puzzle.move(pos, move), move, this));
                }
            }
        }
    }

    @ThreadSafe
    class ValueLatch<T> {
        @GuardedBy("this")
        private T value;
        private final CountDownLatch done = new CountDownLatch(1);

        public boolean isSet() {
            return done.getCount() == 0;
        }

        public synchronized void setValue(T value) {
            if (!isSet()) {
                this.value = value;
                done.countDown();
            }
        }

        public T getValue() throws InterruptedException {
            done.await();
            synchronized (this) {
                return value;
            }
        }
    }
}
