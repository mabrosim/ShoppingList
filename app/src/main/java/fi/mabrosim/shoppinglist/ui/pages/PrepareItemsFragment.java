package fi.mabrosim.shoppinglist.ui.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.ui.editors.EditLabelActivity;
import fi.mabrosim.shoppinglist.utils.Actions;

public class PrepareItemsFragment extends PageFragment<PrepareItemsAdapter> {
    public PrepareItemsFragment() {
        super(new PrepareItemsAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.labels_expandable_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExpandableListView listView = (ExpandableListView) getListView();
        listView.setAdapter(mAdapter);
        listView.setFastScrollEnabled(true);
        listView.setOnChildClickListener(mOnChildClickListener);
        listView.setOnItemLongClickListener(mOnItemLongClickListener);
        resetData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void launchEditLabelActivity(String name, String[] ids) {
        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, EditLabelActivity.class);
            intent.putExtra(Actions.EXTRA_LABEL_NAME, name);
            intent.putExtra(Actions.EXTRA_RECORD_IDS, ids);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    private final ExpandableListView.OnItemLongClickListener mOnItemLongClickListener = new ExpandableListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final int itemType = ExpandableListView.getPackedPositionType(id);

            if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                launchEditItemActivity(ExpandableListView.getPackedPositionChild(id));
                return true;
            } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                launchEditLabelActivity(mAdapter.getGroup(groupPosition), mAdapter.getChildrenIds(groupPosition));
                return true;
            } else {
                // don't consume the click
                return false;
            }
        }
    };

    private final ExpandableListView.OnChildClickListener mOnChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            mAdapter.getChild(groupPosition, childPosition).toggleOnClick(getContext());
            return true;
        }
    };
}
