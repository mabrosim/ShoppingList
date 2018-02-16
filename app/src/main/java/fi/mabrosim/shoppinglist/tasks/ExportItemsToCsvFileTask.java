package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.CsvUtils;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ExportItemsToCsvFileTask extends AsyncTask<Void, Void, File> {
    final Context mAppContext;

    ExportItemsToCsvFileTask(Context context) {
        mAppContext = context;
    }

    @Override
    protected File doInBackground(Void... params) {
        ItemList itemList = ItemList.findCurrentList();

        if (itemList != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(itemList.toPbObject().toByteArray());
            try {
                itemList = new ItemList(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String[]> labeledItemNames = new ArrayList<>();
            for (Label l : itemList.getLabels()) {
                labeledItemNames.add(l.getItemNamesWithLabel());
            }

            File file = FileUtils.CSV.newFile(mAppContext);
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                for (String[] a : CsvUtils.transpose2dArrayToMatrix(labeledItemNames.toArray(new String[labeledItemNames.size()][]))) {
                    writer.writeNext(a);
                }
                writer.close();
            } catch (IOException e) {
                //error
            }
            return file;
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            Toast.makeText(mAppContext, "Items exported to CSV file", Toast.LENGTH_SHORT).show();
        }
    }
}
