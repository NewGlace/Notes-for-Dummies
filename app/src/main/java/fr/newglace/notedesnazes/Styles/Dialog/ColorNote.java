package fr.newglace.notedesnazes.Styles.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import fr.newglace.notedesnazes.Activity.NoteActivity;
import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Colors;
import fr.newglace.notedesnazes.Styles.reSize2;

public class ColorNote extends Dialog {
    private ImageView colorPicker, imageView, hue, hueSelect, colorSelect;
    private EditText hexColor;
    private TextView valid;
    private Activity activity;
    private Space space8, space9, space10;
    private int colorPickerWidth;
    private int colorPickerHeight;
    private Paint paint;
    private Shader shader;
    private final float[] color = { 1.f, 1.f, 1.f };
    private float space;
    private float cursorSize;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ColorNote(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.color_picker);

        this.activity = activity;
        colorPicker = findViewById(R.id.colorPicker);
        hexColor = findViewById(R.id.hexColor);
        valid = findViewById(R.id.valid);
        imageView = findViewById(R.id.imageView);
        space8 = findViewById(R.id.space8);
        space9 = findViewById(R.id.space9);
        space10 = findViewById(R.id.space10);
        hue = findViewById(R.id.hue);
        hueSelect = findViewById(R.id.hue_select);
        colorSelect = findViewById(R.id.color_select);
        reSize();
        editColorPickerHue(1.f);
        hueSelect.setX((float) (hue.getMeasuredWidth() / 360d * 0.001f));

        Colors colors = new Colors();
        Drawable draw = activity.getDrawable(R.drawable.bg_notes);
        Drawable draw2 = activity.getDrawable(R.drawable.bg_notes);
        colors.editColor(imageView, 0, draw2);
        colors.editColor(hexColor, 1, draw);
        colors.editColor(valid, 1, draw);
    }
    public TextView getValid() {
        return valid;
    }

    public EditText getHexColor() {
        return hexColor;
    }

    public void editColorPicker() {
        int red = Color.red(getColor());
        int green = Color.green(getColor());
        int blue = Color.blue(getColor());

        colorSelect.setColorFilter(Color.rgb(255-red, 255-green, 255-blue));

        String hex = String.format("#%02x%02x%02x", red, green, blue);
        hexColor.setText(hex);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String editColorHex() {
        String hex = hexColor.getText().toString().replace("#", "");
        int red = Integer.valueOf( hex.length() >= 2 ? hex.substring(0, 2) : "ff", 16);
        int green = Integer.valueOf( hex.length() >= 4 ? hex.substring(2, 4) : "ff", 16);
        int blue = Integer.valueOf( hex.length() >= 6 ? hex.substring(4, 6) : "ff", 16);

        hex = String.format("#%02x%02x%02x", red, green, blue);

        Color.RGBToHSV(red, green, blue, color);
        editColorPickerHue(color[0]);

        float hueFloat = 360.f / hue.getMeasuredWidth() * color[0];
        if (hueFloat == 360.f) hueFloat = 0.f;
        editColorPickerHue(hueFloat);

        hueSelect.setX((float) (hue.getMeasuredWidth() / 360d * hueFloat)+space);
        float x = colorPicker.getMeasuredWidth()*color[1]*1.f;
        float y = (colorPicker.getMeasuredHeight()*color[2]*1.f)+1f;

        if (x < colorPicker.getX()) x = colorPicker.getX();
        if (y < colorPicker.getY()) y = colorPicker.getY();
        if (x > colorPicker.getMeasuredWidth() + colorPicker.getX() - cursorSize)
            x = colorPicker.getMeasuredWidth() + colorPicker.getX() - 0.001f - cursorSize;
        if (y > colorPicker.getMeasuredHeight() + colorPicker.getY() - cursorSize)
            y = colorPicker.getMeasuredHeight() +colorPicker.getY() - 0.001f - cursorSize;

        colorSelect.setX(x);
        colorSelect.setY(y);

        return hex;
    }

    public void build() {
        show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void editColorPickerHue(float f) {
        float[] color = {f, 1.f, 1.f};
        this.color[0] = f;

        Bitmap bitmap = Bitmap.createBitmap(colorPickerWidth, colorPickerHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.FILL);
        Colors colors = new Colors();
        strokePaint.setColor(Color.parseColor(colors.getColor(1)));
        if (paint == null) {
            paint = new Paint();
            shader = new LinearGradient(5.f, 5.f, 5.f, colorPickerHeight-5, 0xffffffff, 0xff000000, Shader.TileMode.CLAMP);
        }
        int rgb = Color.HSVToColor(color);
        Shader shader2 = new LinearGradient(5.f, 5.f, colorPickerWidth-5, 5.f, 0xffffffff, rgb, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(shader, shader2, PorterDuff.Mode.MULTIPLY);
        paint.setShader(composeShader);
        canvas.drawRoundRect(0.f, 0.f, colorPickerWidth, colorPickerHeight, 10, 10, strokePaint);
        canvas.drawRoundRect(5.f, 5.f, colorPickerWidth-5, colorPickerHeight-5, 10, 10, paint);
        BitmapDrawable drawable = new BitmapDrawable(activity.getResources(), bitmap);
        colorPicker.setBackground(drawable);
    }

    private int getColor() {
        final int argb = Color.HSVToColor(color);
        return (argb & 0x00ffffff);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    public void update(NoteActivity activity, String type) {
        hue.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float x = event.getX();
                if (x < 0.f) x = 0.f;
                if (x > hue.getMeasuredWidth()) x = hue.getMeasuredWidth() - 0.001f;

                float hueFloat = 360.f / hue.getMeasuredWidth() * x;
                if (hueFloat == 360.f) hueFloat = 0.f;

                editColorPickerHue(hueFloat);
                hueSelect.setX((float) (hue.getMeasuredWidth() / 360d * hueFloat)+space);
                editColorPicker();
                activity.editSpan2(type, hexColor.getText().toString());

                return true;
            }
            return false;
        });
        colorPicker.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float x = event.getX();
                float y = event.getY();

                if (x < colorPicker.getX()) x = colorPicker.getX();
                if (y < colorPicker.getY()) y = colorPicker.getY();
                if (x > colorPicker.getMeasuredWidth() + colorPicker.getX() - cursorSize)
                    x = colorPicker.getMeasuredWidth() + colorPicker.getX() - 0.001f - cursorSize;
                if (y > colorPicker.getMeasuredHeight() + colorPicker.getY() - cursorSize)
                    y = colorPicker.getMeasuredHeight() +colorPicker.getY() - 0.001f - cursorSize;

                colorSelect.setX(x);
                colorSelect.setY(y);

                x = event.getX();
                y = event.getY();

                if (x < 0.f) x = 0.f;
                if (x > colorPicker.getMeasuredWidth()) x = colorPicker.getMeasuredWidth();
                if (y < 0.f) y = 0.f;
                if (y > colorPicker.getMeasuredHeight()) y = colorPicker.getMeasuredHeight();

                color[1] = (1.f / colorPicker.getMeasuredWidth() * x);
                color[2] = (1.f - (1.f / colorPicker.getMeasuredHeight() * y));

                editColorPicker();
                activity.editSpan2(type, hexColor.getText().toString());
                return true;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void reSize() {
        reSize2 size = new reSize2();

        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int phoneWidth = metrics.widthPixels;
        int phoneHeight = metrics.heightPixels;

        colorPicker.setScaleType(ImageView.ScaleType.FIT_XY);
        colorSelect.setScaleType(ImageView.ScaleType.FIT_XY);
        cursorSize = 18f;

        space = (float) (((phoneWidth/10d*7d) - (int) (phoneWidth/5d*3d)) / 2d - ((phoneWidth/25d)/2));
        size.reSize2(imageView, (int) (phoneWidth/10d*7d), (int) (phoneHeight/19d*7d));
        size.reSize2(colorPicker, (int) (phoneWidth/5d*3d), (int) (phoneHeight/16d*3d));
        size.reSize2(valid, (int) (phoneWidth/5d), (int) (phoneHeight/20d), true, true);
        size.reSize2(hexColor, (int) (phoneWidth/5d), (int) (phoneHeight/20d), true, true);
        size.reSize2(new View[]{space8, space9, space10}, 0, (int) (phoneHeight/40d));
        size.reSize2(hue, (int) (phoneWidth/5d*3d), (int) (phoneHeight/30d));
        size.reSize2(hueSelect, (int) (phoneWidth/25d), (int) (phoneHeight/30d));
        size.reSize2(colorSelect, (int) cursorSize, (int) cursorSize);
        colorPickerWidth = (int) (phoneWidth/5d*3d);
        colorPickerHeight = (int) (phoneHeight/16d*3d);
    }
}