package fi.mabrosim.shoppinglist.data.records;

import com.orm.dsl.Ignore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.ProtoEntity;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbItem;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbLabel;

public class Label extends ProtoEntity<PbLabel> {
    private       String     name   = "Other";
    @Ignore
    private final List<Item> mItems = new ArrayList<>();

    public Label() {
    }

    public Label(String labelName) {
        name = labelName;
    }

    public Label(InputStream is) throws IOException {
        this(PbLabel.parseFrom(is));
    }

    public Label(PbLabel pbLabel) {
        this();
        name = pbLabel.getName();
        setId(pbLabel.getId());

        for (PbItem pbItem : pbLabel.getItemList()) {
            Item item = new Item(pbItem);
            item.setLabel(this);
            mItems.add(item);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return mItems;
    }

    @Override
    public PbLabel toPbObject() {
        PbLabel.Builder builder = PbLabel.newBuilder();
        builder.setName(name);

        // XXX hack
        List<Item> items;

        if (getId() != null) {
            builder.setId(getId());
            items = Item.findByLabelId(getId());
        } else {
            items = Item.listAll(Item.class);
        }
        for (Item item : items) {
            builder.addItem(item.toPbObject());
        }
        return builder.build();
    }

    /**
     * Returns an array if associated item names, the label name is added at zeroth index.
     *
     * @return an array ot item names
     */
    public String[] getItemNamesWithLabel() {
        int itemNamesCount = mItems.size();

        String[] entries = new String[1 + itemNamesCount];
        entries[0] = name;
        for (int i = 0; i < itemNamesCount; i++) {
            entries[1 + i] = mItems.get(i).getName();
        }
        return entries;
    }
}
