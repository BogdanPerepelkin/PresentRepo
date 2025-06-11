package queue;

import base.Selector;
import base.TestCounter;

import java.util.List;
import java.util.function.Consumer;

import static queue.Queues.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class QueueTest {
    public static final Selector SELECTOR = new Selector(QueueTest.class)
            .variant("Base", variant(QueueModel.class, d -> () -> d))
            .variant("3233", ArrayQueueTest.variant(ToStrModel.class, d -> () -> d, TO_STR))
            .variant("3435", ArrayQueueTest.variant(IndexedToStrModel.class, (IndexedChecker<IndexedToStrModel>) d -> () -> d, INDEXED_TO_STR))
            .variant("3637", variant(FunctionsModel.class, d -> () -> d, FUNCTIONS))
            .variant("3839", variant(IfWhileModel.class, d -> () -> d, IF_WHILE))
            ;

    private QueueTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */ static <M extends QueueModel, T extends QueueChecker<M>> Consumer<TestCounter> variant(
            final Class<M> type,
            final T tester,
            final Queues.Splitter<M> splitter
    ) {
        return new QueueTester<>(type, tester, splitter)::test;
    }

    /* package-private */ static <M extends QueueModel, T extends QueueChecker<M>> Consumer<TestCounter> variant(
            final Class<M> type,
            final T tester
    ) {
        return variant(type, tester, (t, q, r) -> List.of());
    }
}
