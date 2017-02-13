package fi.mabrosim.shoppinglist.ui.editors;

import android.view.View;

@SuppressWarnings("ALL")
interface ButtonClicksListener {

    void onButtonClick(View view);

    void onOkButtonClick(View view);

    void onCancelButtonClick(View view);
}
