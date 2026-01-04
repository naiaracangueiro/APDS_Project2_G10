import java.util.Arrays;
import java.util.List;

/**
 * Backtracking algorithm for the Festival of Infinite Quests problem.
 * Uses marking technique and PBCBS (Pruning Based on Current Best Solution) for optimization.
 */
public class FIQBacktracking {
    private static List<Quest> quests;
    private static int bestWeeks;
    private static int[] bestConfig;
    private static int maxWeeks;
    private static long nodesExplored;
    private static long nodesPruned;

    /**
     * Recursive backtracking with marking and pruning.
     * @param config current configuration array
     * @param level current decision level (quest index)
     * @param timePerWeek marking array for accumulated time per week
     * @param commonPerWeek marking array for common quest count per week
     */
    private static void backtracking(int[] config, int level, int[] timePerWeek, int[] commonPerWeek) {
        Quest currentQuest = quests.get(level);
        int questTime = currentQuest.getEstimatedTime();
        boolean isCommon = Utils.isCommon(currentQuest);

        for (int week = 0; week < maxWeeks; week++) {
            // Constraint pruning
            if (timePerWeek[week] + questTime > FIQEvaluator.WEEKLY_MAX_MINUTES) {
                nodesPruned++;
                continue;
            }
            if (isCommon && commonPerWeek[week] >= FIQEvaluator.MAX_COMMON_PER_WEEK) {
                nodesPruned++;
                continue;
            }

            // Apply marking
            config[level] = week;
            timePerWeek[week] += questTime;
            if (isCommon) {
                commonPerWeek[week]++;
            }

            // PBCBS: prune if current weeks used cannot improve best solution
            int currentWeeksUsed = getMaxAssignedWeek(config, level) + 1;

            if (currentWeeksUsed < bestWeeks) {
                if (level < config.length - 1) {
                    backtracking(config, level + 1, timePerWeek, commonPerWeek);
                } else {
                    nodesExplored++;
                    if (currentWeeksUsed < bestWeeks) {
                        bestWeeks = currentWeeksUsed;
                        bestConfig = config.clone();
                    }
                }
            } else {
                nodesPruned++;
            }

            // Undo marking
            timePerWeek[week] -= questTime;
            if (isCommon) {
                commonPerWeek[week]--;
            }
        }
    }

    /**
     * Returns the highest week index assigned up to a given level.
     * @param config the configuration array
     * @param upToLevel the level up to which to check
     * @return the maximum week index assigned
     */
    private static int getMaxAssignedWeek(int[] config, int upToLevel) {
        int max = 0;
        for (int i = 0; i <= upToLevel; i++) {
            if (config[i] > max) {
                max = config[i];
            }
        }
        return max;
    }

    /**
     * Initializes and runs the backtracking algorithm.
     * @param loadedQuests the list of quests to schedule
     */
    private static void run(List<Quest> loadedQuests) {
        // Sort by importance (Legendary first) for priority preprocessing
        quests = Utils.sortByImportanceDesc(loadedQuests);
        maxWeeks = Utils.calculateMaxWeeks(quests);
        bestWeeks = Integer.MAX_VALUE;
        bestConfig = null;
        nodesExplored = 0;
        nodesPruned = 0;

        int[] config = new int[quests.size()];
        Arrays.fill(config, -1);

        // Initialize marking arrays
        int[] timePerWeek = new int[maxWeeks];
        int[] commonPerWeek = new int[maxWeeks];

        backtracking(config, 0, timePerWeek, commonPerWeek);
    }

    /**
     * Runs the backtracking algorithm with timing and displays results.
     * @param quests the list of quests to schedule
     * @param timer the timer for measuring execution time
     */
    public static void runWithTimer(List<Quest> quests, Timer timer) {
        timer.start();
        run(quests);
        timer.stop();

        System.out.printf("\tExecution time: %.3f ms%n", timer.getElapsedMillis());
        System.out.println("\tConfigurations explored: " + nodesExplored);
        System.out.println("\tNodes pruned: " + nodesPruned);

        if (bestConfig != null) {
            Utils.printWeeklySolution(FIQBacktracking.quests, bestConfig, bestWeeks);
        } else {
            System.out.println("\tNo valid solution found.");
        }
    }

    /** @return the number of complete configurations explored */
    public static long getNodesExplored() {
        return nodesExplored;
    }

    /** @return the number of nodes pruned during search */
    public static long getNodesPruned() {
        return nodesPruned;
    }

    /** @return the minimum weeks found in the best solution */
    public static int getBestWeeks() {
        return bestWeeks;
    }
}
