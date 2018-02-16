package fi.mabrosim.shoppinglist.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import fi.mabrosim.shoppinglist.BuildConfig;
import fi.mabrosim.shoppinglist.tasks.ImportItemsTaskFactory;
import fi.mabrosim.shoppinglist.utils.Dog;

public class ImportFromFileActivity extends Activity {
    private static final int    FILE_SELECT_CODE = 0;
    private static final String TAG              = "ImportFromFileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dog.d(TAG, "onCreate: " + getIntent().getAction());
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = intent.getData();

        if (action != null) {
            if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                String scheme = intent.getScheme();
                if (scheme != null && ((BuildConfig.DEBUG && scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) ||
                        (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0))) {
                    ImportItemsTaskFactory.createAndExecute(getApplicationContext(), uri);
                }
            }
            finish();
        } else {
            showFileChooser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    ImportItemsTaskFactory.createAndExecute(getApplicationContext(), uri);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
