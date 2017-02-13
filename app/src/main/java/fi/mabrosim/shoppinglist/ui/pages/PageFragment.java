package fi.mabrosim.shoppinglist.ui.pages;

import android.content.Intent;
import android.support.v4.app.ListFragment;

import fi.mabrosim.shoppinglist.ui.editors.EditItemActivity;
import fi.mabrosim.shoppinglist.utils.Actions;

public abstract class PageFragment extends ListFragment implements RecordListener {

    abstract void resetData();

    PageFragment() {
        setHasOptionsMenu(true);
    }

    void launchEditItemActivity(long id) {
        Intent intent = new Intent(getActivity(), EditItemActivity.class);
        intent.putExtra(Actions.EXTRA_RECORD_ID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }
}
