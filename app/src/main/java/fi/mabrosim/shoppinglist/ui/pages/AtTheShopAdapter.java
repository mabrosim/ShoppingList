package fi.mabrosim.shoppinglist.ui.pages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.ItemComparators;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.utils.Dog;
import fi.mabrosim.shoppinglist.utils.TimeUtils;

class AtTheShopAdapter extends SugarRecordAdapter<Item> {
    private final static long TIME_TO_KEEP_UNCHECKED_ITEMS_MS = TimeUnit.MINUTES.toMillis(15);

    AtTheShopAdapter() {
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
        boolean isChecked = item.isChecked();

        CheckBox checkedView = (CheckBox) convertView.findViewById(android.R.id.text1);
        checkedView.setText(sb);
        checkedView.setChecked(isChecked);
        if (!isChecked) {
            checkedView.setTextColor(Color.GRAY);
            checkedView.setPaintFlags(checkedView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            checkedView.setTextColor(Color.BLACK);
            checkedView.setPaintFlags(checkedView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return convertView;
    }

    @Override
    protected void addItems(List<Item> items, String filter) {
        long noLaterThan = TimeUtils.DateAsLong(new Date(System.currentTimeMillis() - TIME_TO_KEEP_UNCHECKED_ITEMS_MS));
        items.addAll(Item.find(Item.class, "CHECKED=1 OR UNCHECKED_TIME>=?", String.valueOf(noLaterThan)));
        Collections.sort(items, new ItemComparators.ByChecked());
    }

    @Override
    public void onRecordUpdated(RecordType type, long id) {
        Item item = Item.findById(Item.class, id);
        Dog.d(getClass().getSimpleName(), "onRecordUpdated: " + id + " " + item);
        if (item != null) {
            int index = -1;
            for (Item i : mItems) {
                if (i.getId() == id) {
                    index = mItems.indexOf(i);
                    mItems.set(index, item);
                    break;
                }
            }
            if (index == -1 && item.isChecked()) {
                mItems.add(item);
            }
            Collections.sort(mItems, new ItemComparators.ByChecked());
            notifyDataSetChanged();
        }
    }

    @Override
    public void onRecordAdded(RecordType type, long id) {
        Item item = Item.findById(Item.class, id);
        Dog.d(getClass().getSimpleName(), "onRecordAdded: " + id + " " + item);
        if (item != null && item.isChecked()) {
            mItems.add(item);
            Collections.sort(mItems, new ItemComparators.ByChecked());
            notifyDataSetChanged();
        }
    }
}
