package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Colors;
import fr.newglace.notedesnazes.Styles.reSize2;

public class PasswordNote extends Dialog {
    private EditText password;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PasswordNote(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_password_note);

        password = findViewById(R.id.password);
        final ImageView imageView = findViewById(R.id.imageView);
        final reSize2 size = new reSize2();
        final DisplayMetrics metrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int phoneWidth = metrics.widthPixels;
        final int phoneHeight = metrics.heightPixels;
        final int passwordWidth = (int) (phoneWidth/5d*3d);
        final int passwordHeight = (int) ((phoneHeight/50d*3d)*0.8d);

        size.reSize2(password, passwordWidth, passwordHeight, true, true);
        size.reSize2(imageView, (int) (passwordWidth*1.05d), (int) (passwordHeight*1.05d));

        Colors colors = new Colors();
        Drawable draw = activity.getDrawable(R.drawable.bg_notes);
        Drawable draw2 = activity.getDrawable(R.drawable.bg_notes);
        colors.editColor(imageView, 0, draw2);
        colors.editColor(password, 1, draw);
    }

    public EditText getPassword() {
        return password;
    }
    public void build() {
        show();
    }
}