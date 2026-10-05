package daa_structures; // должно совпадать с папкой, в которой лежит файл

import static org.junit.jupiter.api.Assertions.*;

import metrics.Metrics;
import org.junit.jupiter.api.Test;
import structures.DynamicArray;
import structures.IntList;
import structures.MinHeap;
import structures.MyLinkedList;

class MetricsTest {

    private static IntList filled(IntList l, int n) {
        for (int i = 0; i < n; i++) l.add(i);
        l.metrics().reset();
        return l;
    }

    @Test
    void newMetricsAreZeroAndResetClearsEverything() {
        Metrics m = new Metrics();
        assertEquals(0, m.steps);
        assertEquals(0, m.moves);
        assertEquals(0, m.comparisons);
        m.steps = 5;
        m.moves = 6;
        m.comparisons = 7;
        m.reset();
        assertEquals(0, m.steps);
        assertEquals(0, m.moves);
        assertEquals(0, m.comparisons);
    }

    @Test
    void arrayContainsAbsentValueScansEverything() {
        IntList a = filled(new DynamicArray(), 50);
        assertFalse(a.contains(-1));
        assertEquals(50, a.metrics().steps);
        assertEquals(50, a.metrics().comparisons);
    }

    @Test
    void listContainsAbsentValueScansEverything() {
        IntList l = filled(new MyLinkedList(), 50);
        assertFalse(l.contains(-1));
        assertEquals(50, l.metrics().steps);
        assertEquals(50, l.metrics().comparisons);
    }

    @Test
    void arrayRemoveFromHeadShiftsAllOthers() {
        IntList a = filled(new DynamicArray(), 20);
        a.remove(0);
        assertEquals(19, a.metrics().moves);
    }

    @Test
    void listHeadInsertCountsLinkUpdatesButNoSteps() {
        IntList l = filled(new MyLinkedList(), 20);
        l.add(0, 99);
        assertEquals(2, l.metrics().moves);
        assertEquals(0, l.metrics().steps);
    }

    @Test
    void listMiddleOperationsNeverReportZero() {
        IntList l = filled(new MyLinkedList(), 100);
        l.add(50, 99);
        l.remove(50);
        assertTrue(l.metrics().steps > 0, "steps must be counted in the list");
        assertTrue(l.metrics().moves > 0, "link updates must be counted in the list");
    }

    @Test
    void heapInsertOfIncreasingValuesDoesNotMoveAnything() {
        MinHeap h = new MinHeap();
        for (int i = 1; i <= 8; i++) h.insert(i);
        assertEquals(0, h.metrics().moves);
        assertEquals(7, h.metrics().comparisons);
    }

    @Test
    void heapExtractMinCountsWork() {
        MinHeap h = new MinHeap();
        for (int i = 1; i <= 100; i++) h.insert(i);
        h.metrics().reset();
        h.extractMin();
        assertTrue(h.metrics().comparisons > 0);
        assertTrue(h.metrics().moves > 0);
        assertTrue(h.metrics().steps > 0);
    }
}