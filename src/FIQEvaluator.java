import java.util.List;

/**
 * Evaluator for the Festival of Infinite Quests problem.
 * Validates solutions against the problem constraints.
 */
public class FIQEvaluator {
    /** Maximum minutes allowed per week (20 hours) */
    public static final int WEEKLY_MAX_MINUTES = 1200;
    /** Maximum common quests allowed per week */
    public static final int MAX_COMMON_PER_WEEK = 6;

    /**
     * Validates a configuration and returns the number of weeks used.
     * @param quests the list of quests
     * @param config the configuration array where config[i] = week assigned to quest i
     * @return the number of weeks used, or -1 if constraints are violated
     */
    public static int evaluate(List<Quest> quests, int[] config) {
        // Determine how many weeks are used in this configuration
        int maxWeeks = Utils.getWeeksUsed(config);

        // Arrays to accumulate constraints per week
        int[] timePerWeek = new int[maxWeeks];
        int[] commonPerWeek = new int[maxWeeks];

        // First pass: accumulate time and common count for each week
        for (int i = 0; i < quests.size(); i++) {
            int week = config[i];
            Quest q = quests.get(i);

            timePerWeek[week] += q.getEstimatedTime();
            if (Utils.isCommon(q)) {
                commonPerWeek[week]++;
            }
        }

        // Second pass: check constraints for each week
        for (int w = 0; w < maxWeeks; w++) {
            // Constraint 1: Max 1200 minutes (20 hours) per week
            if (timePerWeek[w] > WEEKLY_MAX_MINUTES) {
                return -1;
            }
            // Constraint 2: Max 6 common quests per week
            if (commonPerWeek[w] > MAX_COMMON_PER_WEEK) {
                return -1;
            }
        }

        return maxWeeks;
    }

    /**
     * Checks if a quest can be added to a specific week without violating constraints.
     * @param quest the quest to add
     * @param week the week index
     * @param timePerWeek array tracking accumulated time per week
     * @param commonPerWeek array tracking common quest count per week
     * @return true if the quest can be added without violating constraints
     */
    public static boolean canAddToWeek(Quest quest, int week, int[] timePerWeek, int[] commonPerWeek) {
        // Check time constraint: would adding this quest exceed 1200 min?
        int newTime = timePerWeek[week] + quest.getEstimatedTime();
        if (newTime > WEEKLY_MAX_MINUTES) {
            return false;
        }

        // Check common quest constraint: already 6 common quests in this week?
        if (Utils.isCommon(quest) && commonPerWeek[week] >= MAX_COMMON_PER_WEEK) {
            return false;
        }

        return true;
    }
}
