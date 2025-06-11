import java.lang.Character;

public class SumFloatPunct {
    public static boolean checkNotP(char curCh) {
        int type = Character.getType(curCh);
        return ((!(Character.isWhitespace(curCh))
                && (type != Character.START_PUNCTUATION)
                && (type != Character.END_PUNCTUATION)));
    }
    public static boolean checkIsP(char curCh) {
        int type = Character.getType(curCh);
        return (Character.isWhitespace(curCh)
                || (type == Character.START_PUNCTUATION)
                || (type == Character.END_PUNCTUATION));
    }

    public static void main(String[] args) {
        //String[] args = new String[3];
        //args[0] = "1";
        //args[1] = "2";
        //args[2] = "3";

        float answ = 0;
        int beg = -1;
        int end = -1;
        float curN;
        char curCh;
        String subS;

        for (String s : args) {
            for (int i = 0; i < s.length(); i++) {
                curCh = s.charAt(i);

                if ( checkNotP(curCh) && (beg == -1)) {
                    beg = i;
                }
                if ((beg != -1) && checkIsP(curCh) | (i == s.length() - 1)) {
                    if (checkIsP(curCh)) {
                        end = i - 1;
                    } else {
                        end = s.length() - 1;
                    }
                    subS = s.substring(beg, end + 1);
                    curN = Float.parseFloat(subS);
                    answ = answ + curN;
                    beg = -1;
                    end = -1;
                }
            }
        }
        System.out.println(answ);
    }
}
/*
1. gettype не нужен
2. | - битовое или, || - логическое
3. второй if - можно написать оптимизированние

 */