package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.reSize2;

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

        final TextView lockText2 = findViewById(R.id.lock_text2);
        final TextView lockText3 = findViewById(R.id.lock_text3);
        final ImageView imageView5 = findViewById(R.id.imageView5);
        final ImageView imageView6 = findViewById(R.id.imageView6);
        final Space space = findViewById(R.id.space);
        final Space space2 = findViewById(R.id.space2);
        final Space space3 = findViewById(R.id.space3);
        final Space space4 = findViewById(R.id.space4);
        final Space space5 = findViewById(R.id.space5);
        final ImageView imageView = findViewById(R.id.imageView);

        final reSize2 size = new reSize2();
        final DisplayMetrics metrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int phoneWidth = metrics.widthPixels;
        final int phoneHeight = metrics.heightPixels;
        final int passwordWidth = (int) (phoneWidth/5d*3d);
        final int passwordHeight = (int) ((phoneHeight/50d*3d)*0.8d);
        final int spaceSize = Math.min((int) (passwordWidth/20d), (int) (passwordHeight/3d));

        size.reSize2(new Space[]{space, space2, space3, space4, space5}, spaceSize, spaceSize);
        size.reSize2(new View[]{password, delete, favorite}, passwordWidth, passwordHeight);
        size.reSize2(new View[]{lockText, lockText2, lockText3}, passwordWidth, (int) (passwordHeight*0.8d), true, true);
        size.reSize2(new View[]{favoriteColor, imageView6, imageView5}, (int) (spaceSize*1.5d), (int) (spaceSize*1.5d));

        size.reSize2(imageView, (int) (passwordWidth*1.05d), (int) (passwordHeight*3d + spaceSize*4));
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