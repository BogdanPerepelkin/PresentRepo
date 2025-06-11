package queue;


import java.util.Objects;

public abstract class AbstractQueue<T> implements Queue<T>{
    protected int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0;
    }

    public abstract void enqueueImpl(T req);

    @Override
    public void enqueue(T req) {
        Objects.requireNonNull(req);
        enqueueImpl(req);
    }

    public abstract T elementImpl();

    @Override
    public T element() {
        assert size > 0;
        return elementImpl();
    }

    public abstract T dequeueImpl();

    @Override
    public T dequeue() {
        assert size > 0;
        return dequeueImpl();
    }

    @Override
    public abstract T get(int index);

    public abstract void setImpl(int index, T req);

    @Override
    public void set(int index, T req) {
        Objects.requireNonNull(req);
        setImpl(index, req);
    }

    public abstract String toStrImpl();

    @Override
    public String toStr() {
        if (size == 0) {
            return "[]";
        }
        return toStrImpl();
    }
}