package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Colors;

public class NewFolder extends Dialog {
    private EditText newFolder;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NewFolder(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.dialog_new_folder);

        this.newFolder = findViewById(R.id.new_folder);
        ImageView imageView = findViewById(R.id.imageView);

        Colors colors = new Colors();
        Drawable draw = activity.getDrawable(R.drawable.bg_notes);
        Drawable draw2 = activity.getDrawable(R.drawable.bg_notes);
        colors.editColor(imageView, 0, draw2);
        colors.editColor(this.newFolder, 1, draw);
    }


    public EditText getNewFolder() {
        return newFolder;
    }
    public void build() {
        show();
    }
}