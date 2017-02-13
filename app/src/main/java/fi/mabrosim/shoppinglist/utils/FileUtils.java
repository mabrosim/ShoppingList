package fi.mabrosim.shoppinglist.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public enum FileUtils {
    JSON,
    PROTO,
    CSV;

    public static final  String PROTO_FILENAME_EXT = "pb";
    public static final  String JSON_FILENAME_EXT  = "json";
    public static final  String CSV_FILENAME_EXT   = "csv";
    public static final  String PROTO_MIME_TYPE    = "application/octet-stream";
    public static final  String JSON_MIME_TYPE     = "application/json";
    public static final  String CSV_MIME_TYPE     = "text/csv";
    private static final String FILENAME_PREFIX    = "items-";
    private static final String TIMESTAMP_FORMAT   = "yyyyMMdd-HHmm";

    public File newFile(Context context) {
        return new File(context.getFilesDir(), currentTimeToFileName());
    }

    private String currentTimeToFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault());

        switch (this) {
            case JSON:
                return FILENAME_PREFIX + sdf.format(new Date()) + "." + JSON_FILENAME_EXT;
            case PROTO:
                return FILENAME_PREFIX + sdf.format(new Date()) + "." + PROTO_FILENAME_EXT;
            case CSV:
                return FILENAME_PREFIX + sdf.format(new Date()) + "." + CSV_FILENAME_EXT;
            default:
                return "";
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }
}
