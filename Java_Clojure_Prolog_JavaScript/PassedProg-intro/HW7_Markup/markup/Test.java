package markup;

import java.util.AbstractList;
import java.util.List;
import java.lang.StringBuilder;
import java.util.ArrayList;

public class Test {
    public static void func(StringBuilder s) {
        s.append("4");
    }
    public static void main(String[] args) {
        StringBuilder s = new StringBuilder("123");
        System.out.println(s);
        func(s);
        System.out.println(s);
        List<List> list = new ArrayList<>();
        list.add(new AbstractList() {
            @Override
            public Object get(int index) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        });
        System.out.println(list.get(0).getClass());
    }
}
        