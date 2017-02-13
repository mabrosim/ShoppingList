package fi.mabrosim.shoppinglist.data;

import com.google.protobuf.GeneratedMessageLite;

public abstract class ProtoEntity<T extends GeneratedMessageLite> extends SugarRecordBroadcastable implements Protobuf<T> {

    protected ProtoEntity() {
    }

    public byte[] toByteArray() {
        return toPbObject().toByteArray();
    }
}
