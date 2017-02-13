package fi.mabrosim.shoppinglist.ui.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.ItemComparators;

class SearchItemsAdapter extends SugarRecordAdapter<Item> {
    private static final String TAG = "SearchItemsAdapter";

    SearchItemsAdapter() {
        super(Item.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_checked_textview, parent, false);
        }

        Item item = getItem(position);
        StringBuilder sb = new StringBuilder();
        sb.append(item.getName());

        String quantity = item.getQuantity();
        if (!quantity.isEmpty()) {
            sb.append(", ");
            sb.append(quantity);
        }
        CheckBox checkedView = (CheckBox) convertView.findViewById(android.R.id.text1);
        checkedView.setText(sb);
        checkedView.setChecked(item.isChecked());

        return convertView;
    }

    @Override
    protected void addItems(List<Item> items, String filter) {
        List<Item> itemList = Item.listAll(Item.class);

        if (filter == null) {
            items.addAll(itemList);
        } else {
            for (Item item : itemList) {
                if (item.getName().toLowerCase(Locale.ENGLISH).contains(filter.toLowerCase(Locale.ENGLISH))) {
                    items.add(item);
                }
            }
        }
        Collections.sort(items, new ItemComparators.ByName());
    }
}
