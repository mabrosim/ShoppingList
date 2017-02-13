package fi.mabrosim.shoppinglist.ui.pages;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.Item;

public class AtTheShopFragment extends PageFragment {
    private final AtTheShopAdapter mAdapter;

    public AtTheShopFragment() {
        mAdapter = new AtTheShopAdapter();
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (isAdded()) {
                    setListShown(true);
                }
            }
        });
        setListAdapter(mAdapter);
    }

    @Override
    public void onRecordUpdated(RecordType type, long id) {
        mAdapter.onRecordUpdated(type, id);
    }

    @Override
    public void onRecordAdded(RecordType type, long id) {
        mAdapter.onRecordAdded(type, id);
    }

    @Override
    public void onRecordDeleted(RecordType type, long id) {
        mAdapter.onRecordDeleted(type, id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setFastScrollEnabled(true);
        setEmptyText("Empty");
        ((TextView) listView.getEmptyView()).setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.list_view_text_empty));
        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setOnItemLongClickListener(mOnItemLongClickListener);
        resetData();
    }

    @Override
    void resetData() {
        setListShown(false);
        mAdapter.notifyDataSetInvalidated();
    }

    private final OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            launchEditItemActivity(id);
            return true;
        }
    };

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Item item = (Item) getListAdapter().getItem(position);
            item.toggleOnClick(getContext());
        }
    };
}
