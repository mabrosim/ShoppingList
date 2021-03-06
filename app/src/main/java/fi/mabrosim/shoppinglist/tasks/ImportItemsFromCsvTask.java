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

import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.CsvUtils;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ImportItemsFromCsvTask extends AsyncTask<Uri, Void, Long> {
    private final Context mAppContext;
    private final Intent  mIntent;
    private final String  mFileName;

    ImportItemsFromCsvTask(Context context, Intent intent, String fileName) {
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
                CSVReader reader = new CSVReader(new InputStreamReader(is, "UTF-8"));
                List<String[]> labeledItemNames = new ArrayList<>();

                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    labeledItemNames.add(nextLine);
                }
                String[][] transposedMatrix = CsvUtils.transpose2dArrayToMatrix(labeledItemNames.toArray(new String[labeledItemNames.size()][]));

                ItemList itemList = new ItemList();
                itemList.setName(FileUtils.getBase(mFileName));
                listId = itemList.save();

                for (String[] s : transposedMatrix) {
                    Label label = new Label(s[0]);
                    for (int i = 1; i < s.length; i++) {
                        if (!s[i].isEmpty()) {
                            Item item = new Item(s[i]);
                            item.setLabel(label.getName());
                            item.setItemList(itemList);
                            item.save();
                        }
                    }
                }
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listId;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        mAppContext.startActivity(mIntent);
        Intent intent = new Intent(Actions.ACTION_RECORD_ADDED);
        intent.putExtra(Actions.EXTRA_RECORD_ID, aLong);
        intent.putExtra(Actions.EXTRA_RECORD_TYPE, RecordType.ITEM_LIST);
        LocalBroadcastManager.getInstance(mAppContext).sendBroadcast(intent);
    }
}
