package fi.mabrosim.shoppinglist.data.records;

import com.orm.dsl.Ignore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public PbItemList toPbObject() {
        PbItemList.Builder builder = PbItemList.newBuilder();
        builder.setName(name);

        // XXX hack
        List<Label> labels;
        labels = Label.listAll(Label.class);
        if (getId() != null) {
            builder.setId(getId());
        }
        for (Label label : labels) {
            builder.addLabel(label.toPbObject());
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
