package fi.mabrosim.shoppinglist.data;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.mabrosim.shoppinglist.utils.Dog;

public final class Utils {
    private static final String TAG = "Utils";

    private Utils() {
    }

    public static List<Long> jsonArrayStringToLongList(String jsonArrString) {
        List<Long> list = null;
        if (!TextUtils.isEmpty(jsonArrString)) {
            try {
                list = jsonArrayToLongList(new JSONArray(jsonArrString));
            } catch (JSONException e) {
                Dog.e(TAG, "JSONException", e);
            }
        }
        return list == null ? Collections.<Long>emptyList() : list;
    }

    private static List<Long> jsonArrayToLongList(JSONArray arr) throws JSONException {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(Long.parseLong(arr.getString(i)));
        }
        return list;
    }
}
