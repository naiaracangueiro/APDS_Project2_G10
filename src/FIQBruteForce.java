import java.util.Arrays;
import java.util.List;

/**
 * Brute Force algorithm for the Festival of Infinite Quests problem.
 * Explores all possible week assignments to find the optimal solution.
 */
public class FIQBruteForce {
    private static List<Quest> quests;
    private static int bestWeeks;
    private static int[] bestConfig;
    private static int maxWeeks;
    private static long nodesExplored;
    private static long configurationsGenerated;

    /**
     * Recursive brute force exploration of all configurations.
     * @param config current configuration array
     * @param level current decision level (quest index)
     */
    private static void bruteForce(int[] config, int level) {
        // Try assigning current quest to each possible week
        for (int week = 0; week < maxWeeks; week++) {
            config[level] = week;
            configurationsGenerated++;

            if (level < config.length - 1) {
                // Not at last level, continue exploring
                bruteForce(config, level + 1);
            } else {
                // Complete configuration reached, evaluate it
                nodesExplored++;
                checkSolution(config);
            }
        }
    }

    /**
     * Initializes and runs the brute force algorithm.
     * @param loadedQuests the list of quests to schedule
     */
    private static void run(List<Quest> loadedQuests) {
        // Preprocessing: sort by importance (Legendary first)
        quests = Utils.sortByImportanceDesc(loadedQuests);

        // Calculate upper bound for weeks needed
        maxWeeks = Utils.calculateMaxWeeks(quests);

        // Initialize best solution tracking
        bestWeeks = Integer.MAX_VALUE;
        bestConfig = null;
        nodesExplored = 0;
        configurationsGenerated = 0;

        // config[i] = week assigned to quest i
        int[] config = new int[quests.size()];
        Arrays.fill(config, -1);

        bruteForce(config, 0);
    }

    /**
     * Checks if a complete configuration is valid and updates best solution if improved.
     * @param config the configuration to check
     */
    private static void checkSolution(int[] config) {
        // Evaluate returns -1 if constraints violated, otherwise number of weeks
        int weeksUsed = FIQEvaluator.evaluate(quests, config);

        // Update best if valid and uses fewer weeks
        if (weeksUsed != -1 && weeksUsed < bestWeeks) {
            bestWeeks = weeksUsed;
            bestConfig = config.clone();
        }
    }

    /**
     * Runs the brute force algorithm with timing and displays results.
     * @param quests the list of quests to schedule
     * @param timer the timer for measuring execution time
     */
    public static void runWithTimer(List<Quest> quests, Timer timer) {
        timer.start();
        run(quests);
        timer.stop();

        System.out.printf("\tExecution time: %.3f ms%n", timer.getElapsedMillis());
        System.out.println("\tConfigurations explored: " + nodesExplored);

        if (bestConfig != null) {
            Utils.printWeeklySolution(FIQBruteForce.quests, bestConfig, bestWeeks);
        } else {
            System.out.println("\tNo valid solution found.");
        }
    }

    /** @return the number of complete configurations explored */
    public static long getNodesExplored() {
        return nodesExplored;
    }

    /** @return the total number of configurations generated */
    public static long getConfigurationsGenerated() {
        return configurationsGenerated;
    }

    /** @return the minimum weeks found in the best solution */
    public static int getBestWeeks() {
        return bestWeeks;
    }
}
