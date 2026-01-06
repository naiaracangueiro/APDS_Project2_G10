import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Quest {
    private final String name;
    private final String subject;
    private final LocalDate deadline;
    private final int estimatedTime;
    private final int difficulty;
    private final int progress;
    private final String importance;
    private final int locX;
    private final int locY;

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

    public LocalDate getDeadline() {
        return deadline;
    }

    public String getSubject() {
        return subject;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a formatted string representation of the quest.
     * @return String with quest information
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("%s | %s | %s | Difficulty: %d | Progress: %d | %s",
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
