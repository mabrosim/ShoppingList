package fi.mabrosim.shoppinglist.ui.editors;

import android.content.Intent;
import android.os.Bundle;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.Label;
import fi.mabrosim.shoppinglist.utils.Actions;

public class AddItemActivity extends EditItemActivity {

    public AddItemActivity() {
        mItem = new Item();
        mItem.setLabel(new Label());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mItem.setChecked(intent.getBooleanExtra(Actions.EXTRA_IS_CHECKED, false));
    }
}
