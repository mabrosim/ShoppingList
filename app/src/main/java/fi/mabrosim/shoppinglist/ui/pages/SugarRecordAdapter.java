package fi.mabrosim.shoppinglist.ui.pages;

import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.RecordType;
import fi.mabrosim.shoppinglist.utils.Dog;

abstract class SugarRecordAdapter<T extends SugarRecord> extends BaseAdapter implements RecordListener {
    private final Class<T> mType;
    final List<T> mItems = new ArrayList<>();
    private String mFilter;

    SugarRecordAdapter(Class<T> type) {
        mType = type;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    protected abstract void addItems(List<T> items, String filter);

    @Override
    public void notifyDataSetInvalidated() {
        Dog.d(getClass().getSimpleName(), "notifyDataSetInvalidated: ");
        new GetFromDb().execute(mFilter);
    }

    void notifyDataSetInvalidated(String filter) {
        mFilter = filter;
        new GetFromDb().execute(mFilter);
    }

    @Override
    public void onRecordUpdated(RecordType type, long id) {
        T item = T.findById(mType, id);
        Dog.d(getClass().getSimpleName(), "onRecordUpdated: " + id + " " + item);
        if (item != null) {
            int index = -1;
            for (T i : mItems) {
                if (i.getId() == id) {
                    index = mItems.indexOf(i);
                    break;
                }
            }
            if (index != -1) {
                mItems.set(index, item);
            } else {
                mItems.add(item);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onRecordAdded(RecordType type, long id) {
        T item = T.findById(mType, id);
        Dog.d(getClass().getSimpleName(), "onRecordAdded: " + id + " " + item);
        if (item != null) {
            mItems.add(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onRecordDeleted(RecordType type, long id) {
        Dog.d(getClass().getSimpleName(), "onRecordDeleted: " + id);
        int index = -1;
        for (T i : mItems) {
            if (i.getId() == id) {
                index = mItems.indexOf(i);
                break;
            }
        }
        if (index != -1) {
            mItems.remove(index);
            notifyDataSetChanged();
        }
    }

    private class GetFromDb extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Dog.d(this.getClass().getSimpleName(), "doInBackground: ");
            List<T> items = new ArrayList<>();
            addItems(items, params.length > 0 ? params[0] : null);
            mItems.clear();
            mItems.addAll(items);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }
}
