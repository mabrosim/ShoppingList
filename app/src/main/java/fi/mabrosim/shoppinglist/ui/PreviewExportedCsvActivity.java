package fi.mabrosim.shoppinglist.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.CsvUtils;

public class PreviewExportedCsvActivity extends AppCompatActivity {
    private TextView mPreviewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_pb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPreviewTv = (TextView) findViewById(R.id.previewPb);
        new getItemsFromDb().execute();
    }

    private class getItemsFromDb extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = "Error";
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

                StringWriter sWriter = new StringWriter();
                try {
                    CSVWriter writer = new CSVWriter(sWriter, ',');
                    for (String[] a : CsvUtils.transpose2dArrayToMatrix(labeledItemNames.toArray(new String[labeledItemNames.size()][]))) {
                        writer.writeNext(a);
                    }
                    writer.close();
                    result = sWriter.toString();
                } catch (IOException e) {
                    //error
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            mPreviewTv.setText(s);
        }
    }
}
