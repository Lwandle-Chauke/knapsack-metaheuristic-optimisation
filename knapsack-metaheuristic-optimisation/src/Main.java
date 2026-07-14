import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class Main {

    private static final String[] INSTANCE_FILES = {
        "f1_l-d_kp_10_269",
        "f2_l-d_kp_20_878",
        "f3_l-d_kp_4_20",
        "f4_l-d_kp_4_11",
        "f5_l-d_kp_15_375",
        "f6_l-d_kp_10_60",
        "f7_l-d_kp_7_50",
        "f8_l-d_kp_23_10000",
        "f9_l-d_kp_5_80",
        "f10_l-d_kp_20_879"
    };

    private static final double[] KNOWN_OPTIMA = {
        295, 1024, 35, 23, 481.0694, 52, 107, 9767, 130, 1025
    };

    private static final int GA_MAX_GENERATIONS = 1000;
    private static final int ILS_MAX_ITERATIONS = 5000;
    private static final int ILS_PERTURBATION_STRENGTH = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter seed value: ");
        long seed = scanner.nextLong();
        scanner.close();

        String dataDir = resolveDataDir();

        System.out.println();
        System.out.println("========================================================================================================");
        System.out.println("       Comparison of GA and Iterated Local Search on 10 Knapsack Problem Instances");
        System.out.println("========================================================================================================");
        System.out.printf("%-28s %-12s %-12s %-18s %-18s %-18s%n",
                "Problem Instance", "Algorithm", "Seed Value", "Best Solution", "Known Optimum", "Runtime (seconds)");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < INSTANCE_FILES.length; i++) {
            String fileName = INSTANCE_FILES[i];
            double knownOptimum = KNOWN_OPTIMA[i];

            try {
                String filePath = dataDir + File.separator + fileName;
                KnapsackInstance instance = KnapsackInstance.loadFromFile(filePath);

                // --- Run ILS ---
                Random ilsRng = new Random(seed);
                int pertStrength = Math.max(1, Math.min(ILS_PERTURBATION_STRENGTH, instance.getNumItems() / 2));

                long ilsStart = System.nanoTime();
                IteratedLocalSearch ils = new IteratedLocalSearch(instance, ilsRng, ILS_MAX_ITERATIONS, pertStrength);
                KnapsackSolution ilsBest = ils.solve();
                double ilsRuntime = (System.nanoTime() - ilsStart) / 1_000_000_000.0;

                // --- Run GA ---
                Random gaRng = new Random(seed);

                long gaStart = System.nanoTime();
                GeneticAlgorithm ga = new GeneticAlgorithm(instance, gaRng, GA_MAX_GENERATIONS);
                KnapsackSolution gaBest = ga.solve();
                double gaRuntime = (System.nanoTime() - gaStart) / 1_000_000_000.0;

                // --- Format values ---
                String ilsBestStr, gaBestStr, optStr;
                if (knownOptimum == Math.floor(knownOptimum)) {
                    ilsBestStr = String.valueOf((int) ilsBest.getTotalValue());
                    gaBestStr  = String.valueOf((int) gaBest.getTotalValue());
                    optStr     = String.valueOf((int) knownOptimum);
                } else {
                    ilsBestStr = String.format("%.4f", ilsBest.getTotalValue());
                    gaBestStr  = String.format("%.4f", gaBest.getTotalValue());
                    optStr     = String.format("%.4f", knownOptimum);
                }

                // ILS row (with instance name)
                System.out.printf("%-28s %-12s %-12d %-18s %-18s %-18.6f%n",
                        fileName, "ILS", seed, ilsBestStr, optStr, ilsRuntime);
                // GA row (instance name blank for grouping)
                System.out.printf("%-28s %-12s %-12d %-18s %-18s %-18.6f%n",
                        "", "GA", seed, gaBestStr, optStr, gaRuntime);

                if (i < INSTANCE_FILES.length - 1) {
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                }

            } catch (IOException e) {
                System.err.println("Error loading instance " + fileName + ": " + e.getMessage());
            }
        }

        System.out.println("========================================================================================================");
    }

    private static String resolveDataDir() {
        File cwd = new File("data");
        if (cwd.isDirectory()) return cwd.getAbsolutePath();

        try {
            File classLocation = new File(
                Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File beside = new File(classLocation.isDirectory() ? classLocation : classLocation.getParentFile(), "data");
            if (beside.isDirectory()) return beside.getAbsolutePath();
        } catch (URISyntaxException ignored) { }

        return "data";
    }
}
