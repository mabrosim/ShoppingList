package fi.mabrosim.shoppinglist.ui.pages;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.ui.editors.EditLabelActivity;
import fi.mabrosim.shoppinglist.utils.Actions;

public class PrepareItemsFragment extends PageFragment {

    private final SearchItemsAdapter  mSearchListAdapter;
    private final PrepareItemsAdapter mAdapter;

    private View     mSearchListBg;
    private MenuItem mSearchMenuItem;

    public PrepareItemsFragment() {
        mAdapter = new PrepareItemsAdapter();
        mSearchListAdapter = new SearchItemsAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.labels_expandable_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExpandableListView listView = (ExpandableListView) getListView();
        listView.setAdapter(mAdapter);
        listView.setFastScrollEnabled(true);
        listView.setOnChildClickListener(mOnChildClickListener);
        listView.setOnItemLongClickListener(mOnItemLongClickListener);
        resetData();

        mSearchListBg = view.findViewById(R.id.searchListBg);
        mSearchListBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchView searchView = mSearchMenuItem != null ? (SearchView) mSearchMenuItem.getActionView() : null;
                if (searchView != null && !searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                if (mSearchMenuItem != null) {
                    mSearchMenuItem.collapseActionView();
                }
            }
        });
        ListView searchListView = (ListView) view.findViewById(R.id.searchList);
        searchListView.setAdapter(mSearchListAdapter);
        searchListView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    void resetData() {
        mAdapter.notifyDataSetInvalidated();
        mSearchListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (mSearchListBg != null) {
                mSearchListBg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_item, menu);
        inflater.inflate(R.menu.item_search, menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchListBg.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchListAdapter.notifyDataSetInvalidated(null);
                mSearchListBg.setVisibility(View.GONE);
                return true;
            }
        });

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = mSearchMenuItem != null ? (SearchView) mSearchMenuItem.getActionView() : null;

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (!searchView.isIconified()) {
                        searchView.setIconified(true);
                    }
                    mSearchMenuItem.collapseActionView();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    mSearchListAdapter.notifyDataSetInvalidated(s);
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onRecordUpdated(RecordType type, long id) {
        mAdapter.onRecordUpdated(type, id);
        mSearchListAdapter.onRecordUpdated(type, id);
    }

    @Override
    public void onRecordAdded(RecordType type, long id) {
        mAdapter.onRecordAdded(type, id);
        mSearchListAdapter.onRecordAdded(type, id);
    }

    @Override
    public void onRecordDeleted(RecordType type, long id) {
        mAdapter.onRecordDeleted(type, id);
        mSearchListAdapter.onRecordDeleted(type, id);
    }

    private void launchEditLabelActivity(String name) {
        Intent intent = new Intent(getActivity(), EditLabelActivity.class);
        intent.putExtra(Actions.EXTRA_LABEL_NAME, name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }

    private final ExpandableListView.OnItemLongClickListener mOnItemLongClickListener = new ExpandableListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final int itemType = ExpandableListView.getPackedPositionType(id);

            if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                launchEditItemActivity(ExpandableListView.getPackedPositionChild(id));
                return true;
            } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                launchEditLabelActivity(mAdapter.getGroup(ExpandableListView.getPackedPositionGroup(id)));
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

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSearchListAdapter.getItem(position).toggleOnClick(getContext());
        }
    };
}
