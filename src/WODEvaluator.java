import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for the Wall of Deadlines problem.
 * Validates solutions against constraints and calculates total quest value.
 */
public class WODEvaluator {
    private static final int DAILY_MAX_MIN = 480;

    /**
     * Evaluates a solution and returns the total quest value.
     * Checks: deadline constraint (480 min/day), time limit, and applies 10% subject discount.
     * @return total quest value (Legendary=5, Rare=2, Common=1), or -1 if invalid
     */
    public static int evaluate(List<Quest> selectedQuests, int maxTime) {
        // Maps to track constraints as we process quests
        Map<LocalDate, Integer> questsByDeadline = new HashMap<>();
        Map<String, Integer> countBySubject = new HashMap<>();
        Map<String, Integer> timeBySubject = new HashMap<>();

        int totalQuests = 0;

        for (Quest quest : selectedQuests) {
            // Accumulate quest value (Legendary=5, Rare=2, Common=1)
            totalQuests += getValue(quest);

            // Constraint 1: Check deadline limit (max 480 min per deadline)
            LocalDate deadline = quest.getDeadline();
            int sum = questsByDeadline.getOrDefault(deadline, 0) + quest.getEstimatedTime();
            if (sum > DAILY_MAX_MIN) {
                return -1;
            }
            questsByDeadline.put(deadline, sum);

            // Track subject data for 10% discount calculation
            String subject = quest.getSubject();
            countBySubject.put(subject, countBySubject.getOrDefault(subject, 0) + 1);
            timeBySubject.put(subject, timeBySubject.getOrDefault(subject, 0) + quest.getEstimatedTime());
        }

        // Calculate total time with 10% discount for subjects with 2+ quests
        double totalTime = 0.0;
        for (String subject : timeBySubject.keySet()) {
            int t = timeBySubject.get(subject);
            int num = countBySubject.get(subject);

            if (num >= 2) {
                // Apply 10% discount for same-subject quests
                totalTime += t * 0.9;
            } else {
                totalTime += t;
            }
        }

        // Constraint 2: Check overall time limit
        if (totalTime > maxTime) {
            return -1;
        }

        return totalQuests;
    }

    private static int getValue(Quest quest) {
        String importance = quest.getImportanceName();

        if (importance.equals("Legendary")) return 5;
        if (importance.equals("Rare")) return 2;
        else return 1;
    }
}
