import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WODBacktracking {
    private static List<Quest> quests;

    private static int bestSol;
    private static int[] bestConfig;

    private static final int DAILY_MAX_MIN = 480;

    private static Map<LocalDate, Integer> timeByDeadline;
    private static Map<String, Integer> countBySubject;
    private static Map<String, Integer> timeBySubject;

    private static int questsValue;     // bc some of them count double or by 5

    private static void backtracking(int[] config, int level, int maxTime, double time) {
        for (int option = 0; option <= 1; option++) {
            config[level] = option;

            // Save time value so if marking returns -1 we don't mess it up
            double nextTime = time;

            // Marking
            // Time can be -1 if it's not valid (already exceeds 480 for same deadline) or the current time sum
            if (option == 1) {
                nextTime = mark(level, time);
            }

            if (nextTime != -1 && nextTime <= maxTime) {
                if (level < quests.size() - 1) {
                    backtracking(config, level + 1, maxTime, nextTime);
                } else {
                    if (questsValue > bestSol) {
                        bestSol = questsValue;
                        bestConfig = config.clone();
                    }
                }
            }

            //undo marking
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

    private static double mark(int level, double current) {
        Quest q = quests.get(level);

        questsValue += getValue(q);

        // Check if the sum of time for the quest deadline already exceeds the limit
        LocalDate deadline = q.getDeadline();
        int oldTime = timeByDeadline.getOrDefault(deadline, 0);
        int newTime = oldTime + q.estimatedTime;
        if (newTime > DAILY_MAX_MIN) {
            questsValue -= getValue(q);
            return -1;
        }
        timeByDeadline.put(deadline, newTime);

        // Check if we can apply the 10% discount
        String subject = q.getSubject();
        int oldCount = countBySubject.getOrDefault(subject, 0);
        int oldSubTime = timeBySubject.getOrDefault(subject, 0);

        current -= calculateSubjectTime(oldCount, oldSubTime);

        int newCount = oldCount + 1;
        int newSubTime = oldSubTime + q.estimatedTime;
        countBySubject.put(subject, newCount);
        timeBySubject.put(subject, newSubTime);

        current += calculateSubjectTime(newCount, newSubTime);

        return current;
    }

    private static int getValue(Quest quest) {
        String importance = quest.getImportanceName();

        if (importance.equals("Legendary")) return 5;
        if (importance.equals("Rare")) return 2;
        else return 1;
    }

    private static double calculateSubjectTime(int count, int minutes) {
        if (count >= 2) {
            return minutes * 0.9;
        }
        return minutes;
    }

    private static double unmark(int level, double current) {
        Quest q = quests.get(level);

        questsValue -= getValue(q);

        // Undo the previous sum for the quest deadline
        LocalDate deadline = q.getDeadline();
        int currTime = timeByDeadline.getOrDefault(deadline, 0);
        int oldTime = currTime - q.estimatedTime;
        if (oldTime == 0) {
            timeByDeadline.remove(deadline);
        } else {
            timeByDeadline.put(deadline, oldTime);
        }

        // Undo the subject discount
        String subject = q.getSubject();
        int currCount = countBySubject.getOrDefault(subject, 0);
        int currSubTime = timeBySubject.getOrDefault(subject, 0);

        current -= calculateSubjectTime(currCount, currSubTime);

        int oldCount = currCount - 1;
        int oldSubTime = currSubTime - q.estimatedTime;

        if (oldCount == 0) {
            countBySubject.remove(subject);
            timeBySubject.remove(subject);
        } else {
            countBySubject.put(subject, oldCount);
            timeBySubject.put(subject, oldSubTime);
        }

        current += calculateSubjectTime(oldCount, oldSubTime);

        return current;
    }
}
