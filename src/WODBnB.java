import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class WODBnB {
    private static int bestSol;
    private static int[] bestConfig;

    private static void run(List<Quest> quests, int maxTime) {
        WODBnBGlobals.init(quests, maxTime);

        bestSol = Integer.MIN_VALUE;
        bestConfig = new int[quests.size()];
        Arrays.fill(bestConfig, -1);

        PriorityQueue<WODBnBConfig> queue = new PriorityQueue<>();
        WODBnBConfig first = new WODBnBConfig();
        queue.offer(first);

        while (!queue.isEmpty()) {
            WODBnBConfig config = queue.poll();

            if (config.estimate() <= bestSol) {
                continue;
            }

            List<WODBnBConfig> children = config.expand();

            for (WODBnBConfig child : children) {
                if (child.getQuestsValue() > bestSol) {
                    bestSol = child.getQuestsValue();
                    bestConfig = child.getConfig().clone();
                }
                if (child.estimate() > bestSol) {
                    queue.offer(child);
                }
            }
        }
    }

    public static void runWithTimer(List<Quest> quests, int maxTime, Timer timer) {
        timer.start();
        run(quests, maxTime);
        timer.stop();
        System.out.printf("\tExecution time: %.3f ms%n", timer.getElapsedMillis());
        System.out.println("\tBest solution: " + bestSol + " quests in " + maxTime + " minutes.");
        System.out.println("\tBest config: ");
        printConfig(bestConfig, quests);
    }

    private static void printConfig(int[] config, List<Quest> quests) {
        for (int i = 0; i < config.length; i++) {
            if (config[i] == 1) {
                System.out.println("\t\t" + quests.get(i).toString());
            }
        }
    }
}
