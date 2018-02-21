package fi.mabrosim.shoppinglist.tasks;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

import fi.mabrosim.shoppinglist.BuildConfig;
import fi.mabrosim.shoppinglist.utils.FileType;
import fi.mabrosim.shoppinglist.utils.FileUtils;

public class ShareFileTask extends ExportItemsToFileTask {
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".files";

    public ShareFileTask(Context context, FileType fileType) {
        super(context, fileType);
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            shareItemList(file);
            super.onPostExecute(file);
        }
    }

    private void shareItemList(File file) {
        Uri uri = FileProvider.getUriForFile(mAppContext, AUTHORITY, file);
        String filename = file.getName();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(FileUtils.getMimeType(mFileType));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, filename);

        Intent chooserIntent = Intent.createChooser(shareIntent, filename);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mAppContext.startActivity(chooserIntent);
    }
}
