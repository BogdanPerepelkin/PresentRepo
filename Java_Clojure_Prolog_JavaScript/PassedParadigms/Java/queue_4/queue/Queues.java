package queue;

import base.ExtendedRandom;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Queues {
    private Queues() {
    }

    protected interface QueueModel {
        @ReflectionTest.Ignore
        ArrayDeque<Object> model();

        default Object dequeue() {
            return model().removeFirst();
        }

        default int size() {
            return model().size();
        }

        default boolean isEmpty() {
            return model().isEmpty();
        }

        default void clear() {
            model().clear();
        }

        default void enqueue(final Object element) {
            model().addLast(element);
        }

        default Object element() {
            return model().getFirst();
        }
    }

    protected interface QueueChecker<M extends QueueModel> {
        M wrap(ArrayDeque<Object> reference);

        default List<M> linearTest(final M queue, final ExtendedRandom random) {
            // Do nothing by default
            return List.of();
        }

        default void check(final M queue, final ExtendedRandom random) {
            queue.element();
        }

        default void add(final M queue, final Object element, final ExtendedRandom random) {
            queue.enqueue(element);
        }

        default Object randomElement(final ExtendedRandom random) {
            return ArrayQueueTester.ELEMENTS[random.nextInt(ArrayQueueTester.ELEMENTS.length)];
        }

        default void remove(final M queue, final ExtendedRandom random) {
            queue.dequeue();
        }

        @SuppressWarnings("unchecked")
        default M cast(final QueueModel model) {
            return (M) model;
        }
    }

    @FunctionalInterface
    protected interface Splitter<M extends QueueModel> {
        List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);
    }

    @FunctionalInterface
    /* package-private */ interface LinearTester<M extends QueueModel> extends Splitter<M> {
        void test(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);

        @Override
        default List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random) {
            test(tester, queue, random);
            return List.of();
        }
    }


    // === Reflection

    /* package-private */ interface ReflectionModel extends QueueModel {
        Field ELEMENTS = getField("elements");
        Field HEAD = getField("head");

        @SuppressWarnings("unchecked")
        private <Z> Z get(final Field field) {
            try {
                return (Z) field.get(model());
            } catch (final IllegalAccessException e) {
                throw new AssertionError("Cannot access field " + field.getName() + ": " + e.getMessage(), e);
            }
        }

        private static Field getField(final String name) {
            try {
                final Field field = ArrayDeque.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (final NoSuchFieldException e) {
                throw new AssertionError("Reflection error: " + e.getMessage(), e);
            }
        }

        @ReflectionTest.Ignore
        default int head() {
            return get(HEAD);
        }

        @ReflectionTest.Ignore
        default Object[] elements() {
            return get(ELEMENTS);
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Predicate<Object> p, final BiFunction<R, Integer, R> f) {
            final int size = size();
            final Object[] elements = elements();
            final int head = head();
            R result = zero;
            for (int i = 0; i < size; i++) {
                if (p.test(elements[(head + i) % elements.length])) {
                    result = f.apply(result, i);
                }
            }
            return result;
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Object v, final BiFunction<R, Integer, R> f) {
            return reduce(zero, o -> Objects.equals(v, o), f);
        }
    }


    // === Indexed

    /* package-private */ interface IndexedChecker<M extends IndexedModel> extends Queues.QueueChecker<M> {
        @Override
        default void check(final M queue, final ExtendedRandom random) {
            Queues.QueueChecker.super.check(queue, random);
            queue.get(randomIndex(queue, random));
        }

        @Override
        default void add(final M queue, final Object element, final ExtendedRandom random) {
            if (queue.isEmpty() || random.nextBoolean()) {
                Queues.QueueChecker.super.add(queue, element, random);
            } else {
                final int index = randomIndex(queue, random);
                queue.set(index, randomElement(random));
            }
        }

        private static int randomIndex(final Queues.QueueModel queue, final ExtendedRandom random) {
            return random.nextInt(queue.size());
        }
    }

    /* package-private */ interface IndexedModel extends ReflectionModel {
        default Object get(final int index) {
            final Object[] elements = elements();
            return elements[(head() + size() - 1 - index) % elements.length];
        }

        default void set(final int index, final Object value) {
            final Object[] elements = elements();
            elements[(head() + size() - 1 - index) % elements.length] = value;
        }
    }


    // === ToStr

    /* package-private */ interface ToStrModel extends QueueModel {
        @SuppressWarnings("UnusedReturnValue")
        default String toStr() {
            return model().toString();
        }
    }

    /* package-private */ static final LinearTester<ToStrModel> TO_STR = (tester, queue, random) -> queue.toStr();


    // === IndexedToStr

    /* package-private */ interface IndexedToStrModel extends IndexedModel, ToStrModel {}
    /* package-private */ static final LinearTester<IndexedToStrModel> INDEXED_TO_STR = TO_STR::test;

    // === Deque

    /* package-private */ interface DequeModel extends QueueModel {
        default void push(final Object element) {
            model().addFirst(element);
        }

        @SuppressWarnings("UnusedReturnValue")
        default Object peek() {
            return model().getLast();
        }

        default Object remove() {
            return model().removeLast();
        }
    }

    /* package-private */ interface DequeChecker<M extends DequeModel> extends QueueChecker<M> {
        @Override
        default void add(final M queue, final Object element, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.add(queue, element, random);
            } else {
                queue.push(element);
            }
        }

        @Override
        default void check(final M queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.check(queue, random);
            } else {
                queue.peek();
            }
        }

        @Override
        default void remove(final M queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.remove(queue, random);
            } else {
                queue.remove();
            }
        }
    }


    // === 3637

    /* package-private */ interface DequeIndexedModel extends DequeModel, IndexedModel {}

    /* package-private */ interface DequeIndexedChecker<M extends DequeIndexedModel> extends
            DequeChecker<M>,
            IndexedChecker<M>
    {
        @Override
        default void check(final M queue, final ExtendedRandom random) {
            DequeChecker.super.check(queue, random);
            IndexedChecker.super.check(queue, random);
        }

        @Override
        default void add(final M queue, final Object element, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                DequeChecker.super.add(queue, element, random);
            } else {
                IndexedChecker.super.add(queue, element, random);
            }
        }
    }
    
    // === 3839
    /* package-private */ interface DequeIndexedToStrModel extends DequeIndexedModel, ToStrModel {}
    /* package-private */ static final LinearTester<DequeIndexedToStrModel> DEQUE_INDEXED_TO_STR = TO_STR::test;


    // === 3637

    /* package-private */  interface FunctionsModel extends Queues.QueueModel {
        @ReflectionTest.Wrap
        default FunctionsModel filter(final Predicate<Object> p) {
            return apply(Stream::filter, p);
        }

        @ReflectionTest.Wrap
        default FunctionsModel map(final Function<Object, Object> f) {
            return apply(Stream::map, f);
        }

        private <T> FunctionsModel apply(final BiFunction<Stream<Object>, T, Stream<Object>> f, final T v) {
            final ArrayDeque<Object> deque = f.apply(model().stream(), v).collect(Collectors.toCollection(ArrayDeque::new));
            return () -> deque;
        }
    }

    /* package-private */ static final Queues.Splitter<FunctionsModel> FUNCTIONS = (tester, queue, random) -> {
        final FunctionsModel result = random.nextBoolean()
                ? queue.filter(random.nextBoolean() ? Predicate.isEqual(tester.randomElement(random)) : Predicate.isEqual(tester.randomElement(random)).negate())
                : queue.map(random.nextBoolean() ? String::valueOf : Object::hashCode);
        return List.of(tester.cast(result));
    };


    // === 3839

    /* package-private */ static Predicate<Object> randomPredicate(final Queues.QueueChecker<? extends Queues.QueueModel> tester, final ExtendedRandom random) {
        return new Predicate<>() {
            final Object element = tester.randomElement(random);

            @Override
            public boolean test(final Object o) {
                return o == element;
            }

            @Override
            public String toString() {
                return "== " + element;
            }
        };
    }

    /* package-private */ static final Queues.LinearTester<IfWhileModel> IF = (tester, queue, random) -> {
        if (random.nextBoolean()) {
            queue.removeIf(randomPredicate(tester, random));
        } else {
            queue.retainIf(randomPredicate(tester, random));
        }
    };


    // === While

    /* package-private */ static final Queues.LinearTester<IfWhileModel> WHILE = (tester, queue, random) -> {
        if (random.nextBoolean()) {
            queue.takeWhile(randomPredicate(tester, random));
        } else {
            queue.dropWhile(randomPredicate(tester, random));
        }
    };


    // === IfWhile

    /* package-private */ interface IfWhileModel extends QueueModel {
        default void removeIf(final Predicate<Object> p) {
            model().removeIf(p);
        }

        default void retainIf(final Predicate<Object> p) {
            model().removeIf(Predicate.not(p));
        }
        // Deliberately ugly implementation
        default void dropWhile(final Predicate<Object> p) {
            final boolean[] remove = {true};
            model().removeIf(e -> remove[0] &= p.test(e));
        }

        // Deliberately ugly implementation
        default void takeWhile(final Predicate<Object> p) {
            final boolean[] keep = {true};
            model().removeIf(e -> !(keep[0] &= p.test(e)));
        }
    }

    /* package-private */ static final Queues.LinearTester<IfWhileModel> IF_WHILE = (tester, queue, random) -> {
        final Predicate<Object> predicate = randomPredicate(tester, random);
        final Queues.LinearTester<IfWhileModel> test = random.randomItem(
                (t, q, r) -> q.removeIf(predicate),
                (t, q, r) -> q.retainIf(predicate),
                (t, q, r) -> q.dropWhile(predicate),
                (t, q, r) -> q.takeWhile(predicate)
        );
        test.test(tester, queue, random);
    };
}
