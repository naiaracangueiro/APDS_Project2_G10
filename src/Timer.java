public class Timer {
    private long startTime;
    private long endTime;

    public Timer() {
        this.startTime = 0;
        this.endTime = 0;
    }

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void stop() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedNano() {
        return endTime - startTime;
    }

    public double getElapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public double getElapsedSeconds() {
        return (endTime - startTime) / 1_000_000_000.0;
    }

    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
    }
}
