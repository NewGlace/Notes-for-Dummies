package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.EditText;

import fr.newglace.notedesnazes.R;

public class PasswordNoteConfig extends Dialog {
    private EditText password;

    public PasswordNoteConfig(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_password_config);

        this.password = findViewById(R.id.password);
    }


    public EditText getPassword() {
        return password;
    }
    public void build() {
        show();
    }
}