package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.utils.Actions;

public class RenameLabelTask extends BroadcastOnCompleteAsyncTask<String> {

    public RenameLabelTask(Context context) {
        super(context, new Intent());
        mIntent.setAction(Actions.ACTION_RECORDS_UPDATED);
    }

    @Override
    protected Void doInBackground(String... params) {
        String oldName = params[0];
        String newName = params[1];

        List<Item> items = Item.find(Item.class, "LABEL_NAME LIKE ?", String.valueOf(oldName));
        for (Item i : items) {
            i.setLabel(newName);
            i.save();
        }
        return null;
    }
}
