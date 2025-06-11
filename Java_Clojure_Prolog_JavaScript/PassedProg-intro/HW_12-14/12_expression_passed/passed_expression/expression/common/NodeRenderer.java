package expression.common;

import base.ExtendedRandom;
import base.Functional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public class NodeRenderer<C> {
    public static final Mode MINI_MODE = Mode.SIMPLE_MINI; // Replace by TRUE_MINI for some challenge;
    private static final String PAREN = "[";
    private static final List<Paren> DEFAULT_PARENS = List.of(new Paren("(", ")"));

    public static final Settings FULL = Mode.FULL.settings(0);
    public static final Settings FULL_EXTRA = Mode.FULL.settings(Integer.MAX_VALUE / 4);
    public static final Settings SAME = Mode.SAME.settings(0);
    public static final Settings MINI = MINI_MODE.settings(0);
    public static final Settings TRUE_MINI = Mode.TRUE_MINI.settings(0);

    private final Renderer<C, Settings, Node<C>> nodeRenderer = new Renderer<>(Node::constant);
    private final Map<String, Priority> priorities = new HashMap<>();
    private final Map<String, String> brackets = new HashMap<>();
    private final ExtendedRandom random;

    public NodeRenderer(final ExtendedRandom random) {
        this.random = random;
        nodeRenderer.unary(PAREN, (mode, arg) -> paren(true, arg));
    }

    public static Paren paren(final String open, final String close) {
        return new Paren(open, close);
    }

    public void unary(final String name, final int priority) {
        final String space = name.equals("-") || Character.isLetter(name.charAt(0)) ? " " : "";
        nodeRenderer.unary(
                name,
                (settings, arg) -> settings.extra(Node.op(name, priority, inner(settings, priority, arg, space)), random)
        );
    }

    public void unary(final String left, final String right) {
        brackets.put(left, right);
        nodeRenderer.unary(
                left,
                (settings, arg) -> settings.extra(Node.op(left, Integer.MAX_VALUE, arg), random)
        );
    }

    private Node<C> inner(final Settings settings, final int priority, final Node<C> arg, final String space) {
        if (settings.mode == Mode.FULL) {
            return paren(true, arg);
        } else {
            final String op = arg.get(
                    c -> space,
                    n -> space,
                    (n, p, a) ->
                            priority > unaryPriority(arg) ? PAREN :
                            PAREN.equals(n) ? "" :
                            space,
                    (n, a, b) -> PAREN
            );
            return op.isEmpty() ? arg : Node.op(op, Priority.MAX.priority | 1, arg);
        }
    }

    private static <C> Integer unaryPriority(final Node<C> node) {
        return node.get(c -> Integer.MAX_VALUE, n -> Integer.MAX_VALUE, (n, p, a) -> p, (n, a, b) -> Integer.MIN_VALUE);
    }

    public void binary(final String name, final int priority) {
        final Priority mp = new Priority(name, priority);
        priorities.put(name, mp);

        nodeRenderer.binary(name, (settings, l, r) -> settings.extra(process(settings, mp, l, r), random));
    }

    private Node<C> process(final Settings settings, final Priority mp, final Node<C> l, final Node<C> r) {
        if (settings.mode == Mode.FULL) {
            return paren(true, op(mp, l, r));
        }

        final Priority lp = priority(l);
        final Priority rp = priority(r);

        final int rc = rp.compareLevels(mp);

        // :NOTE: Especially ugly code, do not replicate
        final boolean advanced = settings.mode == Mode.SAME
                || mp.has(2)
                || mp.has(1) && (mp != rp || (settings.mode == Mode.TRUE_MINI && hasOther(r, rp)));

        final Node<C> al = paren(lp.compareLevels(mp) < 0, l);
        if (rc == 0 && !advanced) {
            return get(r, null, (n, a, b) -> rp.op(mp.op(al, a), b));
        } else {
            return mp.op(al, paren(rc == 0 && advanced || rc < 0, r));
        }
    }

    private boolean hasOther(final Node<C> node, final Priority priority) {
        return get(node, () -> false, (name, l, r) -> {
            final Priority p = Functional.get(priorities, name);
            if (p.compareLevels(priority) != 0) {
                return false;
            }
            return p != priority || hasOther(l, priority);
        });
    }

    private Node<C> op(final Priority mp, final Node<C> l, final Node<C> r) {
        return mp.op(l, r);
    }

    private Priority priority(final Node<C> node) {
        return get(node, () -> Priority.MAX, (n, a, b) -> Functional.get(priorities, n));
    }

    private <R> R get(final Node<C> node, final Supplier<R> common, final Node.Binary<Node<C>, R> binary) {
        return node.get(
                c -> common.get(),
                n -> common.get(),
                (n, p, a) -> common.get(),
                binary
        );
    }

    public String render(final Expr<C, ?> expr, final Settings settings) {
        return renderNode(renderToNode(settings, expr), settings.parens);
    }

    public String renderNode(final Node<C> node) {
        return renderNode(node, DEFAULT_PARENS);
    }

    private String renderNode(final Node<C> node, final List<Paren> parens) {
        return node.cata(
                String::valueOf,
                name -> name,
                (name, priority, arg) -> name == PAREN ? parens(arg, parens)
                        : priority == Integer.MAX_VALUE ? name + arg + brackets.get(name)
                        : (priority & 1) == 1 ? name + arg : arg + name,
                (name, a, b) -> a + " " + name + " " + b
        );
    }

    private String parens(final String expression, final List<Paren> parens) {
        final Paren paren = random.randomItem(parens);
        return paren.open + expression + paren.close;
    }

    public Node<C> renderToNode(final Settings settings, final Expr<C, ?> expr) {
        final Expr<C, Node<C>> convert = expr.convert((name, variable) -> Node.op(name));
        return nodeRenderer.render(settings, convert);
    }

    private static <C> Node<C> paren(final boolean condition, final Node<C> node) {
        return condition ? Node.op(PAREN, 1, node) : node;
    }

    // :NOTE: Especially ugly bit-fiddling, do not replicate
    private static final class Priority {
        private static final int Q = 3;
        private static final Priority MAX = new Priority("MAX", Integer.MAX_VALUE - Q);
        private final String op;

        private final int priority;

        public Priority(final String op, final int priority) {
            this.op = op;
            this.priority = priority;
        }

        private int compareLevels(final Priority that) {
            return (priority | Q) - (that.priority | Q);
        }

        @Override
        public String toString() {
            return String.format("Priority(%s, %d, %d)", op, priority | Q, priority & Q);
        }

        public <C> Node<C> op(final Node<C> l, final Node<C> r) {
            return Node.op(op, l, r);
        }

        private boolean has(final int value) {
            return (priority & Q) == value;
        }
    }

    public enum Mode {
        FULL, SAME, TRUE_MINI, SIMPLE_MINI;

        public Settings settings(final int limit) {
            return new Settings(this, limit);
        }
    }

    public static class Paren {
        private final String open;
        private final String close;

        public Paren(final String open, final String close) {
            this.open = open;
            this.close = close;
        }
    }

    public static class Settings {
        private final Mode mode;
        private final int limit;
        private final List<Paren> parens;

        public Settings(final Mode mode, final int limit) {
            this(mode, limit, DEFAULT_PARENS);
        }

        private Settings(final Mode mode, final int limit, final List<Paren> parens) {
            this.mode = mode;
            this.limit = limit;
            this.parens = parens;
        }

        public <C> Node<C> extra(Node<C> node, final ExtendedRandom random) {
            while (random.nextInt(Integer.MAX_VALUE) < limit) {
                node = paren(true, node);
            }
            return node;
        }

        public Settings withParens(final List<Paren> parens) {
            return this.parens.equals(parens) ? this : new Settings(mode, limit, List.copyOf(parens));
        }
    }
}
