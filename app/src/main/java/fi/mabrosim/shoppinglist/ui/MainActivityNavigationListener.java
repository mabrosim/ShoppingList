package fi.mabrosim.shoppinglist.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.tasks.ShareProtoFileTask;
import fi.mabrosim.shoppinglist.ui.editors.AddItemListActivity;

class MainActivityNavigationListener implements NavigationView.OnNavigationItemSelectedListener {
    private final MainActivity mActivity;

    MainActivityNavigationListener(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_prepare_list:
                mActivity.showAllItems();
                break;
            case R.id.nav_share_pb:
                new ShareProtoFileTask(mActivity).execute();
                break;
            case R.id.nav_import: {
                Intent intent = new Intent(mActivity, ImportFromFileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                break;
            }
            case R.id.nav_settings: {
                Intent intent = new Intent(mActivity, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                break;
            }
            case R.id.nav_add_list: {
                mActivity.startActivity(new Intent(mActivity, AddItemListActivity.class));
                break;
            }
            case R.id.nav_list_name: {
                mActivity.setActiveList((String) item.getTitle());
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
