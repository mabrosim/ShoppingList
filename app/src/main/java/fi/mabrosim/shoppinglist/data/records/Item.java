package fi.mabrosim.shoppinglist.data.records;

import android.content.Context;
import android.widget.Checkable;

import com.orm.dsl.Ignore;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fi.mabrosim.shoppinglist.data.ProtoEntity;
import fi.mabrosim.shoppinglist.data.Utils;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbItem;
import fi.mabrosim.shoppinglist.ui.common.SingleToast;
import fi.mabrosim.shoppinglist.utils.TimeUtils;

public class Item extends ProtoEntity<PbItem> implements Checkable {

    private String  name          = "";
    private String  quantity      = "";
    private boolean checked       = false;
    private String  logChecked    = "";
    private String  logUnchecked  = "";
    private long    checkedTime   = -1L;
    private long    uncheckedTime = -1L;
    private String  labelName     = "";

    private ItemList itemList;

    @Ignore
    private final List<Long> mLogCheckedList;
    @Ignore
    private boolean mLogCheckedListNotLoadedFromString = true;
    @Ignore
    private final List<Long> mLogUncheckedList;
    @Ignore
    private boolean mLogUncheckedListNotLoadedFromString = true;

    public Item(String name) {
        this();
        this.name = name;
    }

    public Item() {
        mLogCheckedList = Collections.synchronizedList(new ArrayList<Long>());
        mLogUncheckedList = Collections.synchronizedList(new ArrayList<Long>());
    }

    public Item(InputStream is) throws IOException {
        this(PbItem.parseFrom(is));
    }

    public Item(PbItem pbItem) {
        this();
        name = pbItem.getName();
        checked = pbItem.getIsChecked();
        quantity = pbItem.getQuantity();
        checkedTime = pbItem.getCheckedTime();
        uncheckedTime = pbItem.getUncheckedTime();

        if (pbItem.getCheckedCount() > 0) {
            getLogCheckedList().addAll(pbItem.getCheckedList());
        }
        if (pbItem.getUncheckedCount() > 0) {
            getLogUncheckedList().addAll(pbItem.getUncheckedList());
        }
    }

    @Override
    public void setChecked(boolean checked) {
        Date timestamp = new Date(System.currentTimeMillis());
        this.checked = checked;
        if (checked) {
            uncheckedTime = -1L;
            checkedTime = TimeUtils.dateAsLong(timestamp);
            addLogCheckedTimestamp(checkedTime);
        } else {
            checkedTime = -1L;
            uncheckedTime = TimeUtils.dateAsLong(timestamp);
            addLogUncheckedTimestamp(uncheckedTime);
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    public void setUncheckedWithoutLogging() {
        this.checked = false;
    }

    public void toggleOnClick(Context context) {
        toggle();
        if (isChecked()) {
            SingleToast.get().showText(context, "+ " + getName());
        } else {
            SingleToast.get().showText(context, "- " + getName());
        }
        saveAndBroadcast(context);
    }

    @Override
    public PbItem toPbObject() {
        PbItem.Builder builder = PbItem.newBuilder();

        builder.clearId();

        builder.setName(name)
                .setIsChecked(checked)
                .setQuantity(quantity);

        if (checkedTime != -1) {
            builder.setCheckedTime(checkedTime);
        } else {
            builder.clearCheckedTime();
        }

        if (uncheckedTime != -1) {
            builder.setUncheckedTime(uncheckedTime);
        } else {
            builder.clearUncheckedTime();
        }

        synchronized (mLogCheckedList) {
            loadLogCheckedList();
            builder.addAllChecked(mLogCheckedList);
        }
        synchronized (mLogUncheckedList) {
            loadLogUncheckedList();
            builder.addAllUnchecked(mLogUncheckedList);
        }
        return builder.build();
    }

    @Override
    public long save() {
        logsToJson();
        return super.save();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabel(String label) {
        this.labelName = label;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }

    @SuppressWarnings("unused")
    public ItemList getItemList() {
        return itemList;
    }

    public static List<Item> findByItemListId(long id) {
        return find(Item.class, "ITEM_LIST = ?", String.valueOf(id));
    }

    /**
     * Add checked timestamp to the array list
     *
     * @param timestamp timestamp in yyyyMMddHHmmss format
     */
    private void addLogCheckedTimestamp(Long timestamp) {
        getLogCheckedList().add(timestamp);
    }

    /**
     * Add unchecked timestamp to the array list
     *
     * @param timestamp timestamp in yyyyMMddHHmmss format
     */
    private void addLogUncheckedTimestamp(Long timestamp) {
        getLogUncheckedList().add(timestamp);
    }

    public List<Long> getLogCheckedList() {
        loadLogCheckedList();
        return mLogCheckedList;
    }

    public List<Long> getLogUncheckedList() {
        loadLogUncheckedList();
        return mLogUncheckedList;
    }

    private void loadLogCheckedList() {
        if (mLogCheckedListNotLoadedFromString) {
            mLogCheckedList.addAll(Utils.jsonArrayStringToLongList(logChecked));
            mLogCheckedListNotLoadedFromString = false;
        }
    }

    private void loadLogUncheckedList() {
        if (mLogUncheckedListNotLoadedFromString) {
            mLogUncheckedList.addAll(Utils.jsonArrayStringToLongList(logUnchecked));
            mLogUncheckedListNotLoadedFromString = false;
        }
    }

    private void logsToJson() {
        synchronized (mLogCheckedList) {
            loadLogCheckedList();
            logChecked = new JSONArray(mLogCheckedList).toString();
        }
        synchronized (mLogUncheckedList) {
            loadLogUncheckedList();
            logUnchecked = new JSONArray(mLogUncheckedList).toString();
        }
    }
}
