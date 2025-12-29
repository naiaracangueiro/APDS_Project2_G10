import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Quest {
    public final String name;
    public final String subject;
    public final LocalDate deadline;
    public final int estimatedTime;
    public final int difficulty;
    public final int progress;
    public final String importance;
    public final int locX;
    public final int locY;

    public Quest (String name, String subject, LocalDate deadline, int estimatedTime,
                  int difficulty, int progress, String importance, int locX, int locY) {
        this.name = name;
        this.subject = subject;
        this.deadline = deadline;
        this.estimatedTime = estimatedTime;
        this.difficulty = difficulty;
        this.progress = progress;
        this.importance = importance;
        this.locX = locX;
        this.locY = locY;
    }

    /**
     * Checks if the quest is completed.
     * @return true if progress is 100%, false otherwise
     */
    public boolean isCompleted() {
        return progress == 100;
    }

    /**
     * Gets the readable name of the importance level.
     * @return String with the importance level name
     */
    public String getImportanceName() {
        String color = importance.toLowerCase();
        switch (color) {
            case "#ff8000":
                return "Legendary";
            case "#cc00ff":
                return "Rare";
            case "#4fd945":
                return "Common";
            default:
                return "Unknown";
        }
    }

    /**
     * Returns a formatted string representation of the quest.
     * @return String with quest information
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("%-50s | %-15s | %s | Difficulty: %2d | Progress: %3d%% | %s",
                name, subject, deadline.format(formatter), difficulty, progress, getImportanceName());
    }

    /**
     * Returns a short summary of the quest (useful for debugging).
     * @return Short string with quest name and deadline
     */
    public String toShortString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return name + " [" + deadline.format(formatter) + "]";
    }
}
