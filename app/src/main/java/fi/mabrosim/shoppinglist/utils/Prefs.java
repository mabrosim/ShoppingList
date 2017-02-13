package fi.mabrosim.shoppinglist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {
    private static final String PREFS_NAME = "fi.mabrosim.shoppinglist.prefs";

    private static final String PREF_READY = "READY";

    private Prefs() {
    }

    public static boolean isReady(Context context) {
        return getBoolean(context);
    }

    public static void setReady(Context context) {
        putBoolean(context);
    }

    private static SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void putBoolean(Context context) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putBoolean(Prefs.PREF_READY, true);
        sharedPrefEditor.apply();
    }

    private static boolean getBoolean(Context context) {
        return sharedPreferences(context).getBoolean(Prefs.PREF_READY, false);
    }
}
