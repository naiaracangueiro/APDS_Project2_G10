import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Performance measurement class for tracking and exporting algorithm metrics.
 * Supports both FIQ and WOD problems.
 */
public class PerformanceMetrics {
    private String algorithmName;
    private String problemName;
    private int datasetSize;
    private double executionTimeMs;
    private int solutionValue;
    private long nodesExplored;
    private long configurationsGenerated;
    private long memoryUsedBytes;
    private String timestamp;

    private static final String CSV_DIRECTORY = "performance_measures";
    private static final String CSV_HEADER = "algorithm,problem,dataset_size,time_ms,solution_value,nodes_explored,configs_generated,memory_bytes,timestamp";

    /**
     * Creates a new PerformanceMetrics instance.
     * @param algorithmName the name of the algorithm being measured
     * @param problemName the problem being solved (FIQ or WOD)
     */
    public PerformanceMetrics(String algorithmName, String problemName) {
        this.algorithmName = algorithmName;
        this.problemName = problemName;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Records performance data from a completed algorithm run.
     * @param datasetSize the number of quests in the dataset
     * @param timer the timer with elapsed time
     * @param solutionValue the solution quality (weeks for FIQ, score for WOD)
     */
    public void record(int datasetSize, Timer timer, int solutionValue) {
        this.datasetSize = datasetSize;
        this.executionTimeMs = timer.getElapsedMillis();
        this.solutionValue = solutionValue;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        estimateMemoryUsage();
    }

    /**
     * Sets the number of nodes explored during the algorithm.
     * @param count the node count
     */
    public void setNodesExplored(long count) {
        this.nodesExplored = count;
    }

    /**
     * Sets the number of configurations generated during the algorithm.
     * @param count the configuration count
     */
    public void setConfigurationsGenerated(long count) {
        this.configurationsGenerated = count;
    }

    /**
     * Estimates current memory usage from the JVM runtime.
     */
    public void estimateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        this.memoryUsedBytes = runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Displays all metrics to the console.
     */
    public void display() {
        System.out.println("\n===== Performance Metrics =====");
        System.out.println("Algorithm: " + algorithmName);
        System.out.println("Problem: " + problemName);
        System.out.println("Dataset size: " + datasetSize + " quests");
        System.out.printf("Execution time: %.3f ms%n", executionTimeMs);
        System.out.println("Solution value: " + solutionValue);
        System.out.println("Nodes explored: " + nodesExplored);
        System.out.println("Configurations generated: " + configurationsGenerated);
        System.out.printf("Memory used: %.2f KB%n", memoryUsedBytes / 1024.0);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("===============================\n");
    }

    /**
     * Converts metrics to a CSV-formatted line.
     * @return CSV line string
     */
    public String toCSVLine() {
        return String.format(Locale.US, "%s,%s,%d,%.3f,%d,%d,%d,%d,%s",
                algorithmName,
                problemName,
                datasetSize,
                executionTimeMs,
                solutionValue,
                nodesExplored,
                configurationsGenerated,
                memoryUsedBytes,
                timestamp);
    }

    /**
     * Exports a list of metrics to a CSV file.
     * Creates the performance_measures directory if it doesn't exist.
     * @param filename the CSV filename
     * @param metrics the list of metrics to export
     */
    public static void exportToCSV(String filename, List<PerformanceMetrics> metrics) {
        File directory = new File(CSV_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = CSV_DIRECTORY + "/" + filename;
        File file = new File(filePath);
        boolean fileExists = file.exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.println(CSV_HEADER);
            }

            for (PerformanceMetrics metric : metrics) {
                writer.println(metric.toCSVLine());
            }

            System.out.println("Metrics exported to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Exports this single metric to a CSV file.
     * @param filename the CSV filename
     */
    public void exportToCSV(String filename) {
        List<PerformanceMetrics> single = new ArrayList<>();
        single.add(this);
        exportToCSV(filename, single);
    }

    /** @return the algorithm name */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /** @return the problem name */
    public String getProblemName() {
        return problemName;
    }

    /** @return the dataset size */
    public int getDatasetSize() {
        return datasetSize;
    }

    /** @return the execution time in milliseconds */
    public double getExecutionTimeMs() {
        return executionTimeMs;
    }

    /** @return the solution value */
    public int getSolutionValue() {
        return solutionValue;
    }

    /** @return the number of nodes explored */
    public long getNodesExplored() {
        return nodesExplored;
    }

    /** @return the number of configurations generated */
    public long getConfigurationsGenerated() {
        return configurationsGenerated;
    }

    /** @return the memory used in bytes */
    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }
}
