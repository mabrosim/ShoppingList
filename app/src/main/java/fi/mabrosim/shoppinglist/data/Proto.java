package fi.mabrosim.shoppinglist.data;

import com.google.protobuf.GeneratedMessageLite;

public abstract class Proto<T extends GeneratedMessageLite> implements Protobuf<T> {

    protected Proto() {
    }

    public byte[] toByteArray() {
        return toPbObject().toByteArray();
    }
}
