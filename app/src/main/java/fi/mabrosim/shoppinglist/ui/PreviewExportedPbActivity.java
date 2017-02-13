package fi.mabrosim.shoppinglist.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.protobuf.Protos;
import fi.mabrosim.shoppinglist.utils.Dog;

public class PreviewExportedPbActivity extends AppCompatActivity {
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
            Protos.PbItemList itemList;

            ItemList items;
            List<ItemList> itemLists = ItemList.listAll(ItemList.class);
            if (itemLists.isEmpty()) {
                items = new ItemList();
            } else {
                // TODO item list name should be taken from task params
                items = itemLists.get(0);
            }

            ByteArrayInputStream in = new ByteArrayInputStream(items.toPbObject().toByteArray());
            try {
                itemList = Protos.PbItemList.parseFrom(in);
                in.close();
                result = itemList.toString();
            } catch (IOException e) {
                Dog.e("PreviewExportedPb", "getItemsFromDb: ", e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            mPreviewTv.setText(s);
        }
    }
}
