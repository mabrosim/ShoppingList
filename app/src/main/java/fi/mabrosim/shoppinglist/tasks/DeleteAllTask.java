package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.data.records.Label;

public class DeleteAllTask extends BroadcastOnCompleteAsyncTask<Void> {

    public DeleteAllTask(Context context, Intent intent) {
        super(context, intent);
    }

    @Override
    protected Void doInBackground(Void... params) {
        //mAppContext.deleteDatabase("items.db");
        Item.deleteAll(Item.class);
        Label.deleteAll(Label.class);
        ItemList.deleteAll(ItemList.class);

        return null;
    }
}
