package fi.mabrosim.shoppinglist.ui.editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Arrays;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.utils.Actions;

public class EditItemActivity extends Activity implements ButtonClicksListener {
    Item mItem;
    private EditText mEditName;
    private EditText mEditQuantity;
    private EditText mEditLabelName;

    private static final int[]         EDIT_TEXT_IDS = {R.id.editName, R.id.editQuantity, R.id.editLabelName};
    private static final List<Integer> BUTTON_IDS    = Arrays.asList(R.id.button1, R.id.button2, R.id.button3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        if (mItem == null) {
            Intent intent = getIntent();
            long mItemDbId = intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L);
            mItem = Item.findById(Item.class, mItemDbId);
        }

        mEditName = findViewById(R.id.editName);
        mEditQuantity = findViewById(R.id.editQuantity);
        mEditLabelName = findViewById(R.id.editLabelName);

        for (int i = 0; i < EDIT_TEXT_IDS.length; i++) {
            EditText editText = findViewById(EDIT_TEXT_IDS[i]);
            ImageButton imageButton = findViewById(BUTTON_IDS.get(i));

            editText.addTextChangedListener(new ButtonStateTextWatcher(imageButton));
            imageButton.setTag(R.id.cachedText, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEditName.setText(mItem.getName());
        mEditQuantity.setText(mItem.getQuantity());
        mEditLabelName.setText(mItem.getLabelName());
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
                EditText editText = findViewById(EDIT_TEXT_IDS[BUTTON_IDS.indexOf(view.getId())]);
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
            if (mItem.getId() != null) {
                mItem.deleteAndBroadcast(getApplicationContext());
            }
        } else {
            mItem.setName(name);
            mItem.setQuantity(mEditQuantity.getText().toString());
            mItem.setLabel(mEditLabelName.getText().toString());

            mItem.saveAndBroadcast(getApplicationContext());
        }
        finish();
    }

    @Override
    public void onCancelButtonClick(View view) {
        finish();
    }
}
