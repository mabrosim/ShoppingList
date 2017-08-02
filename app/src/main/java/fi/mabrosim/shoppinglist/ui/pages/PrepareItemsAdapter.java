package fi.mabrosim.shoppinglist.ui.pages;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.ItemComparators;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.utils.Dog;

class PrepareItemsAdapter extends BaseExpandableListAdapter implements RecordListener {
    private final List<String>     mLabels       = new ArrayList<>();
    private final List<List<Item>> mLabeledItems = new ArrayList<>();

    @Override
    public int getGroupCount() {
        return mLabels.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mLabeledItems.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mLabels.get(groupPosition);
    }

    @Override
    public Item getChild(int groupPosition, int childPosition) {
        return mLabeledItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mLabeledItems.get(groupPosition).get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_label, parent, false);
        }
        TextView label = (TextView) convertView.findViewById(R.id.labelTextView);
        label.setText(mLabels.get(groupPosition));

        TextView sum = (TextView) convertView.findViewById(R.id.itemCount);
        sum.setText(String.valueOf(mLabeledItems.get(groupPosition).size()));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_checked_textview, parent, false);
        }

        Item item = mLabeledItems.get(groupPosition).get(childPosition);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static final String TAG = "PrepareItemsAdapter";

    @Override
    public void notifyDataSetInvalidated() {
        Dog.d(TAG, "notifyDataSetInvalidated: ");
        new GetFromDb().execute();
    }

    @Override
    public void onRecordUpdated(RecordType type, long id) {
        Dog.d(TAG, "onRecordUpdated: type= " + type + " id= " + id);
        switch (type) {
            default:
            case ITEM: {
                // TODO improve performance by updating only changed item
                notifyDataSetInvalidated();
                break;
            }
            case LABEL: {
                notifyDataSetInvalidated();
                break;
            }
            case ITEM_LIST: {
                notifyDataSetInvalidated();
                break;
            }
        }
    }

    @Override
    public void onRecordAdded(RecordType type, long id) {
        Dog.d(TAG, "onRecordAdded: type= " + type + " id= " + id);

        switch (type) {
            default:
            case ITEM: {
                Item item = Item.findById(Item.class, id);
                if (item != null) {
                    notifyDataSetChanged();
                }
                break;
            }
            case ITEM_LIST: {
                notifyDataSetInvalidated();
                break;
            }
        }
    }

    @Override
    public void onRecordDeleted(RecordType type, long id) {
        Dog.d(TAG, "onRecordDeleted: type= " + type + " id= " + id);
        switch (type) {
            default:
            case ITEM: {
                for (List<Item> l : mLabeledItems) {
                    for (Item i : l) {
                        if (i.getId() == id) {
                            l.remove(i);
                            notifyDataSetChanged();
                        }
                    }
                }
                break;
            }
            case ITEM_LIST: {
                notifyDataSetInvalidated();
                break;
            }
        }
    }

    private class GetFromDb extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            final List<Item> itemList = Item.listAll(Item.class);
            Collections.sort(itemList, new ItemComparators.ByName());

            mLabels.clear();
            for (Item item : itemList) {
                String label = item.getLabelName();
                if (!mLabels.contains(label)) {
                    mLabels.add(label);
                }
            }
            mLabeledItems.clear();

            for (String l : mLabels) {
                List<Item> il = new ArrayList<>();
                for (Item item : itemList) {
                    String itemLabel = item.getLabelName();
                    if ((itemLabel != null) && itemLabel.equals(l)) {
                        il.add(item);
                    }
                }
                mLabeledItems.add(il);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }
}
