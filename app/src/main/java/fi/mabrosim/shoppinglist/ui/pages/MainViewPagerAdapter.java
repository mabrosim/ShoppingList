package fi.mabrosim.shoppinglist.ui.pages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.mabrosim.shoppinglist.utils.Dog;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MainViewPagerAdapter";

    private final List<FragmentType> mFragmentTypes;
    private final SparseArray<PageFragment> mRegisteredFragments = new SparseArray<>();

    private static final Map<FragmentType, Class<? extends PageFragment>> TYPE_TO_FRAGMENT_CLASS_MAP;

    private enum FragmentType {
        PREPARE,
        AT_THE_SHOP
    }

    static {
        Map<FragmentType, Class<? extends PageFragment>> m = new HashMap<>();
        m.put(FragmentType.PREPARE, PrepareItemsFragment.class);
        m.put(FragmentType.AT_THE_SHOP, AtTheShopFragment.class);
        TYPE_TO_FRAGMENT_CLASS_MAP = Collections.unmodifiableMap(m);
    }

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentTypes = Arrays.asList(FragmentType.PREPARE, FragmentType.AT_THE_SHOP);
    }

    @Override
    public int getCount() {
        return mFragmentTypes.size();
    }

    @Override
    public Fragment getItem(int position) {
        return newInstance(mFragmentTypes.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PageFragment fragment = (PageFragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.delete(position);
        super.destroyItem(container, position, object);
    }

    PageFragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    public void resetRegisteredFragments() {
        int size = mRegisteredFragments.size();
        for (int i = 0; i < size; i++) {
            mRegisteredFragments.valueAt(i).resetData();
        }
    }

    private static PageFragment newInstance(FragmentType type) {
        PageFragment fragment = null;
        Class c = TYPE_TO_FRAGMENT_CLASS_MAP.get(type);
        if (c != null) {
            try {
                fragment = (PageFragment) c.newInstance();
            } catch (InstantiationException e) {
                Dog.e(TAG, "InstantiationException", e);
            } catch (IllegalAccessException e) {
                Dog.e(TAG, "IllegalAccessException", e);
            }
        }
        return fragment;
    }
}
