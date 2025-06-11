import java.util.Arrays;

public class IntMatrix {
    int[][] intMatrix = new int[0][0];
    int indexList = 0;

    public IntMatrix(int size) {
        intMatrix = new int[size][0];
        indexList = size;
    }

    public void requreMult() {
        if (indexList == intMatrix.length) {
            intMatrix = Arrays.copyOf(intMatrix, intMatrix.length * 2);
        }
    }

    public void add(int num1, int num2) {
        requreMult();
        intMatrix[indexList] = new int[]{num1, num2};
        indexList++;
    }

    public int[] get(int curIndex) {
        return intMatrix[curIndex];
    }

    public void cut() {
        intMatrix = Arrays.copyOf(intMatrix, indexList);
    }

    public void insert(int index, int num1, int num2) {
        intMatrix[index] = new int[]{num1, num2};
    }

    public String showArrayBase() {
        String showS = "";
        for (int i = 0; i < intMatrix.length; i++) {
            if (i != 0) {
                showS = showS + " " + intMatrix[i][0] + ":" + intMatrix[i][1];
            } else {
                showS = " " + intMatrix[i][0];
            }
        }
        return showS;
    }
}