package queue.ownTests;


import queue.ArrayQueueADT;

public class OwnTestArrayQueueADT<T> {
    public static void main(String[] args) {
        ArrayQueueADT<Integer> queue1 = ArrayQueueADT.create();
        ArrayQueueADT<Double> queue2 = ArrayQueueADT.create();
        fill(queue1, 2);
        fill(queue2, 1.0);
        dump(queue1);
        dump(queue2);

    }

    public static <T extends Number> void fill(ArrayQueueADT<T> queue, T inp) {
        for (int i = 0; i < 10; i++) {
            @SuppressWarnings("unchecked")
            T val = (T) Double.valueOf(inp.intValue() + Integer.valueOf(i));
            ArrayQueueADT.enqueue(queue, (T) val);
        }
    }

    public static <T> void dump(ArrayQueueADT<T> queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(
                    ArrayQueueADT.size(queue) + " " +
                            ArrayQueueADT.element(queue) + " " +
                            ArrayQueueADT.dequeue(queue)
            );
        }
    }

}