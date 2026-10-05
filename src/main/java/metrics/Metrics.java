package metrics;
public class Metrics {
    public long steps;
    public long moves;
    public long comparisons;

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}