package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.utils.Actions;

public class ClearChoicesTask extends BroadcastOnCompleteAsyncTask<Void> {

    public ClearChoicesTask(Context context) {
        super(context, new Intent());
        mIntent.setAction(Actions.ACTION_RECORDS_UPDATED);
        mIntent.putExtra(Actions.EXTRA_RECORD_TYPE, RecordType.ITEM);
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (Item item : Item.listAll(Item.class)) {
            if (item.isChecked()) {
                item.setChecked(false);
                item.save();
            }
        }
        return null;
    }
}
