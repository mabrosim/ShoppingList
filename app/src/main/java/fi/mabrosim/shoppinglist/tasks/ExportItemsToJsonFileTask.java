package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.utils.FileUtils;

public class ExportItemsToJsonFileTask extends AsyncTask<Void, Void, File> {
    final Context mAppContext;

    public ExportItemsToJsonFileTask(Context context) {
        mAppContext = context;
    }

    @Override
    protected File doInBackground(Void... params) {
        List<Item> items = new ArrayList<>();
        Iterator<Item> itemIterator = Item.findAll(Item.class);
        while (itemIterator.hasNext()) {
            items.add(itemIterator.next());
        }
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

    @Override
    protected void onPostExecute(File file) {
        Toast.makeText(mAppContext, "Items exported to json", Toast.LENGTH_SHORT).show();
    }
}
