package fi.mabrosim.shoppinglist.data.records;

import com.orm.dsl.Ignore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.data.ProtoEntity;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbItemList;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbLabel;

public class ItemList extends ProtoEntity<PbItemList> {
    private String  name      = "";
    private boolean isCurrent = false;

    @Ignore
    private final List<Label> mLabels = new ArrayList<>();

    public ItemList() {
    }

    public ItemList(InputStream is) throws IOException {
        this(PbItemList.parseFrom(is));
    }

    public ItemList(PbItemList pbItemList) {
        this();
        name = pbItemList.getName();
        for (PbLabel pbLabel : pbItemList.getLabelList()) {
            mLabels.add(new Label(pbLabel));
        }
    }

    @Override
    public PbItemList toPbObject() {
        PbItemList.Builder builder = PbItemList.newBuilder();
        builder.setName(name);
        if (getId() != null) {
            builder.setId(getId());
        }

        final List<String> labels = new ArrayList<>();
        final List<Item> items = Item.listAll(Item.class);

        for (Item item : items) {
            String label = item.getLabelName();
            if (!labels.contains(label)) {
                labels.add(label);
            }
        }

        for (String label : labels) {
            builder.addLabel(new Label(label).toPbObject());
        }

        return builder.build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Label> getLabels() {
        return mLabels;
    }

    public static ItemList findCurrentList() {
        List<ItemList> lists = ItemList.find(ItemList.class, "IS_CURRENT = ?", String.valueOf(1));

        if (lists.size() > 0) {
            return lists.get(0);
        } else {
            return ItemList.first(ItemList.class);
        }
    }

    public void setCurrent(boolean current) {
        this.isCurrent = current;
    }

    public boolean isCurrent() {
        return this.isCurrent;
    }

    public List<Item> findItems() {
        return Item.find(Item.class, "ITEM_LIST=?", String.valueOf(getId()));
    }

    @Override
    public boolean delete() {
        Item.deleteAll(Item.class, "ITEM_LIST=?", String.valueOf(getId()));
        return super.delete();
    }
}
