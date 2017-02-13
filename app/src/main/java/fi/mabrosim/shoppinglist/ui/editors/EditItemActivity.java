package fi.mabrosim.shoppinglist.ui.editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.data.records.Item;
import fi.mabrosim.shoppinglist.data.records.Label;
import fi.mabrosim.shoppinglist.utils.Actions;
import fi.mabrosim.shoppinglist.utils.Dog;

public class EditItemActivity extends Activity implements OnItemSelectedListener, ButtonClicksListener {
    private static final String TAG = "EditItemActivity";

    Item mItem;
    private EditText mEditName;
    private EditText mEditQuantity;
    private Spinner  mLabelSpinner;

    private List<Label> mLabels;
    private Label       mCurrentLabel;

    private static final int[]         EDIT_TEXT_IDS = {R.id.editName, R.id.editQuantity};
    private static final List<Integer> BUTTON_IDS    = Arrays.asList(R.id.button1, R.id.button2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        if (mItem == null) {
            Intent intent = getIntent();
            long mItemDbId = intent.getLongExtra(Actions.EXTRA_RECORD_ID, 0L);
            mItem = Item.findById(Item.class, mItemDbId);
        }

        mEditName = (EditText) findViewById(R.id.editName);
        mEditQuantity = (EditText) findViewById(R.id.editQuantity);
        mLabelSpinner = (Spinner) findViewById(R.id.spinner);

        setupSpinner();

        for (int i = 0; i < EDIT_TEXT_IDS.length; i++) {
            EditText editText = (EditText) findViewById(EDIT_TEXT_IDS[i]);
            ImageButton imageButton = (ImageButton) findViewById(BUTTON_IDS.get(i));

            editText.addTextChangedListener(new ButtonStateTextWatcher(imageButton));
            imageButton.setTag(R.id.cachedText, "");
        }
    }

    private void setupSpinner() {
        Label itemLabel = mItem.getLabel();
        mLabels = Label.listAll(Label.class);

        if (mLabels.isEmpty()) {
            // TODO think of better place to add the default label
            itemLabel.saveAndBroadcast(this);
            mLabels.add(itemLabel);
        }

        List<String> labelNames = new ArrayList<>();
        for (Label l : mLabels) {
            labelNames.add(l.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_label, labelNames.toArray(new String[labelNames.size()]));
        // Specify the layout to use when the list of choices appears android.R.layout.simple_spinner_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLabelSpinner.setAdapter(adapter);

        if (itemLabel != null) {
            mLabelSpinner.setSelection(labelNames.indexOf(itemLabel.getName()));
        }
        mLabelSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEditName.setText(mItem.getName());
        mEditQuantity.setText(mItem.getQuantity());
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
            if (mItem.getId() != null) {
                mItem.deleteAndBroadcast(getApplicationContext());
            }
        } else {
            mItem.setName(name);
            mItem.setQuantity(mEditQuantity.getText().toString());
            if (mItem.getLabel() == null || !mCurrentLabel.getId().equals(mItem.getLabel().getId())) {
                mItem.setLabel(mCurrentLabel);
            }
            mItem.saveAndBroadcast(getApplicationContext());
        }
        finish();
    }

    @Override
    public void onCancelButtonClick(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Dog.d(TAG, "onItemSelected() called with: " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        mCurrentLabel = mLabels.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Dog.d(TAG, "onNothingSelected: ");
    }
}
