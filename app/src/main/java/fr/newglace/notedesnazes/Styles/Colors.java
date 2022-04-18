package fr.newglace.notedesnazes.Styles;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Colors {
    private String[] colorList = {"#220f0f", "#341818", "#960045"};
    public Colors() {

    }
    public void editColor(View view, int idColor, Drawable draw) {
        int color = Color.parseColor(colorList[idColor]);
        if (draw != null) {
            draw.setColorFilter(color, PorterDuff.Mode.LIGHTEN);
            view.setBackground(draw);
        } else {
            view.setBackgroundColor(color);
        }
    }
    public String getColor(int idColor) {
        return colorList[idColor];
    }
}
