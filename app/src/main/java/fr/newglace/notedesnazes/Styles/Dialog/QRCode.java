package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;

import fr.newglace.notedesnazes.R;

public class QRCode extends Dialog {
    private ImageView image;

    public QRCode(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_qrcode_image);

        this.image = findViewById(R.id.qr_code);
    }

    public ImageView getImage() {
        return image;
    }

    public void build() {
        show();
    }
}