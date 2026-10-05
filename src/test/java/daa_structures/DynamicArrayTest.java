package daa_structures;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import structures.DynamicArray;
import structures.IntList;

class DynamicArrayTest extends IntListContractTest {
    @Override
    IntList create() { return new DynamicArray(); }

    @Test
    void getCostsOneStep() {
        IntList a = create();
        for (int i = 0; i < 100; i++) a.add(i);
        a.metrics().reset();
        a.get(77);
        assertEquals(1, a.metrics().steps);
    }

    @Test
    void insertAtHeadMovesEveryElement() {
        IntList a = create();
        for (int i = 0; i < 8; i++) a.add(i);
        a.metrics().reset();
        a.add(0, 99);
        assertEquals(8, a.metrics().moves);
    }
}