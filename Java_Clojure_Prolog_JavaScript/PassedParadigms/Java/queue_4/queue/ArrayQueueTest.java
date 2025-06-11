package queue;

import base.Selector;
import base.TestCounter;

import java.util.List;
import java.util.function.Consumer;

import static queue.Queues.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ArrayQueueTest {
    public static final Selector SELECTOR = new Selector(ArrayQueueTest.class)
            .variant("Base", variant(QueueModel.class, d -> () -> d))
            .variant("3233", variant(IndexedModel.class, (IndexedChecker<IndexedModel>) d -> () -> d))
            .variant("3435", variant(IndexedToStrModel.class, (IndexedChecker<IndexedToStrModel>) d -> () -> d, INDEXED_TO_STR))
            .variant("3637", variant(DequeIndexedModel.class, (DequeIndexedChecker<DequeIndexedModel>) d -> () -> d))
            .variant("3839", variant(DequeIndexedToStrModel.class, (DequeIndexedChecker<DequeIndexedToStrModel>) d -> () -> d, DEQUE_INDEXED_TO_STR))
            ;

    private ArrayQueueTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */
    static <M extends QueueModel> Consumer<TestCounter> variant(
            final Class<M> type,
            final QueueChecker<M> tester,
            final Splitter<M> splitter
    ) {
        return new ArrayQueueTester<>(type, tester, splitter)::test;
    }

    /* package-private */
    static <M extends QueueModel> Consumer<TestCounter> variant(
            final Class<M> type,
            final QueueChecker<M> tester
    ) {
        return variant(type, tester, (t, q, r) -> List.of());
    }
}
