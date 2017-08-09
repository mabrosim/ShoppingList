package fi.mabrosim.shoppinglist.data;

import java.util.ArrayList;
import java.util.List;

import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbItem;
import fi.mabrosim.shoppinglist.protobuf.Protos.PbLabel;

public class Label extends Proto<PbLabel> {
    private String name = "";

    private final List<Item> mItems = new ArrayList<>();

    public Label(String labelName) {
        name = labelName;
    }

    public Label(PbLabel pbLabel) {
        name = pbLabel.getName();

        for (PbItem pbItem : pbLabel.getItemList()) {
            Item item = new Item(pbItem);
            item.setLabel(name);
            mItems.add(item);
        }
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return mItems;
    }

    @Override
    public PbLabel toPbObject() {
        PbLabel.Builder builder = PbLabel.newBuilder();
        builder.setName(name);

        List<Item> items = Item.find(Item.class, "LABEL_NAME LIKE ?", name);
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
