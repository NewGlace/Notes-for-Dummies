package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Colors;
import fr.newglace.notedesnazes.Styles.reSize2;

public class OptionMove extends Dialog {
    private ImageView newFolder, backFolder;
    private Spinner moveFolder;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OptionMove(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_options_move);

        backFolder = findViewById(R.id.imageView2);
        newFolder = findViewById(R.id.imageView3);
        moveFolder = findViewById(R.id.spinner);

        final TextView lockText2 = findViewById(R.id.lock_text2);
        final TextView lockText = findViewById(R.id.lock_text);
        final ImageView imageView4 = findViewById(R.id.imageView4);
        final ImageView imageView5 = findViewById(R.id.imageView5);
        final ImageView imageView6 = findViewById(R.id.imageView6);
        final ImageView textView4 = findViewById(R.id.textView4);
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
        size.reSize2(new View[]{imageView4, newFolder, backFolder}, passwordWidth, passwordHeight);
        size.reSize2(new View[]{lockText2, lockText, moveFolder}, passwordWidth - (int) (spaceSize *3.5d), (int) (passwordHeight*0.8d), true, true);
        size.reSize2(new View[]{textView4, imageView6, imageView5}, (int) (spaceSize*1.5d), (int) (spaceSize*1.5d));

        size.reSize2(imageView, (int) (passwordWidth*1.05d), (int) (passwordHeight*3d + spaceSize*4));

        Colors colors = new Colors();
        Drawable draw = activity.getDrawable(R.drawable.bg_notes);
        Drawable draw2 = activity.getDrawable(R.drawable.bg_notes);
        colors.editColor(imageView, 0, draw2);
        colors.editColor(backFolder, 1, draw);
        colors.editColor(newFolder, 1, draw);
        colors.editColor(imageView4, 1, draw);
        colors.editColor(backFolder, 1, draw);
    }

    public ImageView getBackFolder() {
        return backFolder;
    }
    public ImageView getNewFolder() {
        return newFolder;
    }
    public Spinner getMoveFolder() {
        return moveFolder;
    }

    public void build() {
        show();
    }
}