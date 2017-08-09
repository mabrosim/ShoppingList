package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ExportItemsToJsonFileTask extends AsyncTask<Void, Void, File> {
    final Context mAppContext;

    ExportItemsToJsonFileTask(Context context) {
        mAppContext = context;
    }

    @Override
    protected File doInBackground(Void... params) {
        ItemList itemList = ItemList.findCurrentList();

        if (itemList != null) {
            List<Item> items = itemList.findItems();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(items);
            File file = FileUtils.JSON.newFile(mAppContext);
            FileOutputStream outputStream;

            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(json.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            Toast.makeText(mAppContext, "Items exported to json", Toast.LENGTH_SHORT).show();
        }
    }
}
