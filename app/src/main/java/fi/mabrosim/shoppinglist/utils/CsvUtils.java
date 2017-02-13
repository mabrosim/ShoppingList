package fi.mabrosim.shoppinglist.utils;

public final class CsvUtils {
    private CsvUtils() {
    }

    /**
     * Transposes given 2 dimensional array into matrix by interchanging each row and the corresponding column,<br>
     * where gaps are filled with empty strings.
     *
     * @param m two-dimensional array of Strings
     * @return transposed square matrix
     */
    public static String[][] transpose2dArrayToMatrix(String[][] m) {
        int maxWidth = m.length;
        int maxHeight = 0;

        for (String[] s : m) {
            maxHeight = Math.max(maxHeight, s.length);
        }

        String[][] temp = new String[maxHeight][maxWidth];
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxHeight; j++) {
                temp[j][i] = j < m[i].length ? m[i][j] : "";
            }
        }
        return temp;
    }
}
