package fi.mabrosim.shoppinglist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {

    private TimeUtils() {
    }

    private static final String HISTORY_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";

    public static Long DateAsLong(Date timestamp) {
        return Long.parseLong(new SimpleDateFormat(HISTORY_TIMESTAMP_FORMAT, Locale.getDefault()).format(timestamp));
    }
}
