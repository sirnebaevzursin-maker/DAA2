package structures;

import metrics.Metrics;

public class DynamicArray implements IntList {
    private int[] data;
    private int size;
    private final Metrics m = new Metrics();

    public DynamicArray() {
        data = new int[10];
    }

    @Override
    public void add(int x) {
        if (size == data.length) grow();
        data[size++] = x;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        if (size == data.length) grow();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            m.moves++;
        }
        data[index] = x;
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        int removed = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            m.moves++;
        }
        size--;
        return removed;
    }

    /** Θ(1). */
    @Override
    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        m.steps++;
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            m.steps++;
            m.comparisons++;
            if (data[i] == x) return true;
        }
        return false;
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
            m.moves++; // копирование при росте тоже считаем перемещением
        }
        data = bigger;
    }

    @Override public int size() { return size; }
    @Override public Metrics metrics() { return m; }
}