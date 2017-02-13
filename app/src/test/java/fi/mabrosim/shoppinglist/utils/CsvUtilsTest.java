package fi.mabrosim.shoppinglist.utils;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Test;

import java.util.Arrays;

public class CsvUtilsTest {

    @Test
    public void transpose2dArrayToMatrix1() throws Exception {
        final String[][] entries = {{"1", "2", "3", "4"}, {"5", "6", "7", "8"}, {"9", "10", "11", "12"}, {"13", "14", "15", "16"}};
        final String[][] expectedTransposedEntries = {{"1", "5", "9", "13"}, {"2", "6", "10", "14"}, {"3", "7", "11", "15"}, {"4", "8", "12", "16"}};

        assertEquals(expectedTransposedEntries, CsvUtils.transpose2dArrayToMatrix(entries));
    }

    @Test
    public void transpose2dArrayToMatrix2() throws Exception {
        final String[][] entries = {{"1", "2", "3", "4"}, {"5", "6", "7", ""}, {"9", "10", "", "12"}, {"", "14", "", "16"}};
        final String[][] expectedTransposedEntries = {{"1", "5", "9", ""}, {"2", "6", "10", "14"}, {"3", "7", "", ""}, {"4", "", "12", "16"}};

        assertEquals(expectedTransposedEntries, CsvUtils.transpose2dArrayToMatrix(entries));
    }

    @Test
    public void transpose2dArrayToMatrix3() throws Exception {
        final String[][] entries = {{"1", "2", "3"}, {"5", "6", "7", "8"}, {"9", "10"}, {"13"}};
        final String[][] expectedTransposedEntries = {{"1", "5", "9", "13"}, {"2", "6", "10", ""}, {"3", "7", "", ""}, {"", "8", "", ""}};

        assertEquals(expectedTransposedEntries, CsvUtils.transpose2dArrayToMatrix(entries));
    }

    @Test
    public void transpose2dArrayToMatrix4() throws Exception {
        final String[][] entries = {{"1", "2"}, {"5"}, {"9", "10", "11", "12"}, {"13", "14"}};
        final String[][] expectedTransposedEntries = {{"1", "5", "9", "13"}, {"2", "", "10", "14"}, {"", "", "11", ""}, {"", "", "12", ""}};

        assertEquals(expectedTransposedEntries, CsvUtils.transpose2dArrayToMatrix(entries));
    }

    @Test
    public void transpose2dArrayToMatrix5() throws Exception {
        final String[][] entries = {{"1", "2", "3", "4", "5", "6", "7", "8"}, {"9", "10", "11", "12"}, {"13", "14"}};
        final String[][] expectedTransposedEntries = {{"1", "9", "13"}, {"2", "10", "14"}, {"3", "11", ""}, {"4", "12", ""}, {"5", "", ""}, {"6", "", ""}, {"7", "", ""}, {"8", "", ""}};

        assertEquals(expectedTransposedEntries, CsvUtils.transpose2dArrayToMatrix(entries));
    }

    @Test
    public void transpose2dArrayToMatrix6() throws Exception {
        final String[][] entries = {{"1", "2", "3"}, {"4", "5"}, {"6"}, {"7", "8"}, {"9"}};
        final String[][] expectedTransposedEntries = {{"1", "4", "6", "7", "9"}, {"2", "5", "", "8", ""}, {"3", "", "", "", ""}};

        assertEquals(expectedTransposedEntries, CsvUtils.transpose2dArrayToMatrix(entries));
    }

    private static void assertEquals(String[][] expected, String[][] actual) {
        try {
            Assert.assertTrue(Arrays.deepEquals(expected, actual));
        } catch (AssertionFailedError e) {
            System.out.println(" expected: " + matrixToString(expected));
            System.out.println("   actual: " + matrixToString(actual));
            Assert.fail("Arrays do not match !");
        }
    }

    private static String matrixToString(String[][] m) {
        StringBuilder expectedStr = new StringBuilder();
        expectedStr.append("{");

        for (String[] strings : m) {
            expectedStr.append("{");
            for (String s : strings) {
                expectedStr.append('"');
                expectedStr.append(s);
                expectedStr.append('"');
                expectedStr.append(',');
            }
            expectedStr.deleteCharAt(expectedStr.length() - 1);
            expectedStr.append("},");
        }
        expectedStr.deleteCharAt(expectedStr.length() - 1);
        expectedStr.append("}");
        return expectedStr.toString();
    }
}
