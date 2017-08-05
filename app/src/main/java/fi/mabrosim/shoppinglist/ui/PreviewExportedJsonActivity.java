package fi.mabrosim.shoppinglist.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.ItemList;

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
            ItemList itemList = ItemList.findCurrentList();

            if (itemList != null) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                return gson.toJson(itemList.findItems());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            mPreviewTv.setText(s);
        }
    }
}
