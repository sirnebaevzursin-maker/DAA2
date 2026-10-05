package benchmark;

import metrics.Metrics;
import structures.IntList;
import structures.MinHeap;

import java.util.function.Supplier;

public class Workloads {

    public record Sample(long ns, long steps, long moves, long comparisons) {}

    public interface Run {
        Sample execute();
    }

    static volatile long sink;

    static Run w1(Supplier<IntList> f, int[] data, int[] idx) {
        return () -> {
            IntList l = fill(f, data);
            l.metrics().reset();
            long t = System.nanoTime();
            long sum = 0;
            for (int i : idx) sum += l.get(i);
            long dt = System.nanoTime() - t;
            sink += sum;
            return sample(dt, l.metrics());
        };
    }


    static Run w2(Supplier<IntList> f, int[] data, int[] queries) {
        return () -> {
            IntList l = fill(f, data);
            l.metrics().reset();
            long t = System.nanoTime();
            int found = 0;
            for (int q : queries) if (l.contains(q)) found++;
            long dt = System.nanoTime() - t;
            if (found != queries.length / 2) throw new IllegalStateException("W2: found=" + found);
            sink += found;
            return sample(dt, l.metrics());
        };
    }


    static Run w3(Supplier<IntList> f, int[] data, int[] values, int pos) {
        return () -> {
            IntList l = fill(f, data);
            l.metrics().reset();
            long t = System.nanoTime();
            for (int v : values) l.add(pos, v);
            long sum = 0;
            for (int i = 0; i < values.length; i++) sum += l.remove(pos);
            long dt = System.nanoTime() - t;
            sink += sum;
            return sample(dt, l.metrics());
        };
    }

    static Run w4(int[] data) {
        return () -> {
            MinHeap h = new MinHeap();
            long t = System.nanoTime();
            for (int v : data) h.insert(v);
            int prev = Integer.MIN_VALUE;
            for (int i = 0; i < data.length; i++) {
                int x = h.extractMin();
                if (x < prev) throw new IllegalStateException("W4: output not sorted");
                prev = x;
            }
            long dt = System.nanoTime() - t;
            sink += prev;
            Metrics m = h.metrics();
            return new Sample(dt, m.steps, m.moves, m.comparisons);
        };
    }

    static IntList fill(Supplier<IntList> f, int[] data) {
        IntList l = f.get();
        for (int v : data) l.add(v);
        return l;
    }

    static Sample sample(long ns, Metrics m) {
        return new Sample(ns, m.steps, m.moves, m.comparisons);
    }
}