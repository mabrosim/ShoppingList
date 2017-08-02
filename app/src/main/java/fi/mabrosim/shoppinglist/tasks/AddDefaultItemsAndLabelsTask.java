package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.utils.Prefs;

public class AddDefaultItemsAndLabelsTask extends AsyncTask<Void, Void, Void> {
    private static final String BPB_FILENAME = "default_items.bpb";

    private final Context mAppContext;
    private final Intent  mIntent;

    public AddDefaultItemsAndLabelsTask(Context context, Intent intent) {
        mAppContext = context.getApplicationContext();
        mIntent = intent;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            InputStream is = mAppContext.getAssets().open(BPB_FILENAME);
            ItemList itemList = new ItemList(is);
            List<Label> labels = itemList.getLabels();

            is.close();

            // XXX delete all
            mAppContext.deleteDatabase("items.db");

            itemList.save();

            for (Label label : labels) {
                List<Item> items = label.getItems();

                for (Item item : items) {
                    item.setItemList(itemList);
                    item.setLabel(label.getName());
                    item.save();
                }
            }
            Prefs.setReady(mAppContext);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAppContext.startActivity(mIntent);
    }
}
