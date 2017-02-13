package fi.mabrosim.shoppinglist.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public final class SingleToast {
    private static final SingleToast sInstance = new SingleToast();

    private Toast mToast;

    private final Handler mHandler = new Handler();

    private final Runnable mClearToastRef = new Runnable() {
        @Override
        public void run() {
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
        }
    };

    private SingleToast() {
    }

    public static SingleToast get() {
        return sInstance;
    }

    @SuppressLint("ShowToast")
    public void showText(Context context, CharSequence text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
        mHandler.removeCallbacks(mClearToastRef);
        mHandler.postDelayed(mClearToastRef, 2500);
    }
}
