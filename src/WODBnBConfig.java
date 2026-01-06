import java.time.LocalDate;
import java.util.*;

/**
 * Configuration class for the Branch and Bound algorithm.
 * Represents a partial solution and implements Comparable for priority queue ordering.
 * Each config tracks selected quests and accumulated constraints via marking.
 */
public class WODBnBConfig implements Comparable<WODBnBConfig> {
    // represents which quests we pick
    private final int[] config;
    private int time; //total time invested
    private int questsValue;  // value of quests (x1, x2, x5)
    private int level; // current level
    private int lastIndex;

    // For marking purposes
    private Map<LocalDate, Integer> timeByDeadline;     // time per deadline
    private Map<String, Integer> countBySubject;        // quests per subject
    private Map<String, Integer> timeBySubject;         // time per subject

    public WODBnBConfig() {
        config = new int[WODBnBGlobals.N];
        Arrays.fill(config, -1);
        time = 0;
        questsValue = 0;
        level = 0;
        lastIndex = -1;

        timeByDeadline = new HashMap<>();
        countBySubject = new HashMap<>();
        timeBySubject = new HashMap<>();
    }

    public WODBnBConfig(WODBnBConfig that) {
        this.config = that.config.clone();
        this.time = that.time;
        this.questsValue = that.questsValue;
        this.level = that.level;
        this.timeByDeadline = new HashMap<>(that.timeByDeadline);
        this.countBySubject = new HashMap<>(that.countBySubject);
        this.timeBySubject = new HashMap<>(that.timeBySubject);
    }

    /**
     * Generates child configurations by trying to add each remaining quest.
     * @return list of valid child configurations
     */
    public List<WODBnBConfig> expand() {
        List<WODBnBConfig> children = new ArrayList<>();

        // Try adding each unassigned quest after the last selected one
        for (int i = lastIndex + 1; i < WODBnBGlobals.N; i++) {
            // Skip already selected quests
            if (config[i] == 1) continue;

            Quest quest = WODBnBGlobals.QUESTS.get(i);
            // Create a copy of current config and try adding this quest
            WODBnBConfig next = new WODBnBConfig(this);
            if (next.checkQuest(i, quest)) {
                // Quest was valid and added, include in children
                children.add(next);
            }

        }

        return children;
    }

    /**
     * Checks if adding a quest is valid and updates marking if so.
     * @return true if quest was successfully added, false if constraints violated
     */
    private boolean checkQuest(int index, Quest q) {
        // Constraint 1: Check deadline time limit (max 480 min per deadline)
        LocalDate deadline = q.getDeadline();
        int newTime = timeByDeadline.getOrDefault(deadline, 0) + q.getEstimatedTime();
        if (newTime > WODBnBGlobals.DAILY_MAX_MIN) return false;

        // Calculate time difference with 10% subject discount
        String subject = q.getSubject();
        int oldCount = countBySubject.getOrDefault(subject, 0);
        int oldSubTime = timeBySubject.getOrDefault(subject, 0);

        // Get time contribution before adding this quest
        double oldTotalTime = calculateSubjectTime(oldCount, oldSubTime);

        int newCount = oldCount + 1;
        int newSubTime = oldSubTime + q.getEstimatedTime();

        // Get time contribution after adding this quest (may include 10% discount)
        double newTotalTime = calculateSubjectTime(newCount, newSubTime);

        // Calculate new total time: current + difference (discount is applied incrementally)
        double timeResult = this.time + (newTotalTime - oldTotalTime);

        // Constraint 2: Check overall time limit
        if (timeResult > WODBnBGlobals.TIME_LIMIT) return false;

        // Both constraints passed, update marking variables
        timeByDeadline.put(deadline, newTime);
        countBySubject.put(subject, newCount);
        timeBySubject.put(subject, newSubTime);

        this.time = (int) timeResult;
        this.questsValue += getValue(q);

        // Mark quest as selected in config
        config[index] = 1;

        // Update progress tracking
        this.level++;
        this.lastIndex = index;

        return true;
    }

    private int getValue(Quest quest) {
        String importance = quest.getImportanceName();

        if (importance.equals("Legendary")) return 5;
        if (importance.equals("Rare")) return 2;
        else return 1;
    }

    private double calculateSubjectTime(int count, int minutes) {
        if (count >= 2) {
            return minutes * 0.9;
        }
        return minutes;
    }

    public boolean isFull()  {
        return lastIndex == WODBnBGlobals.N - 1;
    }

    public int getTime() {
        return time;
    }

    public int getQuestsValue() {
        return questsValue;
    }

    public int[] getConfig() {
        return config;
    }

    /**
     * Calculates upper bound estimate assuming all remaining quests are Legendary.
     * Used for pruning: if estimate <= bestSol, this branch can be discarded.
     */
    public int estimate() {
        int remaining = WODBnBGlobals.N - (lastIndex + 1);
        return questsValue + 5 * remaining;
    }

    @Override
    public int compareTo(WODBnBConfig that) {
        return Double.compare(that.estimate(), this.estimate());
    }
}
