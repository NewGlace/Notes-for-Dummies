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

public class OptionNoteEdit extends Dialog {
    private ImageView delete, password, qRCode, loadGallery, loadCamera, imageView5;
    private TextView lockText;

    public OptionNoteEdit(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_options_note_edit);

        delete = findViewById(R.id.imageView4);
        password = findViewById(R.id.imageView3);
        qRCode = findViewById(R.id.create_qr_code);
        loadGallery = findViewById(R.id.load_qr_code_gallery);
        loadCamera = findViewById(R.id.load_qr_code_camera);
        lockText = findViewById(R.id.lock);
        imageView5 = findViewById(R.id.imageView5);

        final TextView textView2 = findViewById(R.id.textView2);
        final TextView textView3 = findViewById(R.id.textView3);
        final TextView textView5 = findViewById(R.id.textView5);
        final TextView textView6 = findViewById(R.id.textView6);
        final ImageView textView4 = findViewById(R.id.textView4);
        final ImageView imageView6 = findViewById(R.id.imageView6);
        final ImageView imageView7 = findViewById(R.id.imageView7);
        final ImageView imageView8 = findViewById(R.id.imageView8);
        final Space space = findViewById(R.id.space);
        final Space space2 = findViewById(R.id.space2);
        final Space space3 = findViewById(R.id.space3);
        final Space space4 = findViewById(R.id.space4);
        final Space space5 = findViewById(R.id.space5);
        final Space space6 = findViewById(R.id.space6);
        final Space space7 = findViewById(R.id.space7);
        final ImageView imageView = findViewById(R.id.imageView);

        final reSize2 size = new reSize2();
        final DisplayMetrics metrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int phoneWidth = metrics.widthPixels;
        final int phoneHeight = metrics.heightPixels;
        final int passwordWidth = (int) (phoneWidth/5d*3d);
        final int passwordHeight = (int) ((phoneHeight/50d*3d)*0.8d);
        final int spaceSize = Math.min((int) (passwordWidth/20d), (int) (passwordHeight/3d));

        size.reSize2(new Space[]{space, space2, space3, space4, space5, space6, space7}, spaceSize, spaceSize);
        size.reSize2(new View[]{password, delete, qRCode, loadGallery, loadCamera}, passwordWidth, passwordHeight);
        size.reSize2(new View[]{lockText, textView2, textView3, textView5, textView6}, passwordWidth, (int) (passwordHeight*0.8d), true, true);
        size.reSize2(new View[]{textView4, imageView6, imageView7, imageView8, imageView5}, (int) (spaceSize*1.5d), (int) (spaceSize*1.5d));

        size.reSize2(imageView, (int) (passwordWidth*1.05d), (int) (passwordHeight*5d + spaceSize*6));
    }

    public TextView getLockText() {
        return lockText;
    }
    public ImageView getImageView5() {
        return imageView5;
    }

    public ImageView getDelete() {
        return delete;
    }
    public ImageView getQRCode() {
        return qRCode;
    }
    public ImageView getPassword() {
        return password;
    }
    public ImageView getLoadCamera() {
        return loadCamera;
    }
    public ImageView getLoadGallery() {
        return loadGallery;
    }

    public void build() {
        show();
    }
}