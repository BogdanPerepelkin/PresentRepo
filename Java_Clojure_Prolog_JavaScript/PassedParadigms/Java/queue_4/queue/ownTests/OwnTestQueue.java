package queue.ownTests;

import queue.AbstractQueue;
import queue.ArrayQueue;
import queue.LinkedQueue;
import queue.Queue;

public class OwnTestQueue<T> {
    public static void main(String[] args) {
        LinkedQueue<String> linkedQueue = new LinkedQueue<>();
        ArrayQueue<String> arrayQueue = new ArrayQueue<>();
        fill(arrayQueue, "-array");
        fill(linkedQueue, "-linked");
        dump(arrayQueue);
        dump(linkedQueue);

    }

    private static <T> void fill(Queue<T> queue, String  element) {
        for (int i = 0; i < 5; i++) {
            @SuppressWarnings("unchecked")
            T val = (T) (i + element);
            queue.enqueue(val);
        }
    }

    private static <T> void dump(Queue<T> queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    queue.size() + " " + queue.element() + " " + queue.dequeue()
            );
        }
    }
}