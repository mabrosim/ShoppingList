package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.records.ItemList;

public class SetCurrentItemListTask extends BroadcastOnCompleteAsyncTask<String> {
    public SetCurrentItemListTask(Context context) {
        super(context);
    }

    @Override
    protected Intent doInBackground(String... params) {
        String currentListName = params[0];

        ItemList oldCurrent = null;
        ItemList newCurrent = null;

        for (ItemList il : ItemList.listAll(ItemList.class)) {
            if (il.isCurrent()) {
                oldCurrent = il;
            } else if (il.getName().equals(currentListName)) {
                newCurrent = il;
            }
        }

        if (oldCurrent != null) {
            oldCurrent.setCurrent(false);
            oldCurrent.save();
        }
        if (newCurrent != null) {
            newCurrent.setCurrent(true);
            newCurrent.saveAndBroadcast(mAppContext);
        }
        return null;
    }
}
