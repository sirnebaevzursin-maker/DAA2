package structures;
import metrics.Metrics;
public interface IntList {
    void add(int x);
    void add(int index, int x);   // 0 <= index <= size, иначе IndexOutOfBoundsException
    int remove(int index);        // 0 <= index < size, иначе IndexOutOfBoundsException
    int get(int index);           // 0 <= index < size, иначе IndexOutOfBoundsException
    boolean contains(int x);
    int size();
    Metrics metrics();
}
