package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;

public class DeleteAllTask extends BroadcastOnCompleteAsyncTask<Void> {

    public DeleteAllTask(Context context) {
        super(context);
    }

    @Override
    protected Intent doInBackground(Void... params) {
        Item.deleteAll(Item.class);
        ItemList.deleteAll(ItemList.class);
        return new Intent(Actions.ACTION_ALL_DELETED);
    }
}
