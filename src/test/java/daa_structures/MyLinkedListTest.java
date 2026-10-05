package daa_structures;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import structures.IntList;
import structures.MyLinkedList;

class MyLinkedListTest extends IntListContractTest {
    @Override
    IntList create() { return new MyLinkedList(); }

    @Test
    void getCostsIndexSteps() {
        IntList l = create();
        for (int i = 0; i < 100; i++) l.add(i);
        l.metrics().reset();
        l.get(77);
        assertEquals(77, l.metrics().steps);
    }

    @Test
    void middleInsertCountsLinkUpdates() {
        IntList l = create();
        for (int i = 0; i < 10; i++) l.add(i);
        l.metrics().reset();
        l.add(5, 99);
        assertEquals(4, l.metrics().steps);
        assertEquals(2, l.metrics().moves);
    }
}