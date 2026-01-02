import java.util.Arrays;
import java.util.List;

public class WOFBruteForce {
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
        WOFEvaluator evaluator = new WOFEvaluator();
        evaluator.evaluate(, maxTime);
    }

    public static void runWithTimer(List<Quest> quests, int maxTime, Timer timer) {
        timer.start();
        run(quests, maxTime);
        timer.stop();
        System.out.printf("Execution time: %.3f ms%n", timer.getElapsedMillis());
    }
}
