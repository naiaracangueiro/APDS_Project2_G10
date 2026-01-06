import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Dataset loader for reading quest data from .paed files.
 */
public class DSLoader {
    // Date format used by the dataset
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d-M-yyyy");


    /**
     * Loads the quests from the specified dataset
     * The method:
     *  1) Opens the file.
     *  2) Reads and ignores the first line (header with total quest count).
     *  3) Reads the quest lines and parses them.
     *
     * @param path       Path to the dataset file
     * @param numQuests  Maximum number of quests to load.
     * @return           A list of Quest objects
     * @throws IOException If the file is empty, unreadable, or a data line is malformed.
     */
    public static List<Quest> loadFile(String path, int numQuests) throws IOException {
        List<Quest> quests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            // First line is the header (total quest count), skip it
            String firstLine = br.readLine();
            if (firstLine == null) {
                throw new IOException("Empty file: " + path);
            }

            // Read up to numQuests lines
            for (int i = 0; i < numQuests; i++) {
                String line = br.readLine();
                if (line == null) break;

                // Skip blank lines but don't count them
                line = line.trim();
                if (line.isEmpty()) {
                    i--;
                    continue;
                }

                // Each line has 8 fields separated by semicolons
                String[] fields = line.split(";");
                if (fields.length != 8) {
                    throw new IOException("Invalid quest line. Missing information at line " + (i + 2));
                }

                // Parse each field from the CSV format
                String name = fields[0].trim();
                String subject = fields[1].trim();
                LocalDate deadline = LocalDate.parse(fields[2].trim(), DATE_FORMAT);
                int estimatedTime = Integer.parseInt(fields[3].trim());
                int difficulty = Integer.parseInt(fields[4].trim());
                int progress = Integer.parseInt(fields[5].trim());
                String importance = fields[6].trim();

                // Location is in format "x-y"
                String[] locations = fields[7].trim().split("-");
                int locX = Integer.parseInt(locations[0].trim());
                int locY = Integer.parseInt(locations[1].trim());

                quests.add(new Quest(name, subject, deadline, estimatedTime, difficulty, progress, importance, locX, locY));
            }
        }
        return quests;
    }
}
