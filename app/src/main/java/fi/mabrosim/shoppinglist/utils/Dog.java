package fi.mabrosim.shoppinglist.utils;

import fi.mabrosim.shoppinglist.BuildConfig;

/**
 * Debug build only logger
 */
public final class Dog {
    private static final boolean LOG = BuildConfig.DEBUG;

    public static void e(String tag, String string, Throwable e) {
        if (LOG) android.util.Log.e(tag, string, e);
    }

    public static void d(String tag, String string) {
        if (LOG) android.util.Log.d(tag, string);
    }
}
