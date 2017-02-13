package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

import fi.mabrosim.shoppinglist.BuildConfig;
import fi.mabrosim.shoppinglist.utils.FileUtils;

public class ShareProtoFileTask extends ExportItemsToPbFileTask {
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".files";

    public ShareProtoFileTask(Context context) {
        super(context);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        shareItemList(mAppContext, file);
    }

    private static void shareItemList(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, AUTHORITY, file);

        String protoFilename = file.getName();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(FileUtils.PROTO_MIME_TYPE);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, protoFilename);

        Intent chooserIntent = Intent.createChooser(shareIntent, protoFilename);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);
    }
}
