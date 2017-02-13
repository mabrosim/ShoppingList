package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ExportItemsToPbFileTask extends AsyncTask<Void, Void, File> {
    final Context mAppContext;

    ExportItemsToPbFileTask(Context context) {
        mAppContext = context;
    }

    @Override
    protected File doInBackground(Void... params) {
        FileOutputStream outputStream;

        ItemList items;
        List<ItemList> itemLists = ItemList.listAll(ItemList.class);
        if (itemLists.isEmpty()) {
            items = new ItemList();
        } else {
            // TODO item list name should be taken from task params
            items = itemLists.get(0);
        }

        File file = FileUtils.PROTO.newFile(mAppContext);
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(items.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onPostExecute(File file) {
        Toast.makeText(mAppContext, "Items exported to proto bytes", Toast.LENGTH_SHORT).show();
    }
}
