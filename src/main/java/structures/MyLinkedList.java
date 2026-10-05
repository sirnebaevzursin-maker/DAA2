package structures;

import metrics.Metrics;

public class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics m = new Metrics();

    @Override
    public void add(int x) {
        Node node = new Node(x);
        if (head == null) {
            head = node;
            tail = node;
            m.moves += 2;
        } else {
            tail.next = node;
            tail = node;
            m.moves += 2;
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        if (index == size) { add(x); return; }
        Node node = new Node(x);
        if (index == 0) {
            node.next = head;
            head = node;
            m.moves += 2;
        } else {
            Node prev = nodeAt(index - 1);
            node.next = prev.next;
            prev.next = node;
            m.moves += 2;
        }
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        int removed;
        if (index == 0) {
            removed = head.value;
            head = head.next;
            m.moves++;
            if (head == null) { tail = null; m.moves++; }
        } else {
            Node prev = nodeAt(index - 1);
            Node target = prev.next;
            removed = target.value;
            prev.next = target.next;
            m.moves++;
            if (target == tail) { tail = prev; m.moves++; }
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        return nodeAt(index).value;
    }

    @Override
    public boolean contains(int x) {
        Node cur = head;
        while (cur != null) {
            m.comparisons++;
            if (cur.value == x) return true;
            cur = cur.next;
            m.steps++;
        }
        return false;
    }

    private Node nodeAt(int index) {
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
            m.steps++;
        }
        return cur;
    }

    @Override public int size() { return size; }
    @Override public Metrics metrics() { return m; }
}