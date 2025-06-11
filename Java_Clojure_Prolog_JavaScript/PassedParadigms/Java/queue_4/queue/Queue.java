package queue;

// Model: a[0]..a[size - 1]
// Inv: size >= 0 && ∀ i = 0..size - 1: a[i] != null
// Let:
// 1) immutable(size) <<==>> ∀ i = 0..size - 1: a'[i] = a[i]
// 2) shift(size) <<==>> ∀ i = 0..size - 2: a'[i] = a[i + 1]
public interface Queue<T> {
    // Pred: req != null
    // Post: a[size] = req && size' = size + 1 && immutable(size)
    void enqueue(T req);

    // Pre: size > 0
    // Post: Res = a[0] && size' = size && immutable(size)
    T element();

    // Pre: size > 0
    // Post: Res = a[0] && size' = size - 1 && shift(size)
    T dequeue();

    // Pre: true
    // Post: Res = size && size' = size && immutable(size)
    int size();

    // Pre: true
    // Post: Res = (size == 0)  && size' = size && immutable(size)
    boolean isEmpty();

    // Pre: true
    // Post: size' = 0
    void clear();

    // Pred: size > 0 && 0 <= index < size
    // Post: R = a[i] && immutable(size) && size' = size
    T get(int index);

    // Pred: req != null && size > 0 && 0 <= index < size
    // Post: a'[index] = req && size' = size
    // && ∀ i = 0..index - 1: a'[i] = a[i]
    // && ∀ i = index + 1..size - 1: a'[i] = a[i]
    void set(int index, T req);

    // Pred: size >= 0
    // Post: size' = size && immutable(size)
    String toStr();
}