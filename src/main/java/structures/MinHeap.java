package structures;

import metrics.Metrics;

public class MinHeap {
    private int[] data = new int[10];
    private int size;
    private final Metrics m = new Metrics();

    public void insert(int x) {
        if (size == data.length) grow();
        int i = size++;
        while (i > 0) {
            int parent = (i - 1) / 2;
            m.steps++;
            m.comparisons++;
            if (data[parent] <= x) break;
            data[i] = data[parent];
            m.moves++;
            i = parent;
        }
        data[i] = x;
    }

    public int peekMin() {
        if (size == 0) throw new IllegalStateException("heap is empty");
        m.steps++;
        return data[0];
    }

    public int extractMin() {
        if (size == 0) throw new IllegalStateException("heap is empty");
        int min = data[0];
        int last = data[--size];
        m.steps += 2;
        if (size == 0) return min;
        int i = 0;
        while (true) {
            int child = 2 * i + 1;
            if (child >= size) break;
            m.steps++;
            if (child + 1 < size) {
                m.steps++;
                m.comparisons++;
                if (data[child + 1] < data[child]) child++;
            }
            m.comparisons++;
            if (last <= data[child]) break;
            data[i] = data[child];
            m.moves++;
            i = child;
        }
        data[i] = last;
        return min;
    }

    public boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            if (data[(i - 1) / 2] > data[i]) return false;
        }
        return true;
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
            m.moves++;
        }
        data = bigger;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public Metrics metrics() { return m; }
}