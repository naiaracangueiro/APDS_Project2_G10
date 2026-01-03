import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WODEvaluator {
    private static final int DAILY_MAX_MIN = 480;

    // It will return the number of quests or -1 if its invalid
    public static int evaluate(List<Quest> selectedQuests, int maxTime) {
        // It sums the total minutes that each deadline has
        Map<LocalDate, Integer> questsByDeadline = new HashMap<>();
        // It counts the number of quests per subject
        Map<String, Integer> countBySubject = new HashMap<>();
        // It sums the total minutes that the quests by subject have
        Map<String, Integer> timeBySubject= new HashMap<>();

        int totalQuests = 0;

        for (Quest quest : selectedQuests) {
            totalQuests += getValue(quest);

            // sum deadlines' time
            LocalDate deadline = quest.getDeadline();
            int sum = questsByDeadline.getOrDefault(deadline, 0) + quest.estimatedTime;
            if (sum > DAILY_MAX_MIN) {
                return -1;
            }
            questsByDeadline.put(deadline, sum);

            // sum subjects count and time
            String subject = quest.getSubject();
            countBySubject.put(subject, countBySubject.getOrDefault(subject, 0) + 1);
            timeBySubject.put(subject, timeBySubject.getOrDefault(subject, 0) + quest.estimatedTime);
        }

        // Calculate the total time (applying reduction if needed)
        double totalTime = 0.0;
        for (String subject : timeBySubject.keySet()) {
            int t = timeBySubject.get(subject);
            int num = countBySubject.get(subject);

            if (num >= 2) {
                totalTime += t * 0.9;
            } else {
                totalTime += t;
            }
        }
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
