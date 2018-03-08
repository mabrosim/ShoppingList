package fi.mabrosim.shoppinglist.ui.pages;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.ui.editors.EditItemActivity;
import fi.mabrosim.shoppinglist.utils.Actions;

public abstract class PageFragment<T extends RecordListener> extends ListFragment implements RecordListener {
    final         T                  mAdapter;
    private final SearchItemsAdapter mSearchListAdapter;

    private View     mSearchListBg;
    private MenuItem mSearchMenuItem;

    PageFragment(T adapter) {
        mAdapter = adapter;
        setHasOptionsMenu(true);

        mSearchListAdapter = new SearchItemsAdapter();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        ListView searchListView = view.findViewById(R.id.searchList);
        searchListView.setAdapter(mSearchListAdapter);
        searchListView.setOnItemClickListener(mOnItemClickListener);
        searchListView.setOnItemLongClickListener(mOnItemLongClickListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_search, menu);
        inflater.inflate(R.menu.add_item, menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);

        mSearchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
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

        Activity activity = getActivity();

        if (activity != null) {
            SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

            final SearchView searchView = mSearchMenuItem != null ? (SearchView) mSearchMenuItem.getActionView() : null;

            if (searchManager != null && searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
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
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    final void resetData() {
        if (mAdapter instanceof AtTheShopAdapter) {
            ((AtTheShopAdapter) mAdapter).notifyDataSetInvalidated();
        } else if (mAdapter instanceof PrepareItemsAdapter) {
            ((PrepareItemsAdapter) mAdapter).notifyDataSetInvalidated();
        }
        mSearchListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onRecordAdded(RecordType type, long id) {
        mAdapter.onRecordAdded(type, id);
        if (View.VISIBLE == mSearchListBg.getVisibility()) {
            mSearchListAdapter.onRecordAdded(type, id);
        }
    }

    @Override
    public void onRecordUpdated(RecordType type, long id) {
        mAdapter.onRecordUpdated(type, id);
        if (View.VISIBLE == mSearchListBg.getVisibility()) {
            mSearchListAdapter.onRecordUpdated(type, id);
        }
    }

    @Override
    public void onRecordDeleted(RecordType type, long id) {
        mAdapter.onRecordDeleted(type, id);
        if (View.VISIBLE == mSearchListBg.getVisibility()) {
            mSearchListAdapter.onRecordDeleted(type, id);
        }
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

    void launchEditItemActivity(long id) {
        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, EditItemActivity.class);
            intent.putExtra(Actions.EXTRA_RECORD_ID, id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    final OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            launchEditItemActivity(id);
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
