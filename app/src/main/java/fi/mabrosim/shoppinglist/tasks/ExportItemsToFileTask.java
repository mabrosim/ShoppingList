package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.FileType;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ExportItemsToFileTask extends AsyncTask<Void, Void, File> {
    final FileType mFileType;
    final Context  mAppContext;

    ExportItemsToFileTask(Context context, FileType fileType) {
        mAppContext = context;
        mFileType = fileType;
    }

    @Override
    protected File doInBackground(Void... voids) {
        ItemList itemList = ItemList.findCurrentList();

        if (itemList != null) {
            return FileUtils.itemsToFile(mAppContext, itemList, mFileType);
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            Toast.makeText(mAppContext, "Items exported to " + mFileType.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
