package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ImportItemsFromPbTask extends AsyncTask<Uri, Void, Long> {

    private final Context mAppContext;
    private final Intent  mIntent;
    private final String  mFileName;

    ImportItemsFromPbTask(Context context, Intent intent, String fileName) {
        mAppContext = context.getApplicationContext();
        mIntent = intent;
        mFileName = fileName;
    }

    @Override
    protected Long doInBackground(Uri... params) {
        Uri uri = params[0];
        InputStream is;
        long listId = 0L;

        try {
            is = mAppContext.getContentResolver().openInputStream(uri);
            if (is != null) {
                ItemList itemList = new ItemList(is);
                is.close();

                if (itemList.getName().isEmpty() || itemList.getName().equals("List")) {
                    itemList.setName(FileUtils.getBase(mFileName));
                }

                List<ItemList> localLists = ItemList.listAll(ItemList.class);
                for (ItemList il : localLists) {
                    if (il.getName().equals(itemList.getName())) {
                        ItemList.delete(il);
                    } else if (il.isCurrent()) {
                        il.setCurrent(false);
                        il.save();
                    }
                }

                itemList.setCurrent(true);
                listId = itemList.save();
                List<Label> labels = itemList.getLabels();
                for (Label label : labels) {
                    List<Item> items = label.getItems();
                    for (Item item : items) {
                        item.setItemList(itemList);
                        item.setLabel(label.getName());
                        item.save();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listId;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        mIntent.setAction(Actions.ACTION_RECORD_ADDED);
        mIntent.putExtra(Actions.EXTRA_RECORD_ID, aLong);
        mIntent.putExtra(Actions.EXTRA_RECORD_TYPE, RecordType.ITEM_LIST);
        mAppContext.startActivity(mIntent);
    }
}
