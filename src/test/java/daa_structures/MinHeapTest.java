package daa_structures;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import structures.MinHeap;

class MinHeapTest {
    @Test
    void emptyHeapThrows() {
        MinHeap h = new MinHeap();
        assertThrows(IllegalStateException.class, h::peekMin);
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void oneElement() {
        MinHeap h = new MinHeap();
        h.insert(5);
        assertEquals(5, h.peekMin());
        assertEquals(5, h.extractMin());
        assertTrue(h.isEmpty());
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void duplicates() {
        MinHeap h = new MinHeap();
        for (int i = 0; i < 20; i++) h.insert(4);
        for (int i = 0; i < 20; i++) assertEquals(4, h.extractMin());
    }

    @Test
    void heapPropertyAfterEveryOperation() {
        Random rnd = new Random(42);
        MinHeap h = new MinHeap();
        for (int i = 0; i < 1000; i++) {
            h.insert(rnd.nextInt(500));
            assertTrue(h.isValidHeap(), "after insert #" + i);
        }
        while (!h.isEmpty()) {
            h.extractMin();
            assertTrue(h.isValidHeap(), "after extractMin, size " + h.size());
        }
    }

    @Test
    void sortedOutput() {
        Random rnd = new Random(42);
        int n = 10_000;
        int[] values = new int[n];
        MinHeap h = new MinHeap();
        for (int i = 0; i < n; i++) {
            values[i] = rnd.nextInt();
            h.insert(values[i]);
        }
        Arrays.sort(values);
        for (int i = 0; i < n; i++) {
            assertEquals(values[i], h.peekMin());
            assertEquals(values[i], h.extractMin());
        }
    }
}