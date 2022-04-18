package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Colors;
import fr.newglace.notedesnazes.Styles.reSize2;

public class QRCode extends Dialog {
    private ImageView image;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public QRCode(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_qrcode_image);

        image = findViewById(R.id.qr_code);
        ImageView imageView = findViewById(R.id.imageView);
        reSize2 size = new reSize2();

        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int phoneWidth = metrics.widthPixels;
        int phoneHeight = metrics.heightPixels;
        int qrCodeValue = Math.min((int) (phoneWidth / 2d), (int) (phoneHeight / 3d));
        size.reSize2(imageView, (int) (qrCodeValue * 1.2d), (int) (qrCodeValue * 1.2d));
        size.reSize2(image, qrCodeValue, qrCodeValue);

        Colors colors = new Colors();
        Drawable draw2 = activity.getDrawable(R.drawable.bg_notes);
        colors.editColor(imageView, 0, draw2);
    }

    public ImageView getImage() {
        return image;
    }

    public void build() {
        show();
    }
}