package benchmark;

import metrics.Metrics;
import structures.DynamicArray;
import structures.IntList;
import structures.MinHeap;
import structures.MyLinkedList;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;


public class Benchmark {
    static final long SEED = 42;
    static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    static final int WARMUP = 3;
    static final int RUNS = 5;
    static final int GETS = 10_000;
    static final int SEARCHES = 1_000;
    static final int UPDATES = 1_000;

    static volatile long sink;

    record Sample(long ns, long steps, long moves, long comparisons) {}

    interface Run { Sample execute(); }

    public static void main(String[] args) throws IOException {
        Path out = Paths.get(args.length > 0 ? args[0] : "results/results.csv");
        if (out.getParent() != null) Files.createDirectories(out.getParent());

        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                Random rnd = new Random(SEED);
                int[] data = new int[n];
                for (int i = 0; i < n; i++) data[i] = rnd.nextInt(n) * 2;
                int[] getIdx = new int[GETS];
                for (int i = 0; i < GETS; i++) getIdx[i] = rnd.nextInt(n);
                int[] queries = new int[SEARCHES];
                for (int i = 0; i < SEARCHES; i++) {
                    queries[i] = (i % 2 == 0) ? data[rnd.nextInt(n)] : rnd.nextInt(n) * 2 + 1;
                }
                int[] updates = new int[UPDATES];
                for (int i = 0; i < UPDATES; i++) updates[i] = rnd.nextInt();

                Supplier<IntList> array = DynamicArray::new;
                Supplier<IntList> list = MyLinkedList::new;
                String[] names = {"DynamicArray", "MyLinkedList"};
                Supplier<IntList>[] factories = new Supplier[]{array, list};

                for (int s = 0; s < 2; s++) {
                    write(w, "W1", "-", names[s], n, measure(w1(factories[s], data, getIdx)));
                    write(w, "W2", "-", names[s], n, measure(w2(factories[s], data, queries)));
                    write(w, "W3", "head", names[s], n, measure(w3(factories[s], data, updates, 0)));
                    write(w, "W3", "middle", names[s], n, measure(w3(factories[s], data, updates, n / 2)));
                }
                write(w, "W4", "-", "MinHeap", n, measure(w4(data)));
                w.flush();
                System.out.println("done n=" + n);
            }
        }
        System.out.println("saved " + out);
    }


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

    static Sample measure(Run r) {
        for (int i = 0; i < WARMUP; i++) r.execute();
        Sample[] s = new Sample[RUNS];
        for (int i = 0; i < RUNS; i++) s[i] = r.execute();
        Arrays.sort(s, Comparator.comparingLong(Sample::ns));
        return s[RUNS / 2];
    }

    static void write(PrintWriter w, String workload, String variant, String structure, int n, Sample s) {
        w.println(String.format(Locale.ROOT, "%s,%s,%s,%d,%.4f,%d,%d,%d",
                workload, variant, structure, n, s.ns() / 1_000_000.0, s.steps(), s.moves(), s.comparisons()));
    }
}