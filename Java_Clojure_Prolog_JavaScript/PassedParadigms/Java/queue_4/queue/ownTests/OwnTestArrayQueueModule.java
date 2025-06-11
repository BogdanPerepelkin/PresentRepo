package queue.ownTests;

import queue.ArrayQueueModule;

public class OwnTestArrayQueueModule {
    public static void main(String[] args) {
        fillQueue();
        dumpQueue();
        System.out.println(ArrayQueueModule.size());
    }

    public static void fillQueue() {
        for (int i = 1; i < 10; i++) {
            ArrayQueueModule.enqueue(i);
        }
    }

    public static void dumpQueue() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(
                    ArrayQueueModule.size() + " " +
                            ArrayQueueModule.element() + " " +
                            ArrayQueueModule.dequeue() + " "
            );
        }
    }
}