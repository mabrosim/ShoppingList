package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;

import fi.mabrosim.shoppinglist.data.records.ItemList;

public class DeleteItemListTask extends BroadcastOnCompleteAsyncTask<Long> {

    public DeleteItemListTask(Context context) {
        super(context);
    }

    @Override
    protected Intent doInBackground(Long... params) {
        // update items
        Long listId = params[0];
        ItemList.findById(ItemList.class, listId).deleteAndBroadcast(mAppContext);
        return null;
    }
}
