import java.time.LocalDate;
import java.util.*;

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

    public List<WODBnBConfig> expand() {
        List<WODBnBConfig> children = new ArrayList<>();

        for (int i = lastIndex + 1; i < WODBnBGlobals.N; i++) {
            if (config[i] == 1) continue;

            Quest quest = WODBnBGlobals.QUESTS.get(i);
            WODBnBConfig next = new WODBnBConfig(this);
            if (next.checkQuest(i, quest)) {
                children.add(next);
            }

        }

        return children;
    }

    private boolean checkQuest(int index, Quest q) {
        // Check deadline time limit
        LocalDate deadline = q.getDeadline();
        int newTime = timeByDeadline.getOrDefault(deadline, 0) + q.getEstimatedTime();
        if (newTime > WODBnBGlobals.DAILY_MAX_MIN) return false;

        // Check if reduction can be applied
        String subject = q.getSubject();
        int oldCount = countBySubject.getOrDefault(subject, 0);
        int oldSubTime = timeBySubject.getOrDefault(subject, 0);

        int oldTotalTime = calculateSubjectTime(oldCount, oldSubTime);

        int newCount = oldCount + 1;
        int newSubTime = oldSubTime + q.getEstimatedTime();

        int newTotalTime = calculateSubjectTime(newCount, newSubTime);

        int timeResult = this.time + (newTotalTime - oldTotalTime);
        if (timeResult > WODBnBGlobals.TIME_LIMIT) return false;

        // If it's under the limit we update the values
        timeByDeadline.put(deadline, newTime);
        countBySubject.put(subject, newCount);
        timeBySubject.put(subject, (int) newSubTime);

        this.time = (int) timeResult;
        this.questsValue += getValue(q);

        config[index] = 1;

        // Store the config
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

    private int calculateSubjectTime(int count, int minutes) {
        if (count >= 2) {
            return (minutes * 9) / 10;
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

    // upper bound -> all the remaining quests are legendary
    public int estimate() {
        int remaining = WODBnBGlobals.N - (lastIndex + 1);
        return questsValue + 5 * remaining;
    }

    @Override
    public int compareTo(WODBnBConfig that) {
        return Double.compare(that.estimate(), this.estimate());
    }
}
