package Chapter8.puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SequentialPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> visited = new HashSet<>();

    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        P initialPosition = puzzle.initialPosition();
        return search(new Node<P, M>(initialPosition, null, null));
    }

    private List<M> search(Node<P, M> node) {
        if (!visited.contains(node.pos)) {
            visited.add(node.pos);
            if (puzzle.isGoal(node.pos)) {
                return node.asMoveList();
            }

            for (M move : puzzle.legalMoves(node.pos)) {
                P p = puzzle.move(node.pos, move);
                List<M> search = search(new Node<>(p, move, node));
                if (search != null) {
                    return search;
                }
            }
        }

        return null;
    }
}
