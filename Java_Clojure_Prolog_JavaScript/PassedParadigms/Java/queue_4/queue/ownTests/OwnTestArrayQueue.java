package queue.ownTests;

import queue.ArrayQueue;

public class OwnTestArrayQueue<T> {
    public static <T> void main(String[] args) {
        ArrayQueue<String> queue1 = new ArrayQueue<>();
        ArrayQueue<String> queue2 = new ArrayQueue<>();
        fill(queue1, "first");
        fill(queue2, "second");
        dump(queue1);
        dump(queue2);
    }

    private static <T extends String> void fill(ArrayQueue<T> queue, T element) {
        for (int i = 0; i < 10; i++) {
            @SuppressWarnings("unchecked")
            T val = (T) (i + element.toString());
            queue.enqueue(val);
        }
    }

    private static <T extends String> void dump(ArrayQueue<T> queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    queue.size() + " " + queue.element() + " " + queue.dequeue()
            );
        }
    }
}