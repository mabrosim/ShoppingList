package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.CsvUtils;

class ImportItemsFromCsvTask extends AsyncTask<Uri, Void, Void> {
    private final Context mAppContext;
    private final Intent  mIntent;

    ImportItemsFromCsvTask(Context context, Intent intent) {
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

                CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
                List<String[]> labeledItemNames = new ArrayList<>();

                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    labeledItemNames.add(nextLine);
                }
                String[][] transposedMatrix = CsvUtils.transpose2dArrayToMatrix(labeledItemNames.toArray(new String[labeledItemNames.size()][]));
                // TODO save ItemList from csv file name
/*                ItemList itemList = new ItemList();
                itemList.setName("MyList1");*/

                for (String[] s : transposedMatrix) {
                    Label label = new Label(s[0]);
                    for (int i = 1; i < s.length; i++) {
                        if (!s[i].isEmpty()) {
                            Item item = new Item(s[i]);
                            item.setLabel(label.getName());
                            item.save();
                        }
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
