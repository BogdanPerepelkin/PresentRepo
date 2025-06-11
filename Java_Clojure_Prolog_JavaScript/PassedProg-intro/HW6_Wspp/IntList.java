import java.util.Arrays;

public class IntList {
    int[] intList = new int[0];
    int indexList = 0;

    public IntList(int size) {
        intList = new int[size];
        indexList = size;
    }

    public void requreMult() {
        if (indexList == intList.length) {
            intList = Arrays.copyOf(intList, intList.length * 2);
        }
    }

    public void add(int num) {
        requreMult();
        intList[indexList] = num;
        indexList++;
    }

    public int get(int curIndex) {
        return intList[curIndex];
    }

    public void cut() {
        intList = Arrays.copyOf(intList, indexList);
    }

    public void insert(int index, int num) {
        intList[index] = num;
    }

    public String showArrayBase() {
        String showS = "";
        for (int i : intList) {
            showS = showS + " " + i;
        }
        return showS;
    }

    public String showArrayMode() {
        String showS = " " + intList[0];
        for (int i = 1; i < intList.length; i = i + 2) {
            showS = showS + " " + intList[i] + ":" + intList[i + 1];
        }
        return showS;
    }

    public int[] toRegArr() {
        return intList;
    }
}