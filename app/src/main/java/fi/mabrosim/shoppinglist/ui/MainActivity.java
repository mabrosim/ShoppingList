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
import android.widget.TextView;
import android.widget.Toast;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.tasks.ClearChoicesTask;
import fi.mabrosim.shoppinglist.ui.editors.AddItemActivity;
import fi.mabrosim.shoppinglist.ui.pages.MainViewPager;
import fi.mabrosim.shoppinglist.ui.pages.MainViewPagerAdapter;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.Dog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MainViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new MainActivityNavigationListener(this));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.listName)).setText("List 1");
        }

        mViewPager = (MainViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTitleByPosition(position);
            }
        });
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        setTitleByPosition(0);
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
        if (id == R.id.action_uncheck_all) {
            new ClearChoicesTask(this).execute();
            return true;
        }
        if (id == R.id.action_add_item) {
            startActivity(new Intent(this, AddItemActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mImportCompletedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mImportCompletedReceiver, new IntentFilter(Actions.ACTION_IMPORT_COMPLETED));
    }

    void showAllItems() {
        mViewPager.setCurrentItem(0);
        setTitle(R.string.prepare_list);
    }

    void showCheckedItems() {
        mViewPager.setCurrentItem(1);
        setTitle(R.string.at_the_shop);
    }

    private final BroadcastReceiver mImportCompletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Actions.ACTION_IMPORT_COMPLETED.equals(intent.getAction())) {
                ((MainViewPagerAdapter) mViewPager.getAdapter()).resetRegisteredFragments();
                Toast.makeText(context, "Items imported: " + Item.count(Item.class), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void setTitleByPosition(int position) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        switch (position) {
            case 0:
                setTitle(R.string.prepare_list);
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_prepare_list);
                }
                break;
            case 1:
                setTitle(R.string.at_the_shop);
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_shopping);
                }
                break;
            default:
                break;
        }
    }
}
