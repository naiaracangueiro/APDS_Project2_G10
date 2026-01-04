import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects performance metrics for all algorithms and exports to CSV.
 * Run this to generate data for the report analysis.
 *
 * Different algorithms are tested with different dataset sizes to avoid
 * impossibly long wait times for exponential algorithms.
 */
public class MetricsSaver {

    // Different sizes per algorithm type (to avoid impossible wait times)
    private static final int[] SIZES_BRUTEFORCE = {5, 6, 7, 8, 9, 10, 11, 12};
    private static final int[] SIZES_BACKTRACKING = {5, 10, 15, 20, 25};
    private static final int[] SIZES_FAST = {5, 10, 15, 20, 25, 30, 40, 50};

    // Number of runs per test (for averaging)
    private static final int RUNS = 3;

    private static List<PerformanceMetrics> allMetrics = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("METRICS COLLECTOR FOR REPORT");
        System.out.println("=".repeat(60));
        System.out.println("Brute Force sizes:   5-12 quests");
        System.out.println("Backtracking sizes:  5-25 quests");
        System.out.println("BnB/Greedy sizes:    5-50 quests");
        System.out.println("Runs per test: " + RUNS);
        System.out.println("=".repeat(60));

        // WOD Tests
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROBLEM 3.1: WALL OF DEADLINES (WOD)");
        System.out.println("=".repeat(60));
        testAllWOD();

        // FIQ Tests
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROBLEM 3.2: FESTIVAL OF INFINITE QUESTS (FIQ)");
        System.out.println("=".repeat(60));
        testAllFIQ();

        // Export all metrics
        System.out.println("\n" + "=".repeat(60));
        System.out.println("EXPORTING METRICS");
        System.out.println("=".repeat(60));

        PerformanceMetrics.exportToCSV("all_metrics.csv", allMetrics);

        // Also export separate files per problem
        List<PerformanceMetrics> wodMetrics = new ArrayList<>();
        List<PerformanceMetrics> fiqMetrics = new ArrayList<>();

        for (PerformanceMetrics m : allMetrics) {
            if (m.getProblemName().equals("WOD")) {
                wodMetrics.add(m);
            } else {
                fiqMetrics.add(m);
            }
        }

        PerformanceMetrics.exportToCSV("wod_metrics.csv", wodMetrics);
        PerformanceMetrics.exportToCSV("fiq_metrics.csv", fiqMetrics);

        System.out.println("\nTotal metrics collected: " + allMetrics.size());
        System.out.println("Files saved in: performance_measures/");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DONE!");
        System.out.println("=".repeat(60));
    }

    private static void testAllWOD() {
        // WOD Brute Force (small sizes only)
        System.out.println("\n--- WOD Brute Force ---");
        for (int size : SIZES_BRUTEFORCE) {
            testWODAlgorithm("BruteForce", size);
        }

        // WOD Backtracking (medium sizes)
        System.out.println("\n--- WOD Backtracking ---");
        for (int size : SIZES_BACKTRACKING) {
            testWODAlgorithm("Backtracking", size);
        }

        // WOD Branch and Bound (all sizes)
        System.out.println("\n--- WOD Branch and Bound ---");
        for (int size : SIZES_FAST) {
            testWODAlgorithm("BranchAndBound", size);
        }
    }

    private static void testAllFIQ() {
        // FIQ Brute Force (small sizes only)
        System.out.println("\n--- FIQ Brute Force ---");
        for (int size : SIZES_BRUTEFORCE) {
            if (size <= 10) { // FIQ Brute Force is even slower
                testFIQAlgorithm("BruteForce", size);
            }
        }

        // FIQ Backtracking (medium sizes)
        System.out.println("\n--- FIQ Backtracking ---");
        for (int size : SIZES_BACKTRACKING) {
            if (size <= 20) { // FIQ Backtracking limit
                testFIQAlgorithm("Backtracking", size);
            }
        }

        // FIQ Greedy - All heuristics (all sizes)
        System.out.println("\n--- FIQ Greedy First Fit ---");
        for (int size : SIZES_FAST) {
            testFIQAlgorithm("Greedy_FirstFit", size);
        }

        System.out.println("\n--- FIQ Greedy First Fit Decreasing ---");
        for (int size : SIZES_FAST) {
            testFIQAlgorithm("Greedy_FFD", size);
        }

        System.out.println("\n--- FIQ Greedy Priority-Based ---");
        for (int size : SIZES_FAST) {
            testFIQAlgorithm("Greedy_PriorityBased", size);
        }

        System.out.println("\n--- FIQ Greedy Best Fit ---");
        for (int size : SIZES_FAST) {
            testFIQAlgorithm("Greedy_BestFit", size);
        }
    }

    /**
     * Tests a WOD algorithm with the specified dataset size.
     * Runs multiple times and averages the execution time.
     */
    private static void testWODAlgorithm(String algorithm, int size) {
        try {
            // Load quests from the largest dataset (XXXL)
            List<Quest> quests = DSLoader.loadFile("../datasets/datasetXXXL.paed", size);
            int actualSize = quests.size();
            // Set time limit proportional to number of quests
            int maxTime = actualSize * 50;

            System.out.print("  Size " + actualSize + ": ");

            // Run multiple times and accumulate total time
            double totalTime = 0;
            for (int i = 0; i < RUNS; i++) {
                Timer timer = new Timer();
                switch (algorithm) {
                    case "BruteForce":
                        WODBruteForce.runWithTimer(quests, maxTime, timer);
                        break;
                    case "Backtracking":
                        WODBacktracking.runWithTimer(quests, maxTime, timer);
                        break;
                    case "BranchAndBound":
                        WODBnB.runWithTimer(quests, maxTime, timer);
                        break;
                }
                totalTime += timer.getElapsedMillis();
            }

            // Calculate and display average time
            double avgTime = totalTime / RUNS;
            System.out.printf("%.3f ms (avg)%n", avgTime);

            // Store metrics for CSV export
            PerformanceMetrics m = new PerformanceMetrics(algorithm, "WOD");
            m.record(actualSize, new MockTimer(avgTime), 0);
            allMetrics.add(m);

        } catch (IOException e) {
            System.out.println("  Size " + size + ": ERROR - " + e.getMessage());
        }
    }

    /**
     * Tests a FIQ algorithm with the specified dataset size.
     * Runs multiple times and averages the execution time.
     */
    private static void testFIQAlgorithm(String algorithm, int size) {
        try {
            // Load quests from the largest dataset (XXXL)
            List<Quest> quests = DSLoader.loadFile("../datasets/datasetXXXL.paed", size);
            int actualSize = quests.size();

            System.out.print("  Size " + actualSize + ": ");

            // Variables to accumulate across runs
            double totalTime = 0;
            int solution = 0;
            long nodesExplored = 0;

            // Run multiple times for averaging
            for (int i = 0; i < RUNS; i++) {
                Timer timer = new Timer();
                switch (algorithm) {
                    case "BruteForce":
                        FIQBruteForce.runWithTimer(quests, timer);
                        solution = FIQBruteForce.getBestWeeks();
                        nodesExplored = FIQBruteForce.getNodesExplored();
                        break;
                    case "Backtracking":
                        FIQBacktracking.runWithTimer(quests, timer);
                        solution = FIQBacktracking.getBestWeeks();
                        nodesExplored = FIQBacktracking.getNodesExplored();
                        break;
                    case "Greedy_FirstFit":
                        FIQGreedy.runFirstFit(quests, timer);
                        solution = FIQGreedy.getBestWeeks();
                        break;
                    case "Greedy_FFD":
                        FIQGreedy.runFirstFitDecreasing(quests, timer);
                        solution = FIQGreedy.getBestWeeks();
                        break;
                    case "Greedy_PriorityBased":
                        FIQGreedy.runPriorityBased(quests, timer);
                        solution = FIQGreedy.getBestWeeks();
                        break;
                    case "Greedy_BestFit":
                        FIQGreedy.runBestFit(quests, timer);
                        solution = FIQGreedy.getBestWeeks();
                        break;
                }
                totalTime += timer.getElapsedMillis();
            }

            // Calculate and display average time and solution quality
            double avgTime = totalTime / RUNS;
            System.out.printf("%.3f ms (avg), %d weeks%n", avgTime, solution);

            // Store metrics for CSV export
            PerformanceMetrics m = new PerformanceMetrics(algorithm, "FIQ");
            m.record(actualSize, new MockTimer(avgTime), solution);
            m.setNodesExplored(nodesExplored);
            allMetrics.add(m);

        } catch (IOException e) {
            System.out.println("  Size " + size + ": ERROR - " + e.getMessage());
        }
    }

    /**
     * Helper class to create a Timer with a preset elapsed time (for averaging).
     */
    private static class MockTimer extends Timer {
        private double elapsed;

        public MockTimer(double elapsedMs) {
            this.elapsed = elapsedMs;
        }

        @Override
        public double getElapsedMillis() {
            return elapsed;
        }
    }
}
