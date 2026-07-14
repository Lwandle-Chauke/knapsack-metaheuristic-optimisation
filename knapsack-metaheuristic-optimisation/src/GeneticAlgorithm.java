
import java.util.Arrays;
import java.util.Random;

/**
 * Genetic Algorithm for the 0/1 Knapsack Problem.
 *
 * Encoding : binary chromosome — selection[i] = 1 means item i is taken.
 * Selection : binary tournament (draws 2 random individuals, returns the
 * fitter). Crossover : uniform crossover — each gene independently inherited
 * from either parent with equal probability (p = 0.5 per gene). Mutation :
 * bit-flip — each gene flipped with probability 1/n, giving an expected total
 * of one flip per chromosome. Repair : infeasible offspring are fixed by
 * removing items in ascending order of value/weight ratio (least efficient
 * items first). Fill : after repair, any remaining capacity is filled greedily
 * (descending v/w ratio) to squeeze out extra value. Elitism : the top
 * ELITISM_COUNT individuals from the current generation are copied unchanged
 * into the next generation.
 */
public class GeneticAlgorithm {

    // ── Hyper-parameters ──────────────────────────────────────────────────────
    private static final int POPULATION_SIZE = 100;
    private static final double CROSSOVER_RATE = 0.85;  // prob. of applying crossover
    private static final int TOURNAMENT_SIZE = 2;     // binary tournament
    private static final int ELITISM_COUNT = 2;     // elites copied unchanged

    // ── State ─────────────────────────────────────────────────────────────────
    private final KnapsackInstance instance;
    private final Random rng;
    private final int maxGenerations;

    public GeneticAlgorithm(KnapsackInstance instance, Random rng, int maxGenerations) {
        this.instance = instance;
        this.rng = rng;
        this.maxGenerations = maxGenerations;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Public entry point
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Runs the GA and returns the best feasible solution found.
     *
     * Algorithm outline: 1. Initialise a random population and evaluate each
     * individual. 2. For each generation: a. Copy the ELITISM_COUNT best
     * individuals unchanged. b. Fill the rest via tournament selection →
     * crossover → mutation → repair/fill. 3. Track the global best across all
     * generations.
     */
    public KnapsackSolution solve() {
        int n = instance.getNumItems();

        // ── 1. Initialise population ──────────────────────────────────────────
        KnapsackSolution[] population = new KnapsackSolution[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = randomSolution();
            repairAndFill(population[i]);
        }

        KnapsackSolution best = copyBest(population);

        // ── 2. Generational loop ──────────────────────────────────────────────
        for (int gen = 0; gen < maxGenerations; gen++) {

            // Sort population descending by fitness for elitism
            Arrays.sort(population, (a, b)
                    -> Double.compare(b.getTotalValue(), a.getTotalValue()));

            KnapsackSolution[] next = new KnapsackSolution[POPULATION_SIZE];

            // (a) Elitism: carry best individuals forward unchanged
            for (int i = 0; i < ELITISM_COUNT; i++) {
                next[i] = new KnapsackSolution(population[i]);
            }

            // (b) Fill remaining slots with offspring
            for (int i = ELITISM_COUNT; i < POPULATION_SIZE; i += 2) {
                KnapsackSolution parent1 = tournamentSelect(population);
                KnapsackSolution parent2 = tournamentSelect(population);

                KnapsackSolution child1, child2;
                if (rng.nextDouble() < CROSSOVER_RATE) {
                    KnapsackSolution[] children = uniformCrossover(parent1, parent2);
                    child1 = children[0];
                    child2 = children[1];
                } else {
                    // No crossover — clone parents directly
                    child1 = new KnapsackSolution(parent1);
                    child2 = new KnapsackSolution(parent2);
                }

                bitFlipMutate(child1);
                bitFlipMutate(child2);

                repairAndFill(child1);
                repairAndFill(child2);

                next[i] = child1;
                if (i + 1 < POPULATION_SIZE) {
                    next[i + 1] = child2;
                }
            }

            population = next;

            // Track global best
            KnapsackSolution genBest = copyBest(population);
            if (genBest.getTotalValue() > best.getTotalValue()) {
                best = genBest;
            }
        }

        return best;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Initialisation
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Creates a random binary chromosome by including each item with 50 %
     * probability, subject to the capacity constraint. Items are shuffled first
     * to avoid systematic bias toward low-index items.
     */
    private KnapsackSolution randomSolution() {
        return KnapsackSolution.generateRandom(instance, rng);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Selection
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Binary tournament selection: samples TOURNAMENT_SIZE individuals at
     * random (with replacement) and returns a copy of the fittest one. Simple,
     * low selection pressure, preserves diversity.
     */
    private KnapsackSolution tournamentSelect(KnapsackSolution[] population) {
        KnapsackSolution best = population[rng.nextInt(POPULATION_SIZE)];
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            KnapsackSolution candidate = population[rng.nextInt(POPULATION_SIZE)];
            if (candidate.getTotalValue() > best.getTotalValue()) {
                best = candidate;
            }
        }
        return new KnapsackSolution(best);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Crossover
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Uniform crossover: for each gene position, the offspring independently
     * inherits from parent1 or parent2 with equal probability. Two
     * complementary offspring are produced.
     */
    private KnapsackSolution[] uniformCrossover(KnapsackSolution p1, KnapsackSolution p2) {
        int n = instance.getNumItems();
        KnapsackSolution child1 = new KnapsackSolution(instance);
        KnapsackSolution child2 = new KnapsackSolution(instance);

        for (int i = 0; i < n; i++) {
            if (rng.nextBoolean()) {
                child1.setItem(i, p1.getItem(i));
                child2.setItem(i, p2.getItem(i));
            } else {
                child1.setItem(i, p2.getItem(i));
                child2.setItem(i, p1.getItem(i));
            }
        }
        return new KnapsackSolution[]{child1, child2};
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Mutation
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Bit-flip mutation: each gene is flipped with probability 1/n. This gives
     * an expected one flip per chromosome — enough to introduce diversity
     * without destroying the structure built by crossover.
     */
    private void bitFlipMutate(KnapsackSolution solution) {
        int n = instance.getNumItems();
        double mutationRate = 1.0 / n;
        for (int i = 0; i < n; i++) {
            if (rng.nextDouble() < mutationRate) {
                solution.flipItem(i);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Repair + Fill
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Two-step feasibility handler applied after crossover and mutation:
     *
     * Repair: if the solution exceeds capacity, remove items one at a time in
     * ascending order of value/weight ratio (least efficient first) until the
     * weight constraint is satisfied.
     *
     * Fill: once feasible, greedily insert unselected items in descending v/w
     * ratio order as long as they fit. This recovers value that may have been
     * lost during repair and improves solution quality.
     */
    private void repairAndFill(KnapsackSolution solution) {
        // ── Repair phase ──────────────────────────────────────────────────────
        if (!solution.isFeasible()) {
            int n = instance.getNumItems();

            // Collect selected items and sort by v/w ratio ascending
            Integer[] selected = new Integer[n];
            int count = 0;
            for (int i = 0; i < n; i++) {
                if (solution.getItem(i) == 1) {
                    selected[count++] = i;
                }
            }
            Arrays.sort(selected, 0, count, (a, b) -> Double.compare(
                    safeRatio(instance.getValue(a), instance.getWeight(a)),
                    safeRatio(instance.getValue(b), instance.getWeight(b))
            ));

            for (int i = 0; i < count && !solution.isFeasible(); i++) {
                solution.setItem(selected[i], 0);
            }
        }

        // ── Fill phase ────────────────────────────────────────────────────────
        int n = instance.getNumItems();
        Integer[] unselected = new Integer[n];
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (solution.getItem(i) == 0) {
                unselected[count++] = i;
            }
        }
        Arrays.sort(unselected, 0, count, (a, b) -> Double.compare(
                safeRatio(instance.getValue(b), instance.getWeight(b)),
                safeRatio(instance.getValue(a), instance.getWeight(a))
        )); // descending — best items first

        for (int i = 0; i < count; i++) {
            int idx = unselected[i];
            if (solution.getTotalWeight() + instance.getWeight(idx) <= instance.getCapacity()) {
                solution.setItem(idx, 1);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * Returns value/weight ratio, guarding against zero-weight items.
     */
    private static double safeRatio(double v, double w) {
        return w == 0 ? Double.MAX_VALUE : v / w;
    }

    /**
     * Finds and returns a deep copy of the best feasible individual.
     */
    private KnapsackSolution copyBest(KnapsackSolution[] population) {
        KnapsackSolution best = null;
        for (KnapsackSolution s : population) {
            if (s.isFeasible() && (best == null || s.getTotalValue() > best.getTotalValue())) {
                best = s;
            }
        }
        // Fallback: if somehow no feasible solution exists, return first individual
        if (best == null) {
            best = population[0];
        }
        return new KnapsackSolution(best);
    }
}
