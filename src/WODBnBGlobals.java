import java.util.List;

public class WODBnBGlobals {
    public static final int DAILY_MAX_MIN = 480;

    public static List<Quest> QUESTS;
    public static int TIME_LIMIT;
    public static int N;

    public static void init(List<Quest> questsList, int maxTime) {
        QUESTS = questsList;
        TIME_LIMIT = maxTime;
        N = QUESTS.size();
    }
}
