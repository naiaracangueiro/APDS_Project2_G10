import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Backtracking algorithm for the Wall of Deadlines problem.
 * Uses marking technique to track constraints efficiently and prune invalid branches.
 */
public class WODBacktracking {
    private static List<Quest> quests;

    private static int bestSol;
    private static int[] bestConfig;

    private static final int DAILY_MAX_MIN = 480;

    private static Map<LocalDate, Integer> timeByDeadline;
    private static Map<String, Integer> countBySubject;
    private static Map<String, Integer> timeBySubject;

    private static int questsValue;     // bc some of them count double or by 5

    /**
     * Recursive backtracking exploration with marking.
     * @param config current configuration array
     * @param level current quest index being decided
     * @param maxTime maximum allowed time
     * @param time accumulated time so far (with discounts applied)
     */
    private static void backtracking(int[] config, int level, int maxTime, double time) {
        // Binary choice: 0 = don't select quest, 1 = select quest
        for (int option = 0; option <= 1; option++) {
            config[level] = option;

            // Save time value so if marking returns -1 we don't mess it up
            double nextTime = time;

            // Apply marking only when selecting a quest
            if (option == 1) {
                // mark() returns -1 if deadline constraint is violated, otherwise the new accumulated time
                nextTime = mark(level, time);
            }

            // Pruning: skip if marking failed or time limit exceeded
            if (nextTime != -1 && nextTime <= maxTime) {
                if (level < quests.size() - 1) {
                    // Not at last level, continue exploring deeper
                    backtracking(config, level + 1, maxTime, nextTime);
                } else {
                    // Complete configuration reached, check if it's better (PBCBS)
                    if (questsValue > bestSol) {
                        bestSol = questsValue;
                        bestConfig = config.clone();
                    }
                }
            }

            // Undo marking when backtracking (only if we marked successfully)
            if (option == 1 && nextTime != -1) {
                unmark(level, nextTime);
            }
        }

    }

    private static void run(List<Quest> loadedQuests, int maxTime) {
        quests = loadedQuests;
        bestSol = -1;
        bestConfig = null;

        int[] config = new int[quests.size()];

        // Init marking
        timeByDeadline = new HashMap<>();
        countBySubject = new HashMap<>();
        timeBySubject = new HashMap<>();
        questsValue = 0;

        backtracking(config, 0, maxTime, 0);
    }

    /**
     * Runs the backtracking algorithm with timing and displays results.
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

    private static void printConfig(int[] config) {
        for (int i = 0; i < config.length; i++) {
            if (config[i] == 1) {
                System.out.println("\t\t" + quests.get(i).toString());
            }
        }
    }

    /**
     * Marks a quest as selected and updates all tracking variables.
     * @return the new accumulated time, or -1 if constraints are violated
     */
    private static double mark(int level, double current) {
        Quest q = quests.get(level);

        // Add quest value (Legendary=5, Rare=2, Common=1)
        questsValue += getValue(q);

        // Constraint 1: Check deadline time limit (max 480 min per deadline)
        LocalDate deadline = q.getDeadline();
        int oldTime = timeByDeadline.getOrDefault(deadline, 0);
        int newTime = oldTime + q.getEstimatedTime();
        if (newTime > DAILY_MAX_MIN) {
            // Constraint violated, undo the quest value addition
            questsValue -= getValue(q);
            return -1;
        }
        timeByDeadline.put(deadline, newTime);

        // Update subject tracking for 10% discount calculation
        String subject = q.getSubject();
        int oldCount = countBySubject.getOrDefault(subject, 0);
        int oldSubTime = timeBySubject.getOrDefault(subject, 0);

        // Subtract the old subject time (before adding this quest)
        current -= calculateSubjectTime(oldCount, oldSubTime);

        // Update subject counters
        int newCount = oldCount + 1;
        int newSubTime = oldSubTime + q.getEstimatedTime();
        countBySubject.put(subject, newCount);
        timeBySubject.put(subject, newSubTime);

        // Add the new subject time (may include 10% discount if 2+ quests)
        current += calculateSubjectTime(newCount, newSubTime);

        return current;
    }

    /** Returns quest value: Legendary=5, Rare=2, Common=1 */
    private static int getValue(Quest quest) {
        String importance = quest.getImportanceName();

        if (importance.equals("Legendary")) return 5;
        if (importance.equals("Rare")) return 2;
        else return 1;
    }

    /** Applies 10% time reduction if 2+ quests from same subject */
    private static double calculateSubjectTime(int count, int minutes) {
        if (count >= 2) {
            return minutes * 0.9;
        }
        return minutes;
    }

    /** Reverts marking when backtracking */
    private static double unmark(int level, double current) {
        Quest q = quests.get(level);

        // Subtract quest value
        questsValue -= getValue(q);

        // Restore deadline time tracking
        LocalDate deadline = q.getDeadline();
        int currTime = timeByDeadline.getOrDefault(deadline, 0);
        int oldTime = currTime - q.getEstimatedTime();
        if (oldTime == 0) {
            // No more quests for this deadline, remove entry
            timeByDeadline.remove(deadline);
        } else {
            timeByDeadline.put(deadline, oldTime);
        }

        // Restore subject time tracking and recalculate discount
        String subject = q.getSubject();
        int currCount = countBySubject.getOrDefault(subject, 0);
        int currSubTime = timeBySubject.getOrDefault(subject, 0);

        // Subtract current subject contribution (with current discount)
        current -= calculateSubjectTime(currCount, currSubTime);

        // Calculate what the subject tracking was before this quest
        int oldCount = currCount - 1;
        int oldSubTime = currSubTime - q.getEstimatedTime();

        if (oldCount == 0) {
            // No more quests for this subject, remove entries
            countBySubject.remove(subject);
            timeBySubject.remove(subject);
        } else {
            countBySubject.put(subject, oldCount);
            timeBySubject.put(subject, oldSubTime);
        }

        // Add back the old subject contribution (with old discount)
        current += calculateSubjectTime(oldCount, oldSubTime);

        return current;
    }
}
