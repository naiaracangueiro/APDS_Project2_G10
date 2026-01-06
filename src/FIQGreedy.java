import java.util.ArrayList;
import java.util.List;

/**
 * Greedy algorithms for the Festival of Infinite Quests problem.
 * Implements four different heuristics for quest scheduling.
 */
public class FIQGreedy {
    private static List<Quest> quests;
    private static int[] bestConfig;
    private static int bestWeeks;

    /**
     * First Fit heuristic: assigns each quest to the first week with available capacity.
     * @param loadedQuests the list of quests to schedule
     * @param timer the timer for measuring execution time
     */
    public static void runFirstFit(List<Quest> loadedQuests, Timer timer) {
        timer.start();
        // Use original order (no sorting)
        quests = new ArrayList<>(loadedQuests);
        // Worst case: each quest in its own week
        int maxWeeks = quests.size();

        int[] config = new int[quests.size()];
        int[] timePerWeek = new int[maxWeeks];
        int[] commonPerWeek = new int[maxWeeks];

        // Process each quest in order
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            boolean assigned = false;

            // Try to fit in the first available week
            for (int week = 0; week < maxWeeks && !assigned; week++) {
                if (FIQEvaluator.canAddToWeek(q, week, timePerWeek, commonPerWeek)) {
                    config[i] = week;
                    // Update marking for the week
                    timePerWeek[week] += q.getEstimatedTime();
                    if (Utils.isCommon(q)) {
                        commonPerWeek[week]++;
                    }
                    assigned = true;
                }
            }
        }

        bestConfig = config;
        bestWeeks = Utils.getWeeksUsed(config);
        timer.stop();

        printResults(timer, "First Fit");
    }

    /**
     * First Fit Decreasing heuristic: sorts quests by time (descending), then applies First Fit.
     * Placing larger quests first often leads to better packing.
     * @param loadedQuests the list of quests to schedule
     * @param timer the timer for measuring execution time
     */
    public static void runFirstFitDecreasing(List<Quest> loadedQuests, Timer timer) {
        timer.start();
        // Sort by estimated time descending (largest quests first)
        quests = Utils.sortByTimeDesc(loadedQuests);
        int maxWeeks = quests.size();

        int[] config = new int[quests.size()];
        int[] timePerWeek = new int[maxWeeks];
        int[] commonPerWeek = new int[maxWeeks];

        // Same logic as First Fit but with sorted input
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            boolean assigned = false;

            for (int week = 0; week < maxWeeks && !assigned; week++) {
                if (FIQEvaluator.canAddToWeek(q, week, timePerWeek, commonPerWeek)) {
                    config[i] = week;
                    timePerWeek[week] += q.getEstimatedTime();
                    if (Utils.isCommon(q)) {
                        commonPerWeek[week]++;
                    }
                    assigned = true;
                }
            }
        }

        bestConfig = config;
        bestWeeks = Utils.getWeeksUsed(config);
        timer.stop();

        printResults(timer, "First Fit Decreasing");
    }

    /**
     * Priority-Based heuristic: processes Legendary quests first, then Rare, then Common.
     * Ensures important quests get scheduled before filling with common ones.
     * @param loadedQuests the list of quests to schedule
     * @param timer the timer for measuring execution time
     */
    public static void runPriorityBased(List<Quest> loadedQuests, Timer timer) {
        timer.start();
        // Sort by importance: Legendary > Rare > Common
        quests = Utils.sortByImportanceDesc(loadedQuests);
        int maxWeeks = quests.size();

        int[] config = new int[quests.size()];
        int[] timePerWeek = new int[maxWeeks];
        int[] commonPerWeek = new int[maxWeeks];

        // First Fit with importance-sorted input
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            boolean assigned = false;

            for (int week = 0; week < maxWeeks && !assigned; week++) {
                if (FIQEvaluator.canAddToWeek(q, week, timePerWeek, commonPerWeek)) {
                    config[i] = week;
                    timePerWeek[week] += q.getEstimatedTime();
                    if (Utils.isCommon(q)) {
                        commonPerWeek[week]++;
                    }
                    assigned = true;
                }
            }
        }

        bestConfig = config;
        bestWeeks = Utils.getWeeksUsed(config);
        timer.stop();

        printResults(timer, "Priority-Based");
    }

    /**
     * Best Fit heuristic: assigns each quest to the week with least remaining capacity that still fits.
     * Minimizes wasted space by filling weeks as tightly as possible.
     * @param loadedQuests the list of quests to schedule
     * @param timer the timer for measuring execution time
     */
    public static void runBestFit(List<Quest> loadedQuests, Timer timer) {
        timer.start();
        quests = new ArrayList<>(loadedQuests);
        int maxWeeks = quests.size();

        int[] config = new int[quests.size()];
        int[] timePerWeek = new int[maxWeeks];
        int[] commonPerWeek = new int[maxWeeks];

        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            int bestWeek = -1;
            int minRemaining = Integer.MAX_VALUE;

            // Find the week that would have the least remaining time after adding this quest
            for (int week = 0; week < maxWeeks; week++) {
                if (FIQEvaluator.canAddToWeek(q, week, timePerWeek, commonPerWeek)) {
                    // Calculate remaining capacity after adding this quest
                    int remaining = FIQEvaluator.WEEKLY_MAX_MINUTES - (timePerWeek[week] + q.getEstimatedTime());
                    // Choose the week with minimum remaining (tightest fit)
                    if (remaining < minRemaining) {
                        minRemaining = remaining;
                        bestWeek = week;
                    }
                }
            }

            // Assign to the best fitting week
            if (bestWeek != -1) {
                config[i] = bestWeek;
                timePerWeek[bestWeek] += q.getEstimatedTime();
                if (Utils.isCommon(q)) {
                    commonPerWeek[bestWeek]++;
                }
            }
        }

        bestConfig = config;
        bestWeeks = Utils.getWeeksUsed(config);
        timer.stop();

        printResults(timer, "Best Fit");
    }

    /**
     * Prints the results of a greedy algorithm execution.
     * @param timer the timer with elapsed time
     * @param heuristicName the name of the heuristic used
     */
    private static void printResults(Timer timer, String heuristicName) {
        System.out.println("\tHeuristic: " + heuristicName);
        System.out.printf("\tExecution time: %.3f ms%n", timer.getElapsedMillis());

        if (bestConfig != null) {
            Utils.printWeeklySolution(quests, bestConfig, bestWeeks);
        } else {
            System.out.println("\tNo valid solution found.");
        }
    }

    /** @return the minimum weeks found in the best solution */
    public static int getBestWeeks() {
        return bestWeeks;
    }
}
