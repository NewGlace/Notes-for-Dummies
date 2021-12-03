package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.TextView;
import fr.newglace.notedesnazes.R;

public class OptionNote extends Dialog {
    private ImageView delete, password, favorite, favoriteColor, imageView5;
    private TextView lockText;

    public OptionNote(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_options_note);

        delete = findViewById(R.id.imageView4);
        password = findViewById(R.id.imageView3);
        favorite = findViewById(R.id.imageView2);
        favoriteColor = findViewById(R.id.textView4);

        imageView5 = findViewById(R.id.imageView5);
        lockText = findViewById(R.id.lock_text);
    }

    public ImageView getDelete() {
        return delete;
    }

    public ImageView getFavorite() {
        return favorite;
    }

    public ImageView getPassword() {
        return password;
    }

    public ImageView getFavoriteColor() {
        return favoriteColor;
    }

    public ImageView getImageView5() {
        return imageView5;
    }

    public TextView getLockText() {
        return lockText;
    }

    public void build() {
        show();
    }
}