package queue;

import java.util.Objects;


public class ArrayQueue<T> extends AbstractQueue<T>{
    private int head = -1;
    private T[] a = createNewArray(1);

    @Override
    public T get(int index) {
        return a[(getTail() + size - index - 1) % a.length];
    }

    @Override
    public void setImpl(int index, T req) {
        a[(getTail() + size - index - 1) % a.length] = req;
    }

    @Override
    public String toStrImpl() {
        int tail = getTail();
        StringBuilder strQueue = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            strQueue.append(a[(tail + i) % a.length]).append(", ");
        }
        strQueue.append(a[(tail + size - 1) % a.length]);
        return "[" + strQueue + "]";
    }

    @Override
    public void enqueueImpl(T req) {
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

    @Override
    public T elementImpl() {
        assert size > 0;

        return a[getTail()];
    }

    @Override
    public T dequeueImpl() {
        T peek = a[getTail()];
        size--;
        return peek;
    }

    @Override
    public void clear() {
        size = 0;
        head = -1;
    }

    // Pre: size > 0
    // Post: Res = 0 && size' = size && immutable(size)
    private int getTail() {
        return (head + a.length - size + 1) % a.length;
    }

    // Pred: true
    // Post: immutable(size) && size' = size + 1
    private void ensureCapacity() {
        int tail = getTail();
        T[] newA = createNewArray((size + 1) * 2);
        if (tail <= head) {
            System.arraycopy(a, tail, newA, 0,  head - tail + 1);
        } else {
            System.arraycopy(a, tail, newA, 0, size - tail);
            System.arraycopy(a, 0, newA, size - tail, head + 1);
        }
        a = newA;
    }

    // Pred: length >= 0
    // Post: R = T[length]
    private T[] createNewArray(int length) {
        @SuppressWarnings("unchecked")
        T[] newA = (T[]) new Object[length];
        return newA;
    }
}