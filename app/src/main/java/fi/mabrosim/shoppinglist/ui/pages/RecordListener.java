package fi.mabrosim.shoppinglist.ui.pages;

import fi.mabrosim.shoppinglist.data.RecordType;

interface RecordListener {
    void onRecordAdded(RecordType type, long id);

    void onRecordUpdated(RecordType type, long id);

    void onRecordDeleted(RecordType type, long id);
}
