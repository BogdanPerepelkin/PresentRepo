package queue;

import java.util.Objects;


public class LinkedQueue<T> extends AbstractQueue<T>{
    private SingleNode<T> begin;

    @Override
    public T get(int index) {
        SingleNode<T> cur = begin;
        for(int i = 0; i < (size - index) % size; i++) {
            cur = cur.getFollow();
        }
        return cur.getValue();
    }

    @Override
    public void setImpl(int index, T req) {
        SingleNode<T> cur = begin;
        for(int i = 0; i < (size - index) % size; i++) {
            cur = cur.getFollow();
        }
        cur.setValue(req);
    }

    @Override
    public String toStrImpl() {
        SingleNode<T> cur = begin.getFollow();
        StringBuilder strQueue = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            strQueue.append(cur.getValue()).append(", ");
            cur = cur.getFollow();
        }
        strQueue.append(cur.getValue());
        return "[" + strQueue + "]";
    }

    @Override
    public void enqueueImpl(T req) {
        Objects.requireNonNull(req);

        if (size == 0) {
            begin = new SingleNode<>(null, req);
            begin.setFollow(begin);
        } else {
            SingleNode<T> newBegin = new SingleNode<>(begin.getFollow(), req);
            begin.setFollow(newBegin);
            begin = newBegin;
        }
        size++;
    }

    @Override
    public T elementImpl() {
        return begin.getFollow().getValue();
    }

    @Override
    public T dequeueImpl() {
        T peek = begin.getFollow().getValue();
        begin.setFollow(begin.getFollow().getFollow());
        size--;
        return peek;
    }

    @Override
    public void clear() {
        size = 0;
        begin = null;
    }
}