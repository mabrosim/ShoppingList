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
    private String name = "List";

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
        if (name.isEmpty()) {
            name = "List";
        }
        for (PbLabel pbLabel : pbItemList.getLabelList()) {
            mLabels.add(new Label(pbLabel));
        }
    }

    private static final String TAG = "ItemList";

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

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        for (Label label : mLabels) {
            items.addAll(label.getItems());
        }
        return items;
    }

    public List<Label> getLabels() {
        return mLabels;
    }
}
