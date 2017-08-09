package fi.mabrosim.shoppinglist.ui.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.Dog;

public class MainViewPager extends ViewPager {
    private static final String TAG = "MainViewPager";

    private final OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener();
    private final IntentFilter         mIntentFilter;
    private       MainViewPagerAdapter mAdapter;

    public MainViewPager(Context context) {
        this(context, null);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Actions.ACTION_RECORD_UPDATED);
        mIntentFilter.addAction(Actions.ACTION_RECORDS_UPDATED);
        mIntentFilter.addAction(Actions.ACTION_RECORD_ADDED);
        mIntentFilter.addAction(Actions.ACTION_RECORD_DELETED);
        mIntentFilter.addAction(Actions.ACTION_ALL_DELETED);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapter = (MainViewPagerAdapter) adapter;
        super.setAdapter(mAdapter);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    public void resetActiveFragment() {
        mAdapter.resetRegisteredFragment(getCurrentItem());
    }

    public void resetRegisteredFragments() {
        mAdapter.resetRegisteredFragments();
    }

    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Fragment fragment = mAdapter.getRegisteredFragment(position);
            if (fragment != null) {
                fragment.setUserVisibleHint(true);
            }
            // Notify other Fragments that they are not visible.
            for (int j = 0; j < mAdapter.getCount(); j++) {
                fragment = mAdapter.getRegisteredFragment(j);
                if (j != position && fragment != null) {
                    if (fragment.getUserVisibleHint()) {
                        fragment.setUserVisibleHint(false);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRecordListener, mIntentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRecordListener);
        super.onDetachedFromWindow();
    }

    private final BroadcastReceiver mRecordListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            RecordType type = (RecordType) intent.getSerializableExtra(Actions.EXTRA_RECORD_TYPE);

            switch (action) {
                case Actions.ACTION_RECORD_UPDATED: {
                    long id = intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L);
                    Dog.d(TAG, "onReceive: " + intent.getAction() + " " + type + " " + id);

                    for (int j = 0; j < mAdapter.getCount(); j++) {
                        PageFragment fragment = mAdapter.getRegisteredFragment(j);
                        if (fragment != null) {
                            fragment.onRecordUpdated(type, id);
                        }
                    }
                    break;
                }

                case Actions.ACTION_RECORDS_UPDATED: {
                    long ids[] = intent.getLongArrayExtra(Actions.EXTRA_RECORD_IDS);

                    for (int j = 0; j < mAdapter.getCount(); j++) {
                        PageFragment fragment = mAdapter.getRegisteredFragment(j);
                        if (fragment != null) {
                            if (ids == null || ids.length == 0) {
                                fragment.resetData();
                            } else {
                                for (long id : ids) {
                                    fragment.onRecordUpdated(type, id);
                                }
                            }
                        }
                    }
                    break;
                }
                case Actions.ACTION_RECORD_ADDED: {
                    long id = intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L);
                    Dog.d(TAG, "onReceive: " + intent.getAction() + " " + type + " " + id);

                    for (int j = 0; j < mAdapter.getCount(); j++) {
                        PageFragment fragment = mAdapter.getRegisteredFragment(j);
                        if (fragment != null) {
                            fragment.onRecordAdded(type, id);
                        }
                    }
                    break;
                }
                case Actions.ACTION_RECORD_DELETED: {
                    long id = intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L);
                    Dog.d(TAG, "onReceive: " + intent.getAction() + " " + type + " " + id);

                    for (int j = 0; j < mAdapter.getCount(); j++) {
                        PageFragment fragment = mAdapter.getRegisteredFragment(j);
                        if (fragment != null) {
                            fragment.onRecordDeleted(type, id);
                        }
                    }
                    break;
                }
                default: {
                    for (int j = 0; j < mAdapter.getCount(); j++) {
                        PageFragment fragment = mAdapter.getRegisteredFragment(j);
                        if (fragment != null) {
                            fragment.resetData();
                        }
                    }
                    break;
                }
            }
        }
    };
}
