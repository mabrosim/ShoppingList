package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.utils.Actions;

public class RenameLabelTask extends BroadcastOnCompleteAsyncTask<String> {
    private final String mLabelName;

    public RenameLabelTask(Context context, String labelName) {
        super(context);
        mLabelName = labelName;
    }

    @Override
    protected Intent doInBackground(String... params) {
        List<Item> items = Item.findById(Item.class, params);

        for (Item i : items) {
            i.setLabel(mLabelName);
            i.save();
        }
        return new Intent(Actions.ACTION_RECORDS_UPDATED);
    }
}
