package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;

/**
 * Do The Magic class <br>
 * Populates shopping item list based on history data,<br>
 * work in progress.
 */
public class DoTheMagicTask extends BroadcastOnCompleteAsyncTask<Void> {

    public DoTheMagicTask(Context context) {
        super(context);
    }

    @Override
    protected Intent doInBackground(Void... params) {
        selectItems();

        Intent intent = new Intent(Actions.ACTION_RECORDS_UPDATED);
        intent.putExtra(Actions.EXTRA_RECORD_TYPE, RecordType.ITEM);
        return intent;
    }

    /**
     * Initial implementation of list population based on historical data
     */
    private void selectItems() {
        ItemList itemList = ItemList.findCurrentList();

        if (itemList != null) {
            List<Item> items = itemList.findItems();

            for (Item item : items) {
                if (item.isChecked()) {
                    item.setCheckedWithoutLogging(false);
                    item.save();
                }
            }

            for (Item item : selectItemsByRelevance2(items)) {
                item.setChecked(true);
                item.save();
            }
        }
    }

    static List<Item> selectItemsByRelevance(List<Item> items) {
        final ArrayList<Pair<Item, Long>> lastItems = new ArrayList<>();
        final ArrayList<Item> selectedItems = new ArrayList<>();

        for (Item item : items) {
            List<Long> stamps = item.getLogCheckedList();

            if (!stamps.isEmpty()) {
                lastItems.add(new Pair<>(item, stamps.get(stamps.size() - 1)));
            }
        }

        Collections.sort(lastItems, new ByStamp());

        // select top 10 items
        int size = lastItems.size() < 10 ? lastItems.size() : 10;
        for (int i = 0; i < size; i++) {
            selectedItems.add(lastItems.get(i).first);
        }

        return selectedItems;
    }

    static List<Item> selectItemsByRelevance2(List<Item> items) {
        reduceLogs(items);
        dropShortSelectionsByTimeDelta(items);
        Collections.sort(items, new ByCheckFrequency());
        // select top 30 items
        return new ArrayList<>(items.subList(0, items.size() < 30 ? items.size() : 30));
    }

    static void dropShortSelectionsByTimeDelta(List<Item> items) {
        final int TIME_DELTA = 130;

        for (Item item : items) {
            List<Long> checks = item.getLogCheckedList();
            Collections.sort(checks);

            ListIterator<Long> cIterator = checks.listIterator();
            ListIterator<Long> uIterator = item.getLogUncheckedList().listIterator();

            while (cIterator.hasNext()) {
                Long c = cIterator.next();
                while (uIterator.hasNext()) {
                    Long u = uIterator.next();
                    if (TIME_DELTA > Math.abs(c - u)) {
                        cIterator.remove();
                        uIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    static void reduceLogs(List<Item> items) {
        for (Item item : items) {
            reduceLog(item.getLogCheckedList());
            reduceLog(item.getLogUncheckedList());
        }
    }

    private static void reduceLog(final List<Long> log) {
        final int TIME_DELTA = 400;

        Collections.sort(log);
        ListIterator<Long> logIter = log.listIterator();

        while (logIter.hasNext()) {
            Long c = logIter.next();
            int i = logIter.nextIndex();
            if ((i < log.size()) && (TIME_DELTA > Math.abs(c - log.get(i)))) {
                logIter.remove();
            }
        }
    }

    /**
     * Item comparator by last checked timestamp, reversed order.
     */
    private static class ByStamp implements Comparator<Pair<Item, Long>> {
        @Override
        public int compare(Pair<Item, Long> lhs, Pair<Item, Long> rhs) {
            return compare(rhs.second, lhs.second);
        }

        private static int compare(long x, long y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }

    static class ByCheckFrequency implements Comparator<Item> {
        @Override
        public int compare(Item lhs, Item rhs) {
            return compare(rhs.getLogCheckedList().size(), lhs.getLogCheckedList().size());
        }

        private static int compare(long x, long y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }
}
