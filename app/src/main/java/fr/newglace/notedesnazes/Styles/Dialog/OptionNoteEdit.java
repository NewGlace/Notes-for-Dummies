package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;

import fr.newglace.notedesnazes.R;

public class OptionNoteEdit extends Dialog {
    private ImageView delete, password, qRCode, loadGallery, loadCamera;

    public OptionNoteEdit(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_options_note_edit);

        this.delete = findViewById(R.id.imageView4);
        this.password = findViewById(R.id.imageView3);
        this.qRCode = findViewById(R.id.create_qr_code);
        this.loadGallery = findViewById(R.id.load_qr_code_gallery);
        this.loadCamera = findViewById(R.id.load_qr_code_camera);
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