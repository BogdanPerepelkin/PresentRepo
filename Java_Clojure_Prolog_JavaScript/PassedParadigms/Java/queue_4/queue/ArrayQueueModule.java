package queue;

import java.util.Objects;


// Model: a[0]..a[size - 1]
// Inv: size >= 0 && ∀ i = 0..size - 1: a[i] != null
// Let:
// 1) immutable(size) <<==>> ∀ i = 0..size - 1: a'[i] = a[i]
// 2) shift(size) <<==>> ∀ i = 0..size - 2: a'[i] = a[i + 1]
public class ArrayQueueModule {
    private static int head = -1;
    private static int size = 0;
    private static Object[] a = new Object[1];

    // Pred: size > 0 && 0 <= index < size
    // Post: R = a[i] && immutable(size) && size' = size
    public static Object get(int index) {
        return  a[(getTail() + size - index - 1) % a.length];
    }

    // Pred: obj != null && size > 0 && 0 <= index < size
    // Post: a'[index] = obj && size' = size
    // && ∀ i = 0..index - 1: a'[i] = a[i]
    // && ∀ i = index + 1..size - 1: a'[i] = a[i]
    public static void set(int index, Object obj) {
        Objects.requireNonNull(obj);
        a[(getTail() + size - index - 1) % a.length] = obj;
    }

    // Pred: size >= 0
    // Post: size' = size && immutable(size)
    public static String toStr() {
        int tail = getTail();
        StringBuilder strQueue = new StringBuilder();
        if (size == 0) {
            return "[]";
        }
        for (int i = 0; i < size - 1; i++) {
            strQueue.append(a[(tail + i) % a.length]).append(", ");
        }
        strQueue.append(a[(tail + size - 1) % a.length]);
        return "[" + strQueue + "]";
    }

    // Pred: req != null
    // Post: a[size] = req && size' = size + 1 && immutable(size)
    public static void enqueue(Object req) {
        Objects.requireNonNull(req);
        if (size == a.length) {
            ensureCapacity();
            head = size;
        } else {
            head = (head + 1) % a.length;
        }
        a[head] = req;
        size++;
    }

    // Pre: size > 0
    // Post: Res = a[0] && size' = size && immutable(size)
    public static Object element() {
        assert size > 0;
        return a[getTail()];
    }

    // Pre: size > 0
    // Post: Res = a[0] && size' = size - 1 && shift(size)
    public static Object dequeue() {
        assert size > 0;
        Object peek = a[getTail()];
        size--;
        return peek;
    }

    // Pre: true
    // Post: Res = size && size' = size && immutable(size)
    public static Object size() {
        return size;
    }

    // Pre: true
    // Post: Res = (size == 0)  && size' = size && immutable(size)
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pre: true
    // Post: size' = 0
    public static void clear() {
        size = 0;
        head = -1;
    }

    // Pre: size > 0
    // Post: Res = 0 && size' = size && immutable(size)
    private static int getTail() {
        return (head + a.length - size + 1) % a.length;
    }

    // Pred: true
    // Post: immutable(size) && size' = size + 1
    private static void ensureCapacity() {
        int tail = getTail();
        Object[] newA = new Object[(size + 1) * 2];
        if (tail <= head) {
            System.arraycopy(a, tail, newA, 0,  head - tail + 1);
        } else {
            System.arraycopy(a, tail, newA, 0, size - tail);
            System.arraycopy(a, 0, newA, size - tail, head + 1);
        }
        a = newA;
    }
}