package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.EditText;

import fr.newglace.notedesnazes.R;

public class NewFolder extends Dialog {
    private EditText newFolder;

    public NewFolder(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_new_folder);

        this.newFolder = findViewById(R.id.new_folder);
    }


    public EditText getNewFolder() {
        return newFolder;
    }
    public void build() {
        show();
    }
}