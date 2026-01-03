import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class with helper functions for quest classification, sorting, and solution display.
 */
public class Utils {

    /**
     * Checks if a quest is of Common importance.
     * @param q the quest to check
     * @return true if the quest is Common
     */
    public static boolean isCommon(Quest q) {
        return q.getImportanceName().equals("Common");
    }

    /**
     * Checks if a quest is of Rare importance.
     * @param q the quest to check
     * @return true if the quest is Rare
     */
    public static boolean isRare(Quest q) {
        return q.getImportanceName().equals("Rare");
    }

    /**
     * Checks if a quest is of Legendary importance.
     * @param q the quest to check
     * @return true if the quest is Legendary
     */
    public static boolean isLegendary(Quest q) {
        return q.getImportanceName().equals("Legendary");
    }

    /**
     * Returns a numeric rank for quest importance.
     * @param q the quest to evaluate
     * @return 3 for Legendary, 2 for Rare, 1 for Common
     */
    public static int getImportanceRank(Quest q) {
        switch (q.getImportanceName()) {
            case "Legendary": return 3;
            case "Rare": return 2;
            default: return 1;
        }
    }

    /**
     * Sorts quests by importance in descending order (Legendary first).
     * @param quests the list of quests to sort
     * @return a new sorted list
     */
    public static List<Quest> sortByImportanceDesc(List<Quest> quests) {
        List<Quest> sorted = new ArrayList<>(quests);
        Collections.sort(sorted, new Comparator<Quest>() {
            @Override
            public int compare(Quest q1, Quest q2) {
                return getImportanceRank(q2) - getImportanceRank(q1);
            }
        });
        return sorted;
    }

    /**
     * Sorts quests by estimated time in descending order (longest first).
     * @param quests the list of quests to sort
     * @return a new sorted list
     */
    public static List<Quest> sortByTimeDesc(List<Quest> quests) {
        List<Quest> sorted = new ArrayList<>(quests);
        Collections.sort(sorted, new Comparator<Quest>() {
            @Override
            public int compare(Quest q1, Quest q2) {
                return q2.estimatedTime - q1.estimatedTime;
            }
        });
        return sorted;
    }

    /**
     * Calculates the upper bound for maximum weeks needed based on time and common quest constraints.
     * @param quests the list of quests
     * @return the maximum number of weeks that could be needed
     */
    public static int calculateMaxWeeks(List<Quest> quests) {
        int totalTime = 0;
        int commonCount = 0;

        for (Quest q : quests) {
            totalTime += q.estimatedTime;
            if (isCommon(q)) {
                commonCount++;
            }
        }

        int byTime = (int) Math.ceil(totalTime / 1200.0);
        int byCommon = (int) Math.ceil(commonCount / 6.0);

        return Math.max(Math.max(byTime, byCommon), 1);
    }

    /**
     * Returns the number of weeks used in a configuration.
     * @param config the configuration array where config[i] = week assigned to quest i
     * @return the number of weeks used (max week index + 1)
     */
    public static int getWeeksUsed(int[] config) {
        int max = 0;
        for (int week : config) {
            if (week > max) {
                max = week;
            }
        }
        return max + 1;
    }

    /**
     * Prints the weekly schedule showing quests assigned to each week.
     * @param quests the list of quests
     * @param config the configuration array
     * @param weeksUsed the total number of weeks in the solution
     */
    public static void printWeeklySolution(List<Quest> quests, int[] config, int weeksUsed) {
        System.out.println("\tSolution uses " + weeksUsed + " week(s):");

        for (int w = 0; w < weeksUsed; w++) {
            System.out.println("\n\tWeek " + (w + 1) + ":");
            int totalTime = 0;
            int commonCount = 0;

            for (int i = 0; i < quests.size(); i++) {
                if (config[i] == w) {
                    Quest q = quests.get(i);
                    System.out.println("\t  - " + q.name + " (" + q.estimatedTime + " min, " + q.getImportanceName() + ")");
                    totalTime += q.estimatedTime;
                    if (isCommon(q)) {
                        commonCount++;
                    }
                }
            }
            System.out.println("\t  Total: " + totalTime + "/1200 min, " + commonCount + "/6 common quests");
        }
    }

    /**
     * Counts the number of common quests in a list.
     * @param quests the list of quests
     * @return the count of common quests
     */
    public static int countCommonQuests(List<Quest> quests) {
        int count = 0;
        for (Quest q : quests) {
            if (isCommon(q)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the total estimated time for a list of quests.
     * @param quests the list of quests
     * @return the sum of all estimated times in minutes
     */
    public static int totalTime(List<Quest> quests) {
        int total = 0;
        for (Quest q : quests) {
            total += q.estimatedTime;
        }
        return total;
    }
}
