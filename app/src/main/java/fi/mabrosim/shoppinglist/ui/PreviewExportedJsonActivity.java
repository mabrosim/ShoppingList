package fi.mabrosim.shoppinglist.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Item;

public class PreviewExportedJsonActivity extends AppCompatActivity {
    private TextView mPreviewTv;
    private static final String TAG = "PreviewExportedJsonActiv";

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
            List<Item> items = new ArrayList<>();
            Iterator<Item> itemIterator = Item.findAll(Item.class);
            while (itemIterator.hasNext()) {
                items.add(itemIterator.next());
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(items);
/*            File file = FileUtils.JSON.newFile(getApplicationContext());
            FileOutputStream outputStream;

            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(json.getBytes());

                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            mPreviewTv.setText(s);
        }
    }
}
