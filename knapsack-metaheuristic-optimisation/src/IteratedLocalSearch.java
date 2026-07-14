import java.util.Random;

public class IteratedLocalSearch {

    private final KnapsackInstance instance;
    private final Random rng;
    private final int maxIterations;        
    private final int perturbationStrength; 

    public IteratedLocalSearch(KnapsackInstance instance, Random rng, int maxIterations, int perturbationStrength) {
        this.instance = instance;
        this.rng = rng;
        this.maxIterations = maxIterations;
        this.perturbationStrength = perturbationStrength;
    }

    /**
     * Main ILS loop:
     *   - Start with a greedy initial solution and apply local search
     *   - For each iteration: perturb -> local search -> accept/reject
     *   - Track the overall best solution found across all iterations
     */
    public KnapsackSolution solve() {
        // Step 1: Generate and locally optimise an initial solution
        KnapsackSolution current = generateInitialSolution();
        current = localSearch(current);

        KnapsackSolution best = new KnapsackSolution(current);

        // Step 2: Iterate - perturb, optimise, and decide whether to move
        for (int iter = 0; iter < maxIterations; iter++) {
            KnapsackSolution perturbed = perturbation(current);
            KnapsackSolution candidate = localSearch(perturbed);

            // Accept or reject the candidate based on the acceptance criterion
            current = acceptanceCriterion(current, candidate, best);

            // Update the global best if the current solution is a new best
            if (current.isFeasible() && current.getTotalValue() > best.getTotalValue()) {
                best = new KnapsackSolution(current);
            }
        }

        return best;
    }

    /**
     * Generates an initial feasible solution using a greedy randomised heuristic.
     * Items are added in order of decreasing value/weight ratio, with random
     * tie-breaking to allow diverse starting points across different seeds.
     */
    private KnapsackSolution generateInitialSolution() {
        return KnapsackSolution.generateGreedyRandom(instance, rng);
    }

    /**
     * Local search combining two neighbourhood structures:
     *
     * Phase 1 - 1-flip: Try toggling each item's inclusion. Accept the first
     *   move that improves the objective while remaining feasible.
     *
     * Phase 2 - Swap (1-1 exchange): If no 1-flip improves, try removing one
     *   selected item and adding one unselected item. This allows the search
     *   to navigate between solutions of similar weight but higher value.
     *
     * Terminates when neither neighbourhood finds an improvement (local optimum).
     */
    private KnapsackSolution localSearch(KnapsackSolution solution) {
        KnapsackSolution current = new KnapsackSolution(solution);
        boolean improved = true;

        while (improved) {
            improved = false;
            int n = current.getNumItems();

            // Phase 1: 1-flip neighbourhood - try adding or removing a single item
            int[] order = getShuffledIndices(n);
            for (int idx : order) {
                double oldValue = current.getTotalValue();
                current.flipItem(idx);

                if (current.isFeasible() && current.getTotalValue() > oldValue) {
                    improved = true;
                    break;  
                } else {
                    current.flipItem(idx); 
                }
            }

            if (improved) continue;

            // Phase 2: Swap neighbourhood - exchange one in-item for one out-item
            int[] shuffled = getShuffledIndices(n);
            for (int si = 0; si < n && !improved; si++) {
                int i = shuffled[si];
                if (current.getItem(i) != 1) continue;  
                for (int sj = 0; sj < n && !improved; sj++) {
                    int j = shuffled[sj];
                    if (i == j || current.getItem(j) != 0) continue;  

                    double oldValue = current.getTotalValue();
                    current.flipItem(i);  
                    current.flipItem(j);  

                    if (current.isFeasible() && current.getTotalValue() > oldValue) {
                        improved = true;
                    } else {
                        current.flipItem(j);  
                        current.flipItem(i);  
                    }
                }
            }
        }

        // After reaching a local optimum, greedily fill any remaining capacity
        fillSolution(current);

        return current;
    }

    
    private void fillSolution(KnapsackSolution solution) {
        int n = solution.getNumItems();
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        java.util.Arrays.sort(indices, (a, b) -> {
            double ratioA = safeRatio(instance.getValue(a), instance.getWeight(a));
            double ratioB = safeRatio(instance.getValue(b), instance.getWeight(b));
            return Double.compare(ratioB, ratioA);
        });

        for (int idx : indices) {
            if (solution.getItem(idx) == 0) {
                if (solution.getTotalWeight() + instance.getWeight(idx) <= instance.getCapacity()) {
                    solution.setItem(idx, 1);
                }
            }
        }
    }

    /**
     * Perturbation: kicks the current solution out of its local optimum basin
     * by randomly flipping 'perturbationStrength' bits. This creates a new
     * starting point for local search that is structurally different from
     * the current solution but still related to it (unlike a full restart).
     */
    private KnapsackSolution perturbation(KnapsackSolution solution) {
        KnapsackSolution perturbed = new KnapsackSolution(solution);
        int n = perturbed.getNumItems();

        int flips = Math.min(perturbationStrength, n);
        int[] indices = getShuffledIndices(n);

        // Flip 'flips' randomly chosen bits
        for (int i = 0; i < flips; i++) {
            perturbed.flipItem(indices[i]);
        }

        // Repair if the perturbation made the solution infeasible
        repair(perturbed);

        return perturbed;
    }

    /**
     * Repair operator: if the solution exceeds capacity, remove items one at a
     * time starting with the least efficient (lowest value/weight ratio).
     * This minimises the value lost while restoring feasibility.
     */
    private void repair(KnapsackSolution solution) {
        if (solution.isFeasible()) return;

        int n = solution.getNumItems();
        Integer[] selected = new Integer[n];
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (solution.getItem(i) == 1) {
                selected[count++] = i;
            }
        }

        java.util.Arrays.sort(selected, 0, count, (a, b) -> {
            double ratioA = safeRatio(instance.getValue(a), instance.getWeight(a));
            double ratioB = safeRatio(instance.getValue(b), instance.getWeight(b));
            return Double.compare(ratioA, ratioB);
        });

        // Remove items until the solution becomes feasible again
        for (int i = 0; i < count && !solution.isFeasible(); i++) {
            solution.setItem(selected[i], 0);
        }
    }

    /**
     * Acceptance criterion: always accept improving or equal solutions.
     * For worsening solutions, accept with probability exp(-delta / (0.05 * bestVal)).
     * This SA-like mechanism allows occasional uphill moves to escape local optima,
     * with acceptance probability decreasing as the quality gap grows.
     */
    private KnapsackSolution acceptanceCriterion(KnapsackSolution current, KnapsackSolution candidate, KnapsackSolution best) {
        if (!candidate.isFeasible()) return current;

        // Always accept if the candidate is at least as good
        if (candidate.getTotalValue() >= current.getTotalValue()) {
            return candidate;
        }

        // Probabilistic acceptance of worse solutions to maintain diversity
        double delta = current.getTotalValue() - candidate.getTotalValue();
        double bestVal = best.getTotalValue();
        double threshold = bestVal > 0 ? Math.exp(-delta / (0.05 * bestVal)) : 0.1;

        if (rng.nextDouble() < threshold) {
            return candidate;
        }

        return current;
    }

    private static double safeRatio(double v, double w) {
        return w == 0 ? Double.MAX_VALUE : v / w;
    }

    /**
     * Creates a randomly shuffled array of indices [0..n-1]
     * using the Fisher-Yates algorithm. Used to randomise the order
     * in which items are considered during local search and perturbation.
     */
    private int[] getShuffledIndices(int n) {
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = indices[i];
            indices[i] = indices[j];
            indices[j] = tmp;
        }
        return indices;
    }
}
