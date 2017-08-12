package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.FileUtils;

class ExportItemsToPbFileTask extends AsyncTask<Void, Void, File> {
    final Context mAppContext;

    ExportItemsToPbFileTask(Context context) {
        mAppContext = context;
    }

    @Override
    protected File doInBackground(Void... params) {
        ItemList itemList = ItemList.findCurrentList();

        if (itemList != null) {
            File file = FileUtils.PROTO.newFile(mAppContext);

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
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            Toast.makeText(mAppContext, "Items exported to proto bytes", Toast.LENGTH_SHORT).show();
        }
    }
}
