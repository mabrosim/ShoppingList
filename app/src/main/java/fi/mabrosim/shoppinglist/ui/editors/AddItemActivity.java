package fi.mabrosim.shoppinglist.ui.editors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;

public class AddItemActivity extends EditItemActivity {

    public AddItemActivity() {
        mItem = new Item();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mItem.setChecked(intent.getBooleanExtra(Actions.EXTRA_IS_CHECKED, true));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mItem.setItemList(ItemList.findCurrentList());
    }
}
