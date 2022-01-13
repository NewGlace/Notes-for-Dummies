package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.reSize2;

public class PasswordNoteConfig extends Dialog {
    private EditText password;

    public PasswordNoteConfig(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_password_config);

        password = findViewById(R.id.password);
        TextView textView = findViewById(R.id.textView);
        ImageView imageView = findViewById(R.id.imageView);

        final reSize2 size = new reSize2();
        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int phoneWidth = metrics.widthPixels;
        final int phoneHeight = metrics.heightPixels;
        final int passwordWidth = (int) (phoneWidth/5d*3d);
        final int passwordHeight = (int) ((phoneHeight/50d*3d)*0.8d);

        size.reSize2(password, passwordWidth, passwordHeight, true, true);
        size.reSize2(textView, passwordWidth, (int) (passwordHeight*0.8d), true, true);
        size.reSize2(imageView, (int) (passwordWidth*1.05d), (int) (passwordHeight*2.5d));
    }


    public EditText getPassword() {
        return password;
    }
    public void build() {
        show();
    }
}