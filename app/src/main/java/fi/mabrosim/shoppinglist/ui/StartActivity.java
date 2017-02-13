package fi.mabrosim.shoppinglist.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import fi.mabrosim.shoppinglist.tasks.AddDefaultItemsAndLabelsTask;
import fi.mabrosim.shoppinglist.utils.Prefs;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!Prefs.isReady(this)) {
            new AddDefaultItemsAndLabelsTask(getApplicationContext(), intent).execute();
        } else {
            startActivity(intent);
        }
        finish();
    }
}
