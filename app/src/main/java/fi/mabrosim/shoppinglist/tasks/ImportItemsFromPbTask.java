package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.utils.Actions;

class ImportItemsFromPbTask extends AsyncTask<Uri, Void, Void> {
    private final Context mAppContext;
    private final Intent  mIntent;

    ImportItemsFromPbTask(Context context, Intent intent) {
        mAppContext = context.getApplicationContext();
        mIntent = intent;
    }

    @Override
    protected Void doInBackground(Uri... params) {
        Uri uri = params[0];
        InputStream is;
        try {
            is = mAppContext.getContentResolver().openInputStream(uri);
            if (is != null) {
                // FIXME merge diff to database
                ItemList.deleteAll(ItemList.class);
                Item.deleteAll(Item.class);

                ItemList itemList = new ItemList(is);
                List<Label> labels = itemList.getLabels();
                itemList.save();
                for (Label label : labels) {
                    List<Item> items = label.getItems();
                    for (Item item : items) {
                        item.setItemList(itemList);
                        item.setLabel(label.getName());
                        item.save();
                    }
                }
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAppContext.startActivity(mIntent);
        Intent intent = new Intent(Actions.ACTION_IMPORT_COMPLETED);
        LocalBroadcastManager.getInstance(mAppContext).sendBroadcast(intent);
    }
}
