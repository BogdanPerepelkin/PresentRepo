package queue;

import java.util.Objects;


// Model: a[0]..a[size - 1]
// Inv: size >= 0 && ∀ i = 0..size - 1: a[i] != null
// Let:
// 1) immutable(size) <<==>> ∀ i = 0..size - 1: a'[i] = a[i]
// 2) shift(size) <<==>> ∀ i = 0..size - 2: a'[i] = a[i + 1]
public class ArrayQueueADT<T> {
    private int head = -1;
    private int size = 0;
    private T[] a = createNewArray(1);

    public ArrayQueueADT() {
    }

    // Pred: size > 0 && 0 <= index < size
    // Post: R = a[i] && immutable(size) && size' = size
    public static <T> T get(ArrayQueueADT<T> queue, int index) {
        return  queue.a[(getTail(queue) + queue.size - index - 1) % queue.a.length];
    }

    // Pred: obj != null && size > 0 && 0 <= index < size
    // Post: a'[index] = obj && size' = size
    // && ∀ i = 0..index - 1: a'[i] = a[i]
    // && ∀ i = index + 1..size - 1: a'[i] = a[i]
    public static <T> void set(ArrayQueueADT<T> queue, int index, T obj) {
        Objects.requireNonNull(obj);
        queue.a[(getTail(queue) + queue.size - index - 1) % queue.a.length] = obj;
    }

    // Pred: size >= 0
    // Post: size' = size && immutable(size)
    public static <T> String toStr(ArrayQueueADT<T> queue) {
        int tail = getTail(queue);
        StringBuilder strQueue = new StringBuilder();
        if (queue.size == 0) {
            return "[]";
        }
        for (int i = 0; i < queue.size - 1; i++) {
            strQueue.append(queue.a[(tail + i) % queue.a.length]).append(", ");
        }
        strQueue.append(queue.a[(tail + queue.size - 1) % queue.a.length]);
        return "[" + strQueue + "]";
    }

    // Pred: req != null
    // Post: a[size] = req && size' = size + 1 && immutable(size)
    public static <T> ArrayQueueADT<T> create() {
        ArrayQueueADT<T> queue = new ArrayQueueADT<>();

        queue.a = createNewArray(1);
        return queue;
    }

    // Pred: req != null
    // Post: a[size] = req && size' = size + 1 && immutable(size)
    public static <T> void enqueue(ArrayQueueADT<T> queue, T req) {
        Objects.requireNonNull(req);

        if (queue.size == queue.a.length) {
            ensureCapacity(queue);
            queue.head = queue.size;
        } else {
            queue.head = (queue.head + 1) % queue.a.length;
        }
        queue.a[queue.head] = req;
        queue.size++;
    }

    // Pre: size > 0
    // Post: Res = a[0] && size' = size && immutable(size)
    public static <T> T element(ArrayQueueADT<T> queue) {
        assert queue.size > 0;

        return queue.a[getTail(queue)];
    }

    // Pre: size > 0
    // Post: Res = a[0] && size' = size - 1 && shift(size)
    public static <T> T dequeue(ArrayQueueADT<T> queue) {
        assert queue.size > 0;

        T peek = queue.a[getTail(queue)];
        queue.size--;
        return peek;
    }

    // Pre: true
    // Post: Res = size && size' = size && immutable(size)
    public static <T> int size(ArrayQueueADT<T> queue) {
        return queue.size;
    }

    // Pre: true
    // Post: Res = (size == 0)  && size' = size && immutable(size)
    public static <T> boolean isEmpty(ArrayQueueADT<T> queue) {
        return queue.size == 0;
    }

    // Pre: true
    // Post: size' = 0
    public static <T> void clear(ArrayQueueADT<T> queue) {
        queue.size = 0;
        queue.head = -1;
    }

    // Pre: size > 0
    // Post: Res = 0 && size' = size && immutable(size)
    private static <T> int getTail(ArrayQueueADT<T> queue) {
        return (queue.head + queue.a.length - queue.size + 1) % queue.a.length;
    }

    // Pred: true
    // Post: immutable(size) && size' = size + 1
    private static <T> void ensureCapacity(ArrayQueueADT<T> queue) {
        int tail = getTail(queue);

        T[] newA = createNewArray((queue.size + 1) * 2);
        if (tail <= queue.head) {
            System.arraycopy(queue.a, tail, newA, 0,  queue.head - tail + 1);
        } else {
            System.arraycopy(queue.a, tail, newA, 0, queue.size - tail);
            System.arraycopy(queue.a, 0, newA, queue.size - tail, queue.head + 1);
        }
        queue.a = newA;
    }

    // Pred: length >= 0
    // Post: R = T[length]
    private static <T> T[] createNewArray(int length) {
        @SuppressWarnings("unchecked")
        T[] newA = (T[]) new Object[length];
        return newA;
    }
}