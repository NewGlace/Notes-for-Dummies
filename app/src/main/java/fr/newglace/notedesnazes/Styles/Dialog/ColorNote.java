package fr.newglace.notedesnazes.Styles.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import fr.newglace.notedesnazes.R;

public class ColorNote extends Dialog {
    private SeekBar r, g, b;
    private ImageView colorPicker;
    private EditText hexColor;
    private TextView valid;

    public ColorNote(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.color_picker);

        this.r = findViewById(R.id.SeekBarRedPickerColor);
        this.g = findViewById(R.id.SeekBarGreenPickerColor);
        this.b = findViewById(R.id.SeekBarBluePickerColor);
        this.colorPicker = findViewById(R.id.colorPicker);
        this.hexColor = findViewById(R.id.hexColor);
        this.valid = findViewById(R.id.valid);
    }

    public SeekBar getR() {
        return r;
    }
    public SeekBar getG() {
        return g;
    }
    public SeekBar getB() {
        return b;
    }
    public TextView getValid() {
        return valid;
    }
    public EditText getHexColor() {
        return hexColor;
    }

    public void editColorPicker() {
        String hex = String.format("#%02x%02x%02x", r.getProgress(), g.getProgress(), b.getProgress());
        int color = Color.parseColor(hex);
        colorPicker.setColorFilter(color);
        hexColor.setText(hex);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editColorHex() {
        String hex = hexColor.getText().toString().replace("#", "");
        int red = Integer.valueOf( hex.length() >= 2 ? hex.substring(0, 2) : "ff", 16);
        int green = Integer.valueOf( hex.length() >= 4 ? hex.substring(2, 4) : "ff", 16);
        int blue = Integer.valueOf( hex.length() >= 6 ? hex.substring(4, 6) : "ff", 16);

        r.setProgress(red);
        g.setProgress(green);
        b.setProgress(blue);

        hex = String.format("#%02x%02x%02x", r.getProgress(), g.getProgress(), b.getProgress());

        int c = Color.parseColor(hex);
        colorPicker.setColorFilter(c);
    }
    public void build() {
        show();
    }
}