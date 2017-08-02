package fi.mabrosim.shoppinglist.utils;

public final class Actions {
    private static final String PREFIX = "fi.mabrosim.shoppinglist";

    public static final String ACTION_ALL_DELETED      = PREFIX + ".action.ALL_DELETED";
    public static final String ACTION_IMPORT_COMPLETED = PREFIX + ".action.IMPORT_COMPLETED";
    public static final String ACTION_ITEM_MODIFIED    = PREFIX + ".action.ITEM_MODIFIED";

    public static final String ACTION_RECORD_UPDATED = PREFIX + ".action.RECORD_UPDATED";
    public static final String ACTION_RECORD_ADDED   = PREFIX + ".action.RECORD_ADDED";
    public static final String ACTION_RECORD_DELETED = PREFIX + ".action.RECORD_DELETED";

    public static final String ACTION_RECORDS_UPDATED = PREFIX + ".action.RECORDS_UPDATED";
    public static final String ACTION_RECORDS_ADDED   = PREFIX + ".action.RECORDS_ADDED";
    public static final String ACTION_RECORDS_DELETED = PREFIX + ".action.RECORDS_DELETED";

    public static final String EXTRA_RECORD_ID   = PREFIX + ".extra.RECORD_ID";
    public static final String EXTRA_RECORD_IDS  = PREFIX + ".extra.RECORD_IDS";
    public static final String EXTRA_RECORD_TYPE = PREFIX + ".extra.RECORD_TYPE";
    public static final String EXTRA_IS_CHECKED  = PREFIX + ".extra.IS_CHECKED";
    public static final String EXTRA_LABEL_NAME  = PREFIX + ".extra.LABEL_NAME";

    private Actions() {
    }
}
