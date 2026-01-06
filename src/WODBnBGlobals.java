import java.util.List;

/**
 * Global variables shared across BnB algorithm classes.
 * Stores quest list, time limit, and problem size.
 */
public class WODBnBGlobals {
    /** Maximum minutes allowed per deadline (8 hours) */
    public static final int DAILY_MAX_MIN = 480;

    /** The list of quests to optimize */
    public static List<Quest> QUESTS;
    /** User-specified maximum total time in minutes */
    public static int TIME_LIMIT;
    /** Number of quests (problem size) */
    public static int N;

    /** Initializes global variables before running BnB algorithm */
    public static void init(List<Quest> questsList, int maxTime) {
        QUESTS = questsList;
        TIME_LIMIT = maxTime;
        N = QUESTS.size();
    }
}
