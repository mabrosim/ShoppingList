package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;

public class DeleteAllTask extends BroadcastOnCompleteAsyncTask<Void> {

    public DeleteAllTask(Context context, Intent intent) {
        super(context, intent);
    }

    @Override
    protected Void doInBackground(Void... params) {
        //mAppContext.deleteDatabase("items.db");
        Item.deleteAll(Item.class);
        ItemList.deleteAll(ItemList.class);

        return null;
    }
}
