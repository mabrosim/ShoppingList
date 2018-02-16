package fi.mabrosim.shoppinglist.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPreviewTv = findViewById(R.id.previewPb);
        new getItemsFromDb().execute();
    }

    private class getItemsFromDb extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = "Error";
            ItemList itemList = ItemList.findCurrentList();

            if (itemList != null) {
                Protos.PbItemList pbItemList;
                ByteArrayInputStream in = new ByteArrayInputStream(itemList.toPbObject().toByteArray());
                try {
                    pbItemList = Protos.PbItemList.parseFrom(in);
                    in.close();
                    result = pbItemList.toString();
                } catch (IOException e) {
                    Dog.e("PreviewExportedPb", "getItemsFromDb: ", e);
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
