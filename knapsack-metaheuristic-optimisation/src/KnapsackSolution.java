import java.util.Random;

/**
 * Represents a binary solution to the 0/1 Knapsack problem.
 * Each item is either included (1) or excluded (0).
 * Maintains running totals of value and weight for O(1) feasibility checks.
 */
public class KnapsackSolution {
    private final int[] selection;      
    private double totalValue;          
    private double totalWeight;         
    private final KnapsackInstance instance;

    // Creates an empty solution (no items selected)
    public KnapsackSolution(KnapsackInstance instance) {
        this.instance = instance;
        this.selection = new int[instance.getNumItems()];
        this.totalValue = 0;
        this.totalWeight = 0;
    }

    // Deep copy constructor — used to preserve best-known solutions
    public KnapsackSolution(KnapsackSolution other) {
        this.instance = other.instance;
        this.selection = other.selection.clone();
        this.totalValue = other.totalValue;
        this.totalWeight = other.totalWeight;
    }

    /**
     * Sets item at 'index' to included (1) or excluded (0).
     * Incrementally updates running totals to avoid recalculating from scratch.
     */
    public void setItem(int index, int value) {
        if (selection[index] == value) return;

        if (value == 1 && selection[index] == 0) {
            totalValue += instance.getValue(index);
            totalWeight += instance.getWeight(index);
        } else if (value == 0 && selection[index] == 1) {
            totalValue -= instance.getValue(index);
            totalWeight -= instance.getWeight(index);
        }
        selection[index] = value;
    }

    // Toggles an item between selected and unselected
    public void flipItem(int index) {
        setItem(index, 1 - selection[index]);
    }

    public int getItem(int index)      { return selection[index]; }
    public double getTotalValue()      { return totalValue; }
    public double getTotalWeight()     { return totalWeight; }
    public KnapsackInstance getInstance() { return instance; }
    public int getNumItems()           { return instance.getNumItems(); }

    // A solution is feasible if total weight does not exceed the knapsack capacity
    public boolean isFeasible() {
        return totalWeight <= instance.getCapacity();
    }

    /**
     * Constructs an initial solution using a greedy heuristic.
     * Items are sorted by value/weight ratio (best items first).
     * Random tie-breaking ensures different starting solutions across runs.
     * Items are added greedily as long as they fit within the capacity.
     */
    public static KnapsackSolution generateGreedyRandom(KnapsackInstance instance, Random rng) {
        KnapsackSolution sol = new KnapsackSolution(instance);
        int n = instance.getNumItems();

        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        java.util.Arrays.sort(indices, (a, b) -> {
            double ratioA = safeRatio(instance.getValue(a), instance.getWeight(a));
            double ratioB = safeRatio(instance.getValue(b), instance.getWeight(b));
            int cmp = Double.compare(ratioB, ratioA);
            return cmp != 0 ? cmp : (rng.nextBoolean() ? 1 : -1);
        });

        // Greedily add items in sorted order if they fit
        for (int idx : indices) {
            if (sol.getTotalWeight() + instance.getWeight(idx) <= instance.getCapacity()) {
                sol.setItem(idx, 1);
            }
        }

        return sol;
    }

    /**
     * Constructs a random solution by shuffling items and including each
     * with 50% probability, provided the capacity constraint is not violated.
     */
    public static KnapsackSolution generateRandom(KnapsackInstance instance, Random rng) {
        KnapsackSolution sol = new KnapsackSolution(instance);
        int n = instance.getNumItems();

        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        // Fisher-Yates shuffle for unbiased random ordering
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = indices[i];
            indices[i] = indices[j];
            indices[j] = tmp;
        }

        for (int idx : indices) {
            if (rng.nextDouble() < 0.5) {
                if (sol.getTotalWeight() + instance.getWeight(idx) <= instance.getCapacity()) {
                    sol.setItem(idx, 1);
                }
            }
        }

        return sol;
    }

    private static double safeRatio(double v, double w) {
        return w == 0 ? Double.MAX_VALUE : v / w;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < selection.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(selection[i]);
        }
        sb.append("] Value=").append(String.format("%.4f", totalValue))
          .append(" Weight=").append(String.format("%.4f", totalWeight));
        return sb.toString();
    }
}
