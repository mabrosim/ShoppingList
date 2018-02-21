package fi.mabrosim.shoppinglist.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fi.mabrosim.shoppinglist.data.Label;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.ItemList;

public final class FileUtils {

    public static final  String PROTO_FILENAME_EXT = "pb";
    public static final  String JSON_FILENAME_EXT  = "json";
    public static final  String CSV_FILENAME_EXT   = "csv";
    public static final  String PROTO_MIME_TYPE    = "application/octet-stream";
    public static final  String JSON_MIME_TYPE     = "application/json";
    public static final  String CSV_MIME_TYPE      = "text/csv";

    private static final String FILENAME_PREFIX    = "items-";
    private static final String TIMESTAMP_FORMAT   = "yyyyMMdd-HHmm";


    private FileUtils() {
    }

    public static String getBase(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static String getFileName(ContentResolver contentResolver, Uri uri) {
        String result = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
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

    public static String currentTimeToFileName(FileType type) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault());

        switch (type) {
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

    public static File itemsToFile(Context context, ItemList itemList, FileType type) {
        switch (type) {
            case JSON:
                return itemsToJson(context, itemList);
            case PROTO:
                return itemsToProto(context, itemList);
            case CSV:
                return itemsToCsv(context, itemList);
            default:
                return null;
        }
    }

    public static String getMimeType(FileType type) {
        switch (type) {
            case JSON:
                return FileUtils.JSON_MIME_TYPE;
            case PROTO:
                return FileUtils.PROTO_MIME_TYPE;
            case CSV:
                return FileUtils.CSV_MIME_TYPE;
            default:
                return "";
        }
    }

    private static File itemsToProto(Context context, ItemList itemList) {
        File file = new File(context.getFilesDir(), currentTimeToFileName(FileType.PROTO));

        if (itemList.getName().isEmpty() || itemList.getName().equals("List")) {
            itemList.setName(FileUtils.getBase(file.getName()));
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(itemList.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private static File itemsToJson(Context context, ItemList itemList) {
        List<Item> items = itemList.findItems();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(items);

        File file = new File(context.getFilesDir(), currentTimeToFileName(FileType.JSON));
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    private static File itemsToCsv(Context context, ItemList itemList) {
        ByteArrayInputStream in = new ByteArrayInputStream(itemList.toPbObject().toByteArray());
        try {
            itemList = new ItemList(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String[]> labeledItemNames = new ArrayList<>();
        for (Label l : itemList.getLabels()) {
            labeledItemNames.add(l.getItemNamesWithLabel());
        }

        File file = new File(context.getFilesDir(), currentTimeToFileName(FileType.CSV));
        try {
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file),
                    Charset.forName("UTF-8")),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            for (String[] a : CsvUtils.transpose2dArrayToMatrix(labeledItemNames.toArray(new String[labeledItemNames.size()][]))) {
                writer.writeNext(a);
            }
            writer.close();
        } catch (IOException e) {
            //error
        }
        return file;
    }
}
