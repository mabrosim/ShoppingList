package fi.mabrosim.shoppinglist.ui.editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Collections;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Label;
import fi.mabrosim.shoppinglist.tasks.DeleteLabelTask;
import fi.mabrosim.shoppinglist.utils.Actions;

public class EditLabelActivity extends Activity implements ButtonClicksListener {

    Label mLabel;
    private EditText mEditName;

    private static final int[]         EDIT_TEXT_IDS = {R.id.editName};
    private static final List<Integer> BUTTON_IDS    = Collections.singletonList(R.id.button1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_label);

        if (mLabel == null) {
            Intent intent = getIntent();
            mLabel = Label.findById(Label.class, intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L));
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
        mEditName.setText(mLabel.getName());
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
            if (mLabel.getId() != null) {
                new DeleteLabelTask(this).execute(mLabel.getId());
            }
        } else {
            mLabel.setName(name);
            mLabel.saveAndBroadcast(getApplicationContext());
        }
        finish();
    }

    @Override
    public void onCancelButtonClick(View view) {
        finish();
    }
}
