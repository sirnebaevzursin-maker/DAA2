package daa_structures;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import structures.IntList;

abstract class IntListContractTest {
    abstract IntList create();

    @Test
    void randomOperationsMatchArrayList() {
        Random rnd = new Random(42);
        IntList actual = create();
        List<Integer> expected = new ArrayList<>();
        for (int step = 0; step < 5000; step++) {
            int op = rnd.nextInt(5);
            if (op == 0) {
                int x = rnd.nextInt(100);
                actual.add(x);
                expected.add(x);
            } else if (op == 1) {
                int idx = rnd.nextInt(expected.size() + 1);
                int x = rnd.nextInt(100);
                actual.add(idx, x);
                expected.add(idx, x);
            } else if (op == 2 && !expected.isEmpty()) {
                int idx = rnd.nextInt(expected.size());
                assertEquals(expected.remove(idx), actual.remove(idx));
            } else if (op == 3 && !expected.isEmpty()) {
                int idx = rnd.nextInt(expected.size());
                assertEquals(expected.get(idx), actual.get(idx));
            } else {
                int x = rnd.nextInt(100);
                assertEquals(expected.contains(x), actual.contains(x));
            }
            assertEquals(expected.size(), actual.size());
        }
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    void emptyStructure() {
        IntList list = create();
        assertEquals(0, list.size());
        assertFalse(list.contains(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 5));
    }

    @Test
    void oneElement() {
        IntList list = create();
        list.add(0, 7);
        assertEquals(1, list.size());
        assertEquals(7, list.get(0));
        assertTrue(list.contains(7));
        assertEquals(7, list.remove(0));
        assertEquals(0, list.size());
        list.add(9); // список должен остаться рабочим после опустошения
        assertEquals(9, list.get(0));
    }

    @Test
    void duplicateValues() {
        IntList list = create();
        for (int i = 0; i < 5; i++) list.add(3);
        assertTrue(list.contains(3));
        list.remove(2);
        assertEquals(4, list.size());
        assertTrue(list.contains(3));
        for (int i = 0; i < 4; i++) list.remove(0);
        assertFalse(list.contains(3));
    }

    @Test
    void firstAndLastIndex() {
        IntList list = create();
        for (int i = 0; i < 25; i++) list.add(i);
        assertEquals(0, list.get(0));
        assertEquals(24, list.get(24));
        list.add(0, -1);
        list.add(list.size(), 99);
        assertEquals(-1, list.get(0));
        assertEquals(99, list.get(list.size() - 1));
        assertEquals(99, list.remove(list.size() - 1));
        assertEquals(-1, list.remove(0));
        assertEquals(24, list.get(list.size() - 1));
        list.add(100);
        assertEquals(100, list.get(list.size() - 1));
    }

    @Test
    void invalidIndexThrows() {
        IntList list = create();
        list.add(1);
        list.add(2);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, 0));
    }
}