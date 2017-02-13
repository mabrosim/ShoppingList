package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;

import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.Label;

public class DeleteLabelTask extends BroadcastOnCompleteAsyncTask<Long> {

    public DeleteLabelTask(Context context) {
        super(context, null);
    }

    @Override
    protected Void doInBackground(Long... params) {
        // update items
        Long labelId = params[0];
        List<Item> items = Item.find(Item.class, "LABEL==?", String.valueOf(labelId));
        for (Item i : items) {
            i.setLabel(null);
            i.save();
        }
        Label.findById(Label.class, labelId).deleteAndBroadcast(mAppContext);
        return null;
    }
}
