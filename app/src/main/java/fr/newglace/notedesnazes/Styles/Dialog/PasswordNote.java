package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.ImageView;

import fr.newglace.notedesnazes.R;

public class PasswordNote extends Dialog {
    private EditText password;

    public PasswordNote(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_password_note);

        this.password = findViewById(R.id.password);
    }

    public EditText getPassword() {
        return password;
    }
    public void build() {
        show();
    }
}