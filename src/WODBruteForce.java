import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Brute Force algorithm for the Wall of Deadlines problem.
 * Explores all possible combinations of quests (2^N configurations).
 */
public class WODBruteForce {
    private static List<Quest> quests;

    private static int bestSol;
    private static int[] bestConfig;

    /**
     * Recursive brute force exploration. Each quest can be selected (1) or not (0).
     */
    private static void bruteForce(int[] config, int level, int maxTime) {
        // Binary choice: 0 = don't select quest, 1 = select quest
        for (int option = 0; option <= 1; option++) {
            config[level] = option;

            if (level < config.length - 1) {
                // Not at last level, continue exploring
                bruteForce(config, level + 1, maxTime);
            } else {
                // Complete configuration, evaluate it
                checkSolution(config, maxTime);
            }
        }
    }

    private static void run(List<Quest> loadedQuests, int maxTime) {
        quests = loadedQuests;

        // Initialize best solution tracking
        bestSol = -1;
        bestConfig = null;

        // config[i] = 1 if quest i is selected, 0 otherwise
        int[] config = new int[quests.size()];
        Arrays.fill(config, -1);

        bruteForce(config, 0, maxTime);
    }

    private static void checkSolution(int[] config, int maxTime) {
        // Create list of selected quests based on config
        List<Quest> selectedQuests = createList(config);

        // Evaluate returns -1 if constraints violated, otherwise total quest value
        int totalQuests = WODEvaluator.evaluate(selectedQuests, maxTime);

        // Update best if valid and better value (maximization problem)
        if (totalQuests != -1 && totalQuests > bestSol) {
            bestSol = totalQuests;
            bestConfig = config.clone();
        }
    }

    /**
     * Runs the brute force algorithm with timing and displays results.
     * @param quests the list of quests to evaluate
     * @param maxTime the maximum time limit in minutes
     * @param timer the timer for measuring execution time
     */
    public static void runWithTimer(List<Quest> quests, int maxTime, Timer timer) {
        timer.start();
        run(quests, maxTime);
        timer.stop();
        System.out.printf("\tExecution time: %.3f ms%n", timer.getElapsedMillis());
        System.out.println("\tBest solution: " + bestSol + " quests in " + maxTime + " minutes.");
        System.out.println("\tBest config: ");
        printConfig(bestConfig);
    }

    /** Converts binary config array to list of selected quests */
    private static List<Quest> createList(int[] config) {
        List<Quest> selected = new ArrayList<>();
        for (int i = 0; i < config.length; i++) {
            if (config[i] == 1) {
                selected.add(quests.get(i));
            }
        }
        return selected;
    }

    private static void printConfig(int[] config) {
        for (int i = 0; i < config.length; i++) {
            if (config[i] == 1) {
                System.out.println("\t\t" + quests.get(i).toString());
            }
        }
    }
}
