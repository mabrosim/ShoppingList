package fi.mabrosim.shoppinglist.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.tasks.ClearChoicesTask;
import fi.mabrosim.shoppinglist.tasks.DeleteItemListTask;
import fi.mabrosim.shoppinglist.tasks.DoTheMagicTask;
import fi.mabrosim.shoppinglist.tasks.SetCurrentItemListTask;
import fi.mabrosim.shoppinglist.ui.editors.AddItemActivity;
import fi.mabrosim.shoppinglist.ui.editors.EditItemListActivity;
import fi.mabrosim.shoppinglist.ui.pages.MainViewPager;
import fi.mabrosim.shoppinglist.ui.pages.MainViewPagerAdapter;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.Dog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MainViewPager mViewPager;
    private String mCurrentListName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                updateNavigationDrawer();
            }
        };
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        ItemList list = ItemList.findCurrentList();
        if (list != null) {
            mCurrentListName = list.getName();
        }
        setTitleByPosition(0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new MainActivityNavigationListener(this));
            updateNavigationDrawer();
        }

        mViewPager = (MainViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTitleByPosition(position);
            }
        });
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_ALL_DELETED);
        intentFilter.addAction(Actions.ACTION_RECORD_UPDATED);
        intentFilter.addAction(Actions.ACTION_RECORD_ADDED);
        intentFilter.addAction(Actions.ACTION_RECORD_DELETED);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dog.d(TAG, "onOptionsItemSelected: ");
        int id = item.getItemId();

        switch (id) {
            case R.id.action_uncheck_all: {
                new ClearChoicesTask(this).execute();
                return true;
            }
            case R.id.action_add_item: {
                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_magic: {
                new DoTheMagicTask(this).execute();
                return true;
            }
            case R.id.action_edit_list_name: {
                List<ItemList> lists = ItemList.find(ItemList.class, "NAME LIKE ?", mCurrentListName);
                if (lists.size() > 0) {
                    Intent intent = new Intent(this, EditItemListActivity.class);
                    intent.putExtra(Actions.EXTRA_RECORD_ID, lists.get(0).getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.action_delete_list: {
                List<ItemList> lists = ItemList.find(ItemList.class, "NAME LIKE ?", mCurrentListName);
                if (lists.size() > 0) {
                    new DeleteItemListTask(this).execute(lists.get(0).getId());
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    void showAllItems() {
        mViewPager.setCurrentItem(1);
        setTitleByPosition(1);
    }

    void setActiveList(String name) {
        if (mCurrentListName.equals(name)) {
            mViewPager.setCurrentItem(0);
            setTitleByPosition(0);
        } else {
            mCurrentListName = name;
            new SetCurrentItemListTask(this.getApplicationContext()).execute(name);
        }
    }

    private void updateNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {

            final Menu menu = navigationView.getMenu();
            menu.clear();

            Iterator<ItemList> itemsIterator = ItemList.findAll(ItemList.class);

            while (itemsIterator.hasNext()) {
                ItemList il = itemsIterator.next();
                MenuItem mi = menu.add(0, R.id.nav_list_name, 0, il.getName());
                if (mCurrentListName.equals(il.getName())) {
                    mi.setChecked(true);
                } else {
                    mi.setChecked(false);
                }
                mi.setIcon(R.drawable.ic_shopping_cart_black_24dp);
            }
            navigationView.setCheckedItem(R.id.nav_settings);
            navigationView.inflateMenu(R.menu.activity_main_drawer);

            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_list_name)).setText(mCurrentListName);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RecordType type = (RecordType) intent.getSerializableExtra(Actions.EXTRA_RECORD_TYPE);
            long listId = intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L);

            if (RecordType.ITEM_LIST.equals(type) && listId != 0L) {
                String action = intent.getAction();
                switch (action) {
                    case Actions.ACTION_RECORD_UPDATED: {
                        mCurrentListName = ItemList.findById(ItemList.class, listId).getName();
                        switchToActiveListFragment();
                        ((MainViewPagerAdapter) mViewPager.getAdapter()).resetRegisteredFragment(0);
                        break;
                    }
                    case Actions.ACTION_RECORD_ADDED: {
                        mCurrentListName = ItemList.findById(ItemList.class, listId).getName();
                        switchToActiveListFragment();
                        ((MainViewPagerAdapter) mViewPager.getAdapter()).resetRegisteredFragments();
                        break;
                    }
                    case Actions.ACTION_RECORD_DELETED: {
                        ItemList itemList = ItemList.first(ItemList.class);
                        mCurrentListName = itemList != null ? itemList.getName() : "";
                        switchToActiveListFragment();
                        ((MainViewPagerAdapter) mViewPager.getAdapter()).resetRegisteredFragments();
                        break;
                    }

                    default: {
                        break;
                    }
                }
            }
        }
    };

    private void switchToActiveListFragment() {
        mViewPager.setCurrentItem(0);
        setTitle(mCurrentListName);
        updateNavigationDrawer();
    }

    private void setTitleByPosition(int position) {
        switch (position) {
            case 0:
                setTitle(mCurrentListName);
                break;
            case 1:
                setTitle(R.string.prepare_list);
                break;
            default:
                break;
        }
    }
}
