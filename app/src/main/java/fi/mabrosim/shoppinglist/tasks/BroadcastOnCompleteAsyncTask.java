package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import fi.mabrosim.shoppinglist.utils.Dog;

abstract class BroadcastOnCompleteAsyncTask<T> extends AsyncTask<T, Void, Intent> {
    private static final String TAG = "BroadcastOnComplete";

    final Context mAppContext;

    BroadcastOnCompleteAsyncTask(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Override
    protected void onPostExecute(Intent intent) {
        if (intent != null) {
            Dog.d(TAG, "onPostExecute: " + intent.getAction());
            LocalBroadcastManager.getInstance(mAppContext).sendBroadcast(intent);
        }
    }
}
