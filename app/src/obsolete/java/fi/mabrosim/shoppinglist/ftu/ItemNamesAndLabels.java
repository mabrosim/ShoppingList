package fi.mabrosim.shoppinglist.ftu;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.mabrosim.shoppinglist.data.Item;
import fi.mabrosim.shoppinglist.data.Label;

public class ItemNamesAndLabels {
    private static final String TAG           = "ItemNamesAndLabels";
    private static final String JSON_FILENAME = "labeled_items.json";

    public static void addDefaultItems(Context context) {
        for (Item item : readItemsFromAsset(context)) {
            item.getLabel().save();
            item.save();
        }
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(JSON_FILENAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static List<Item> readItemsFromAsset(Context context) {
        final List<Item> items = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context));
            Iterator<String> jsonLabels = jsonObject.keys();

            while (jsonLabels.hasNext()) {
                String labelName = jsonLabels.next();
                Mog.d(TAG, "readItemsFromAsset: " + labelName);
                Label label = new Label(labelName);
                //label.saveAndBroadcast();

                JSONArray jsonItems = jsonObject.getJSONArray(labelName);
                for (int i = 0; i < jsonItems.length(); i++) {
                    String itemName = jsonItems.getString(i);
                    Item item = new Item(itemName);
                    item.setLabel(label);
                    Mog.d(TAG, "readItemsFromAsset: item " + itemName);
                    items.add(item);
                }

/*                Iterator<String> jsonItem = jsonItems.keys();
                while (jsonItem.hasNext()) {
                    String itemName = jsonItem.next();
                    Item item = new Item(itemName);
                    item.setLabel(label);
                    //item.saveAndBroadcast();
                }*/
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, file from asset", e);
        }
        return items;
    }
}
