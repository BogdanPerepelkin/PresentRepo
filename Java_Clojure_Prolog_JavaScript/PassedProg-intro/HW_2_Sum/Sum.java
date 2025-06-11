import java.lang.Character;

public class Sum {
    public static void main(String[] args) {
        //String[] args = new String[3];
        //args[0] = "1";
        //args[1] = "2";
        //args[2] = "3";

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
                if ((beg != -1) && (Character.isWhitespace(s.charAt(i)) | (i == s.length() - 1))) {
                    if (Character.isWhitespace(s.charAt(i))) {
                        end = i - 1;
                    } else {
                        end = s.length() - 1;
                    }
                    subS = s.substring(beg, end + 1);
                    n = Integer.valueOf(subS);
                    answ = answ + n;
                    beg = -1;
                    end = -1;
                }
            }
        }
        System.out.println(answ);
    }
}