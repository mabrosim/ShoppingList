package fi.mabrosim.shoppinglist.ui.editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.tasks.DeleteItemListTask;
import fi.mabrosim.shoppinglist.utils.Actions;

public class EditItemListActivity extends Activity implements ButtonClicksListener {

    ItemList mItemList;
    private EditText mEditName;

    private static final int[]         EDIT_TEXT_IDS = {R.id.editName};
    private static final List<Integer> BUTTON_IDS    = Collections.singletonList(R.id.button1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listname);

        if (mItemList == null) {
            Intent intent = getIntent();
            mItemList = ItemList.findById(ItemList.class, intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L));
        }
        mEditName = (EditText) findViewById(R.id.editName);

        for (int i = 0; i < EDIT_TEXT_IDS.length; i++) {
            EditText editText = (EditText) findViewById(EDIT_TEXT_IDS[i]);
            ImageButton imageButton = (ImageButton) findViewById(BUTTON_IDS.get(i));

            editText.addTextChangedListener(new ButtonStateTextWatcher(imageButton));
            imageButton.setTag(R.id.cachedText, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditName.setText(mItemList.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onButtonClick(View view) {
        switch ((ButtonStateTextWatcher.STATE) view.getTag(R.id.buttonState)) {
            case CLEAR: {
                EditText editText = (EditText) findViewById(EDIT_TEXT_IDS[BUTTON_IDS.indexOf(view.getId())]);
                view.setTag(R.id.cachedText, editText.getText().toString());
                editText.setText("");
                break;
            }
            case UNDO: {
                String text = (String) view.getTag(R.id.cachedText);
                view.setTag(R.id.cachedText, "");
                ((EditText) findViewById(EDIT_TEXT_IDS[BUTTON_IDS.indexOf(view.getId())])).setText(text);
                break;
            }
            default:
            case INACTIVE: {
                break;
            }
        }
    }

    @Override
    public void onOkButtonClick(View view) {
        String name = mEditName.getText().toString();
        if (name.isEmpty()) {
            if (mItemList.getId() != null) {
                new DeleteItemListTask(this).execute(mItemList.getId());
            }
            finish();
        } else if (!isNameValid(name)) {
            Toast.makeText(this, R.string.error_name_exists, Toast.LENGTH_SHORT).show();
        } else {
            mItemList.setName(name);
            mItemList.saveAndBroadcast(getApplicationContext());
            finish();
        }
    }

    @Override
    public void onCancelButtonClick(View view) {
        finish();
    }

    private static boolean isNameValid(String name) {
        Iterator<ItemList> listsIterator = ItemList.findAll(ItemList.class);

        while (listsIterator.hasNext()) {
            if (name.equals(listsIterator.next().getName())) {
                return false;
            }
        }
        return true;
    }
}
