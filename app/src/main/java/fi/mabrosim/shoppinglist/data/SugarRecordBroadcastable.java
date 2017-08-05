package fi.mabrosim.shoppinglist.data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.orm.SugarRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.Dog;

abstract class SugarRecordBroadcastable extends SugarRecord {

    private static final Map<Class<? extends SugarRecord>, RecordType> RECORD_CLASS_TO_TYPE_MAP;

    static {
        Map<Class<? extends SugarRecord>, RecordType> m = new HashMap<>();
        m.put(Item.class, RecordType.ITEM);
        m.put(ItemList.class, RecordType.ITEM_LIST);
        RECORD_CLASS_TO_TYPE_MAP = Collections.unmodifiableMap(m);
    }

    public void saveAndBroadcast(Context context) {
        Long id = getId();
        final String action;

        Dog.d(TAG, "saveAndBroadcast: " + getId());
        if (id != null) {
            save();
            action = Actions.ACTION_RECORD_UPDATED;
        } else {
            id = save();
            action = Actions.ACTION_RECORD_ADDED;
        }
        broadcastAction(context, action, id, RECORD_CLASS_TO_TYPE_MAP.get(getClass()));
    }

    public void deleteAndBroadcast(Context context) {
        Long id = getId();
        if (id != null) {
            delete();
            broadcastAction(context, Actions.ACTION_RECORD_DELETED, id, RECORD_CLASS_TO_TYPE_MAP.get(getClass()));
        }
    }

    private static final String TAG = "RecordBroadcastable";

    private static void broadcastAction(Context context, String action, long id, RecordType type) {
        Dog.d(TAG, "broadcastAction: action = [" + action + "], id = [" + id + "], type = [" + type + "]");
        Intent intent = new Intent(action);
        intent.putExtra(Actions.EXTRA_RECORD_ID, id);
        intent.putExtra(Actions.EXTRA_RECORD_TYPE, type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
