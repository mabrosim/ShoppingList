package fi.mabrosim.shoppinglist.data;

import java.io.Serializable;
import java.util.Comparator;

import fi.mabrosim.shoppinglist.data.records.Item;

public final class ItemComparators {

    private ItemComparators() {
    }

    /**
     * Comparator implements simple alphanumeric sorting from a to z by item name.
     */
    public static class ByName implements Comparator<Item>, Serializable {

        @Override
        public int compare(Item lhs, Item rhs) {
            return byName(lhs, rhs);
        }
    }

    /**
     * Comparator does alphanumeric sorting but shows checked items first.
     */
    public static class ByChecked implements Comparator<Item>, Serializable {

        @Override
        public int compare(Item lhs, Item rhs) {
            return byChecked(lhs, rhs);
        }
    }

    private static class ByLabel implements Comparator<Item>, Serializable {

        @Override
        public int compare(Item lhs, Item rhs) {
            return byLabel(lhs, rhs);
        }
    }

    private static int byName(Item lhs, Item rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

    private static int byChecked(Item lhs, Item rhs) {
        return lhs.isChecked() == rhs.isChecked() ? byLabel(lhs, rhs) : lhs.isChecked() ? -1 : 1;
    }

    private static int byLabel(Item lhs, Item rhs) {
        String lhsName = lhs.getLabelName();
        String rhsName = rhs.getLabelName();
        return lhsName.equals(rhsName) ? byName(lhs, rhs) : lhsName.compareTo(rhsName);
    }

    private static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
