package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.Label;

class ImportItemsFromJsonTask extends AsyncTask<Uri, Void, Void> {
    private final Context mAppContext;
    private final Intent  mIntent;

    private static final Type TYPE_ITEMS_JSON = new TypeToken<List<Item>>() {
    }.getType();

    ImportItemsFromJsonTask(Context context, Intent intent) {
        mAppContext = context.getApplicationContext();
        mIntent = intent;
    }

    @Override
    protected Void doInBackground(Uri... params) {
        Uri uri = params[0];
        String json = null;
        try {
            json = inputStreamToString(mAppContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Item> items;
        if (json != null) {
            items = gson.fromJson(json, TYPE_ITEMS_JSON);

            // FIXME temporary solution - deleteAndBroadcast ALL first
            Label.deleteAll(Label.class);
            Item.deleteAll(Item.class);

            List<String> labelNames = new ArrayList<>();
            for (Item item : items) {
                String name = item.getLabel().getName();
                if (!labelNames.contains(name)) {
                    labelNames.add(name);
                    item.getLabel().save();
                }
                item.save();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAppContext.startActivity(mIntent);
    }

    private static String inputStreamToString(InputStream is) {
        String json = "";
        int size;
        try {
            size = is.available();
            byte[] buffer = new byte[size];
            int count = is.read(buffer);
            is.close();
            if (count > 0) {
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
