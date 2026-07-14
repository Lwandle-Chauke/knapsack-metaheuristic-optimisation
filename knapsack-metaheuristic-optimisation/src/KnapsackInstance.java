import java.io.*;
import java.util.*;

/**
 * Represents a single 0/1 Knapsack problem instance.
 * Each instance has n items (each with a value and weight) and a knapsack capacity W.
 * The goal is to select items to maximise total value without exceeding W.
 */
public class KnapsackInstance {
    private final String name;
    private final int numItems;
    private final double capacity;
    private final double[] values;
    private final double[] weights;

    public KnapsackInstance(String name, int numItems, double capacity, double[] values, double[] weights) {
        this.name = name;
        this.numItems = numItems;
        this.capacity = capacity;
        this.values = values;
        this.weights = weights;
    }

    /**
     * Parses a standard-format knapsack instance file.
     * Expected format:
     *   Line 1:       n  capacity
     *   Lines 2..n+1: value_i  weight_i
     */
    public static KnapsackInstance loadFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        String name = file.getName();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // First line contains number of items and knapsack capacity
            String firstLine = br.readLine().trim();
            String[] header = firstLine.split("\\s+");
            int n = Integer.parseInt(header[0]);
            double cap = Double.parseDouble(header[1]);

            double[] vals = new double[n];
            double[] wts = new double[n];

            // Each subsequent line contains one item's value and weight
            for (int i = 0; i < n; i++) {
                String line = br.readLine().trim();
                String[] parts = line.split("\\s+");
                vals[i] = Double.parseDouble(parts[0]);
                wts[i] = Double.parseDouble(parts[1]);
            }

            return new KnapsackInstance(name, n, cap, vals, wts);
        }
    }

    // --- Accessors ---
    public String getName()    { return name; }
    public int getNumItems()   { return numItems; }
    public double getCapacity() { return capacity; }
    public double getValue(int i)  { return values[i]; }
    public double getWeight(int i) { return weights[i]; }
    public double[] getValues()  { return values; }
    public double[] getWeights() { return weights; }
}
