package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;

public class ClearChoicesTask extends BroadcastOnCompleteAsyncTask<Void> {

    public ClearChoicesTask(Context context) {
        super(context);
    }

    @Override
    protected Intent doInBackground(Void... params) {
        ItemList itemList = ItemList.findCurrentList();

        if (itemList != null) {
            for (Item item : itemList.findItems()) {
                if (item.isChecked()) {
                    item.setChecked(false);
                    item.save();
                }
            }

            Intent intent = new Intent();
            intent.setAction(Actions.ACTION_RECORDS_UPDATED);
            intent.putExtra(Actions.EXTRA_RECORD_TYPE, RecordType.ITEM);
            return intent;
        }
        return null;
    }
}
