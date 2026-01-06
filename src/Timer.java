/**
 * Simple timer class for measuring algorithm execution time.
 * Uses System.nanoTime() for high precision measurements.
 */
public class Timer {
    private long startTime;
    private long endTime;

    public Timer() {
        this.startTime = 0;
        this.endTime = 0;
    }

    /** Starts the timer */
    public void start() {
        this.startTime = System.nanoTime();
    }

    /** Stops the timer */
    public void stop() {
        this.endTime = System.nanoTime();
    }

    /** @return elapsed time in nanoseconds */
    public long getElapsedNano() {
        return endTime - startTime;
    }

    /** @return elapsed time in milliseconds */
    public double getElapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /** @return elapsed time in seconds */
    public double getElapsedSeconds() {
        return (endTime - startTime) / 1_000_000_000.0;
    }

    /** Resets the timer to initial state */
    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
    }
}
