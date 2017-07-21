package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.Dog;

/**
 * Do The Magic class <br>
 * Populates shopping item list based on history data,<br>
 * work in progress.
 */
public class DoTheMagicTask extends BroadcastOnCompleteAsyncTask<Void> {
    private static final String TAG = "DoTheMagicTask";

    public DoTheMagicTask(Context context) {
        super(context, new Intent());
        mIntent.setAction(Actions.ACTION_RECORDS_UPDATED);
        mIntent.putExtra(Actions.EXTRA_RECORD_TYPE, RecordType.ITEM);
    }

    @Override
    protected Void doInBackground(Void... params) {
        selectItems();
        return null;
    }

    /**
     * Initial implementation of list population based on historical data
     */
    private void selectItems() {
        ArrayList<Pair<Item, Long>> lastItems = new ArrayList<>();

        // get data from items
        for (Item item : Item.listAll(Item.class)) {
            List<Long> stamps = item.getLogCheckedList();

            if (!stamps.isEmpty()) {
                lastItems.add(new Pair<>(item, stamps.get(stamps.size() - 1)));
            }

            if (item.isChecked()) {
                item.setCheckedWithoutLogging(false);
                item.save();
            }
        }

        Collections.sort(lastItems, new ByStamp());

        for (Pair<Item, Long> item : lastItems) {
            Dog.d(TAG, "SItem: " + item.first.getName() + " " + item.second.toString());
        }

        // select top 10 items
        int size = lastItems.size() < 10 ? lastItems.size() : 10;
        for (int i = 0; i < size; i++) {
            Item item = lastItems.get(i).first;
            item.setChecked(true);
            item.save();
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
}
