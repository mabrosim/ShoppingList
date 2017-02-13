package fi.mabrosim.shoppinglist.data;

import com.google.protobuf.GeneratedMessageLite;

interface Protobuf<T extends GeneratedMessageLite> {
    T toPbObject();
}
