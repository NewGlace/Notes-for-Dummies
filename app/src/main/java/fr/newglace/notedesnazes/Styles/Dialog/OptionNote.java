package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;

import fr.newglace.notedesnazes.R;

public class OptionNote extends Dialog {
    private ImageView delete, password, favorite, favoriteColor;

    public OptionNote(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_options_note);

        this.delete = findViewById(R.id.imageView4);
        this.password = findViewById(R.id.imageView3);
        this.favorite = findViewById(R.id.imageView2);
        this.favoriteColor = findViewById(R.id.textView4);
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

    public void build() {
        show();
    }
}