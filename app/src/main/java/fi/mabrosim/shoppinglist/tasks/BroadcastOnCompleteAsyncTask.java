package fi.mabrosim.shoppinglist.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import fi.mabrosim.shoppinglist.utils.Dog;

abstract class BroadcastOnCompleteAsyncTask<T> extends AsyncTask<T, Void, Void> {
    private static final String TAG = "BroadcastOnComplete";

    final Context mAppContext;
    final Intent  mIntent;

    BroadcastOnCompleteAsyncTask(Context context, Intent intent) {
        mAppContext = context.getApplicationContext();
        mIntent = intent;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mIntent != null) {
            Dog.d(TAG, "onPostExecute: " + mIntent.getAction());
            LocalBroadcastManager.getInstance(mAppContext).sendBroadcast(mIntent);
        }
    }
}
