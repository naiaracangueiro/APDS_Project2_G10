import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Branch and Bound algorithm for the Wall of Deadlines problem.
 * Uses a priority queue to explore promising configurations first,
 * pruning branches that cannot improve the current best solution.
 */
public class WODBnB {
    private static int bestSol;
    private static int[] bestConfig;

    /**
     * Main BnB loop: explores configurations ordered by their estimated upper bound.
     * Prunes branches where estimate <= current best solution.
     */
    private static void run(List<Quest> quests, int maxTime) {
        WODBnBGlobals.init(quests, maxTime);

        bestSol = Integer.MIN_VALUE;
        bestConfig = new int[quests.size()];
        Arrays.fill(bestConfig, -1);

        PriorityQueue<WODBnBConfig> queue = new PriorityQueue<>();
        WODBnBConfig first = new WODBnBConfig();
        queue.offer(first);

        while (!queue.isEmpty()) {
            WODBnBConfig config = queue.poll();

            // Bound: skip if this config cannot improve best solution
            if (config.estimate() <= bestSol) {
                continue;
            }

            // Branch: expand to child configurations
            List<WODBnBConfig> children = config.expand();

            for (WODBnBConfig child : children) {
                if (child.getQuestsValue() > bestSol) {
                    bestSol = child.getQuestsValue();
                    bestConfig = child.getConfig().clone();
                }
                if (child.estimate() > bestSol) {
                    queue.offer(child);
                }
            }
        }
    }

    /**
     * Runs the Branch and Bound algorithm with timing and displays results.
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
        printConfig(bestConfig, quests);
    }

    private static void printConfig(int[] config, List<Quest> quests) {
        for (int i = 0; i < config.length; i++) {
            if (config[i] == 1) {
                System.out.println("\t\t" + quests.get(i).toString());
            }
        }
    }
}
