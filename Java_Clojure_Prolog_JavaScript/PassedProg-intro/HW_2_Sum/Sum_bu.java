import java.lang.Character;
import java.lang.management.PlatformManagedObject;

public class Sum_bu {
    public static void main(String[] args) {
        //String[] args = new String[2];
        //args[0] = " ";
        //args[1] = "  ";
        //args[2] = "030";

        int answ = 0;
        int beg = -1;
        int end = -1;
        int n;
        String subS;

        for (String s : args) {
            for (int i = 0; i < s.length(); i++) {
                if ((Character.isWhitespace(s.charAt(i)) == false) && (beg == -1)) {
                    beg = i;
                }
                if ((beg != -1) && (end == -1) && (Character.isWhitespace(s.charAt(i)))) {
                    end = i - 1;
                }
                if ((beg != -1) && (end != -1)) {
                    subS = s.substring(beg, end + 1);
                    n = Integer.valueOf(subS);
                    answ = answ + n;
                    beg = -1;
                    end = -1;
                }
                if ((i == s.length() - 1) && (beg != -1) && (end == -1)) {
                    end = s.length() - 1;
                    subS = s.substring(beg, end + 1);
                    n = Integer.valueOf(subS);
                    answ = answ + n;
                    beg = -1;
                    end = -1;
                }
                if ((i == s.length() - 1) && (beg == -1) && (end == -1)) {
                    int pass = 0;
                }

            }
        }
        System.out.println(answ);
    }
}