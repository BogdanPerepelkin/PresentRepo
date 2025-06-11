package queue;

public class SingleNode<T> {
    private SingleNode<T> follow;
    private T value;

    public SingleNode(final SingleNode<T> follow, final T value) {
        this.follow = follow;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public SingleNode<T> getFollow() {
        return follow;
    }

    public void setFollow(SingleNode<T> newFollow) {
        follow = newFollow;
    }

    public void setValue(T req) {
        value = req;
    }
}