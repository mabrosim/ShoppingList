package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import fi.mabrosim.shoppinglist.BuildConfig;
import fi.mabrosim.shoppinglist.ui.MainActivity;
import fi.mabrosim.shoppinglist.utils.FileUtils;
import fi.mabrosim.shoppinglist.utils.Dog;

public class ImportItemsTaskFactory {
    private static final String TAG = "ImportItemsTaskFactory";

    public static void createAndExecute(Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String fileExt = FileUtils.getFileExt(FileUtils.getFileName(context, uri));

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "createAndExecute: " + mimeType + " " + fileExt);
        if (BuildConfig.DEBUG && FileUtils.JSON_MIME_TYPE.equals(mimeType) && FileUtils.JSON_FILENAME_EXT.equals(fileExt)) {
            Dog.d(TAG, "onActivityResult: import from JSON");
            new ImportItemsFromJsonTask(context, intent).execute(uri);
        } else if (FileUtils.PROTO_MIME_TYPE.equals(mimeType) && FileUtils.PROTO_FILENAME_EXT.equals(fileExt)) {
            Dog.d(TAG, "onActivityResult: import from PB");
            new ImportItemsFromPbTask(context, intent).execute(uri);
        } else if (FileUtils.CSV_MIME_TYPE.equals(mimeType) && FileUtils.CSV_FILENAME_EXT.equals(fileExt)) {
            Dog.d(TAG, "onActivityResult: import from CSV");
            new ImportItemsFromCsvTask(context, intent).execute(uri);
        } else {
            Toast.makeText(context, "File not recognised", Toast.LENGTH_SHORT).show();
        }
    }
}
