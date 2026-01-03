import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WODBruteForce {
    private static List<Quest> quests;

    private static int bestSol;
    private static int[] bestConfig;

    private static void bruteForce(int[] config, int level, int maxTime) {

        for (int option = 0; option <= 1; option++) {
            config[level] = option;

            if (level < config.length - 1) {
                bruteForce(config, level + 1, maxTime);
            } else {
                checkSolution(config, maxTime);
            }
        }
    }

    private static void run(List<Quest> loadedQuests, int maxTime) {
        quests = loadedQuests;
        bestSol = -1;
        bestConfig = null;

        int[] config = new int[quests.size()];
        Arrays.fill(config, -1);

        bruteForce(config, 0, maxTime);
    }

    private static void checkSolution(int[] config, int maxTime) {
        List<Quest> selectedQuests = createList(config);
        int totalQuests = WODEvaluator.evaluate(selectedQuests, maxTime);

        if (totalQuests != -1 && totalQuests > bestSol) {
            bestSol = totalQuests;
            bestConfig = config.clone();
        }
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
