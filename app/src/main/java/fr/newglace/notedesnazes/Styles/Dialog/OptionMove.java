package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.Spinner;

import fr.newglace.notedesnazes.R;

public class OptionMove extends Dialog {
    private ImageView newFolder, backFolder;
    private Spinner moveFolder;

    public OptionMove(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_options_move);

        this.backFolder = findViewById(R.id.imageView2);
        this.newFolder = findViewById(R.id.imageView3);
        this.moveFolder = findViewById(R.id.spinner);
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